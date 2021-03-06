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

package org.mqttbee.api.mqtt.mqtt3;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.api.mqtt.*;
import org.mqttbee.api.mqtt.datatypes.MqttClientIdentifier;
import org.mqttbee.api.mqtt.mqtt5.Mqtt5ClientBuilder;
import org.mqttbee.mqtt.MqttClientData;
import org.mqttbee.mqtt.MqttClientExecutorConfigImpl;
import org.mqttbee.mqtt.MqttVersion;
import org.mqttbee.mqtt.datatypes.MqttClientIdentifierImpl;
import org.mqttbee.mqtt.mqtt3.Mqtt3ClientView;
import org.mqttbee.mqtt.mqtt5.Mqtt5ClientImpl;

/**
 * @author Silvio Giebl
 */
public class Mqtt3ClientBuilder extends MqttClientBuilder {

    public Mqtt3ClientBuilder(
            @NotNull final MqttClientIdentifierImpl identifier, @NotNull final String serverHost, final int serverPort,
            @Nullable final MqttClientSslConfig sslConfig, @Nullable final MqttWebSocketConfig webSocketConfig,
            @NotNull final MqttClientExecutorConfigImpl executorConfig) {

        Preconditions.checkNotNull(identifier, "Identifier must not be null.");
        Preconditions.checkNotNull(serverHost, "Server host must not be null.");
        Preconditions.checkNotNull(executorConfig, "Executor config must not be null.");

        this.identifier = identifier;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.sslConfig = sslConfig;
        this.webSocketConfig = webSocketConfig;
        this.executorConfig = executorConfig;
    }

    @NotNull
    @Override
    public Mqtt3ClientBuilder identifier(@NotNull final String identifier) {
        super.identifier(identifier);
        return this;
    }

    @NotNull
    @Override
    public Mqtt3ClientBuilder identifier(@NotNull final MqttClientIdentifier identifier) {
        super.identifier(identifier);
        return this;
    }

    @NotNull
    @Override
    public Mqtt3ClientBuilder serverHost(@NotNull final String host) {
        super.serverHost(host);
        return this;
    }

    @NotNull
    @Override
    public Mqtt3ClientBuilder serverPort(final int port) {
        super.serverPort(port);
        return this;
    }

    @NotNull
    @Override
    public Mqtt3ClientBuilder useSslWithDefaultConfig() {
        super.useSslWithDefaultConfig();
        return this;
    }

    @NotNull
    @Override
    public Mqtt3ClientBuilder useSsl(@NotNull final MqttClientSslConfig sslConfig) {
        super.useSsl(sslConfig);
        return this;
    }

    @NotNull
    @Override
    public MqttClientSslConfigBuilder<? extends Mqtt3ClientBuilder> useSsl() {
        return new MqttClientSslConfigBuilder<>(this::useSsl);
    }

    @NotNull
    @Override
    public Mqtt3ClientBuilder useWebSocketWithDefaultConfig() {
        super.useWebSocketWithDefaultConfig();
        return this;
    }

    @NotNull
    @Override
    public Mqtt3ClientBuilder useWebSocket(@NotNull final MqttWebSocketConfig webSocketConfig) {
        super.useWebSocket(webSocketConfig);
        return this;
    }

    @NotNull
    @Override
    public MqttWebSocketConfigBuilder<? extends Mqtt3ClientBuilder> useWebSocket() {
        return new MqttWebSocketConfigBuilder<>(this::useWebSocket);
    }

    @NotNull
    @Override
    public Mqtt3ClientBuilder executorConfig(@NotNull final MqttClientExecutorConfig executorConfig) {
        super.executorConfig(executorConfig);
        return this;
    }

    @NotNull
    @Override
    public MqttClientExecutorConfigBuilder<? extends Mqtt3ClientBuilder> executorConfig() {
        return new MqttClientExecutorConfigBuilder<>(this::executorConfig);
    }

    @NotNull
    @Override
    public Mqtt3ClientBuilder useMqttVersion3() {
        return this;
    }

    @NotNull
    @Override
    public Mqtt5ClientBuilder useMqttVersion5() {
        throw new UnsupportedOperationException(
                "Switching MQTT Version is not allowed. Please call useMqttVersion3/5 only once.");
    }

    @NotNull
    public Mqtt3Client buildReactive() {
        return new Mqtt3ClientView(new Mqtt5ClientImpl(buildClientData()));
    }

    @NotNull
    private MqttClientData buildClientData() {
        return new MqttClientData(MqttVersion.MQTT_3_1_1, identifier, serverHost, serverPort, sslConfig,
                webSocketConfig, false, false, executorConfig, null);
    }

}
