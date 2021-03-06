/*
 * Copyright 2018 The MQTT Bee project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.mqttbee.mqtt.handler.auth;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.api.mqtt.mqtt5.Mqtt5ClientData;
import org.mqttbee.api.mqtt.mqtt5.auth.Mqtt5EnhancedAuthProvider;
import org.mqttbee.api.mqtt.mqtt5.exceptions.Mqtt5MessageException;
import org.mqttbee.api.mqtt.mqtt5.message.auth.Mqtt5Auth;
import org.mqttbee.api.mqtt.mqtt5.message.auth.Mqtt5AuthBuilder;
import org.mqttbee.api.mqtt.mqtt5.message.auth.Mqtt5AuthReasonCode;
import org.mqttbee.api.mqtt.mqtt5.message.disconnect.Mqtt5DisconnectReasonCode;
import org.mqttbee.mqtt.MqttClientData;
import org.mqttbee.mqtt.datatypes.MqttUTF8StringImpl;
import org.mqttbee.mqtt.handler.disconnect.MqttDisconnectUtil;
import org.mqttbee.mqtt.handler.util.ChannelInboundHandlerWithTimeout;
import org.mqttbee.mqtt.message.auth.MqttAuth;
import org.mqttbee.mqtt.message.auth.MqttAuthBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mqttbee.api.mqtt.mqtt5.message.auth.Mqtt5AuthReasonCode.CONTINUE_AUTHENTICATION;

/**
 * Base for enhanced auth handling according to the MQTT 5 specification.
 *
 * @author Silvio Giebl
 */
abstract class AbstractMqttAuthHandler extends ChannelInboundHandlerWithTimeout {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMqttAuthHandler.class);

    /**
     * Utility method to get a builder for a new AUTH message.
     *
     * @param reasonCode           the reason code for the AUTH message.
     * @param enhancedAuthProvider the enhanced auth provider for the new AUTH message.
     * @return a builder for a new AUTH message.
     */
    @NotNull
    static MqttAuthBuilder getAuthBuilder(
            @NotNull final Mqtt5AuthReasonCode reasonCode,
            @NotNull final Mqtt5EnhancedAuthProvider enhancedAuthProvider) {

        return new MqttAuthBuilder(reasonCode, (MqttUTF8StringImpl) enhancedAuthProvider.getMethod());
    }

    static boolean enhancedAuthProviderAccepted(@Nullable final Throwable throwable) {
        if (throwable != null) {
            LOGGER.error("auth cancelled because of an unexpected exception", throwable);
            return false;
        }
        return true;
    }

    static boolean enhancedAuthProviderAccepted(@Nullable final Boolean accepted, @Nullable final Throwable throwable) {
        if (throwable != null) {
            LOGGER.error("auth cancelled because of an unexpected exception", throwable);
            return false;
        } else if (accepted == null) {
            LOGGER.error("auth cancelled because of an unexpected null value");
            return false;
        }
        return accepted;
    }

    final MqttClientData clientData;
    Mqtt5EnhancedAuthProvider enhancedAuthProvider;

    AbstractMqttAuthHandler(@NotNull final MqttClientData clientData) {
        this.clientData = clientData;
    }

    /**
     * Handles an incoming AUTH message. Sends a DISCONNECT message if the AUTH message is not valid.
     *
     * @param ctx  the channel handler context.
     * @param auth the incoming AUTH message.
     */
    final void readAuth(@NotNull final ChannelHandlerContext ctx, @NotNull final MqttAuth auth) {
        cancelTimeout();

        if (validateAuth(ctx, auth)) {
            switch (auth.getReasonCode()) {
                case CONTINUE_AUTHENTICATION:
                    readAuthContinue(ctx, auth);
                    break;
                case SUCCESS:
                    readAuthSuccess(ctx, auth);
                    break;
                case REAUTHENTICATE:
                    readReAuth(ctx, auth);
                    break;
            }
        }
    }

    /**
     * Validates an incoming AUTH message.
     * <p>
     * If validation fails, disconnection and closing of the channel is already handled.
     *
     * @param ctx  the channel handler context.
     * @param auth the incoming AUTH message.
     * @return true if the AUTH message is valid, otherwise false.
     */
    private boolean validateAuth(@NotNull final ChannelHandlerContext ctx, @NotNull final MqttAuth auth) {
        if (!auth.getMethod().equals(enhancedAuthProvider.getMethod())) {
            MqttDisconnectUtil.disconnect(ctx.channel(), Mqtt5DisconnectReasonCode.PROTOCOL_ERROR,
                    new Mqtt5MessageException(auth, "Auth method must be the same as in the CONNECT message"));
            return false;
        }
        return true;
    }

    /**
     * Handles an incoming AUTH message with the Reason Code CONTINUE AUTHENTICATION.
     * <ul>
     * <li>Calls {@link Mqtt5EnhancedAuthProvider#onContinue(Mqtt5ClientData, Mqtt5Auth, Mqtt5AuthBuilder)}.</li>
     * <li>Sends a new AUTH message if the enhanced auth provider accepted the incoming AUTH message.</li>
     * <li>Otherwise sends a DISCONNECT message.</li>
     * </ul>
     *
     * @param ctx  the channel handler context.
     * @param auth the received AUTH message.
     */
    private void readAuthContinue(@NotNull final ChannelHandlerContext ctx, @NotNull final MqttAuth auth) {
        final MqttAuthBuilder authBuilder = getAuthBuilder(CONTINUE_AUTHENTICATION, enhancedAuthProvider);

        enhancedAuthProvider.onContinue(clientData, auth, authBuilder).whenCompleteAsync((accepted, throwable) -> {
            if (enhancedAuthProviderAccepted(accepted, throwable)) {
                ctx.writeAndFlush(authBuilder.build()).addListener(this);
            } else {
                MqttDisconnectUtil.disconnect(ctx.channel(), Mqtt5DisconnectReasonCode.NOT_AUTHORIZED,
                        new Mqtt5MessageException(auth, "Server auth not accepted"));
            }
        }, ctx.executor());
    }

    /**
     * Disconnects on an incoming AUTH message with the Reason Code SUCCESS.
     *
     * @param ctx  the channel handler context.
     * @param auth the incoming AUTH message.
     */
    abstract void readAuthSuccess(@NotNull ChannelHandlerContext ctx, @NotNull MqttAuth auth);

    /**
     * Disconnects on an incoming AUTH message with the Reason Code REAUTHENTICATE.
     *
     * @param ctx  the channel handler context.
     * @param auth the incoming AUTH message.
     */
    abstract void readReAuth(@NotNull ChannelHandlerContext ctx, @NotNull MqttAuth auth);

    @Override
    protected final long getTimeout(@NotNull final ChannelHandlerContext ctx) {
        return enhancedAuthProvider.getTimeout();
    }

    @NotNull
    @Override
    protected final Mqtt5DisconnectReasonCode getTimeoutReasonCode() {
        return Mqtt5DisconnectReasonCode.NOT_AUTHORIZED;
    }

}
