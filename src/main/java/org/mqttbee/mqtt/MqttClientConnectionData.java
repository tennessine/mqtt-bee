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

package org.mqttbee.mqtt;

import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.api.mqtt.datatypes.MqttUTF8String;
import org.mqttbee.api.mqtt.mqtt3.Mqtt3ClientConnectionData;
import org.mqttbee.api.mqtt.mqtt5.Mqtt5ClientConnectionData;
import org.mqttbee.api.mqtt.mqtt5.auth.Mqtt5EnhancedAuthProvider;
import org.mqttbee.mqtt.datatypes.MqttTopicImpl;
import org.mqttbee.mqtt.datatypes.MqttVariableByteInteger;
import org.mqttbee.util.collections.IntMap;

import java.util.Optional;

/**
 * @author Silvio Giebl
 */
public class MqttClientConnectionData implements Mqtt5ClientConnectionData, Mqtt3ClientConnectionData {

    private int keepAlive;
    private long sessionExpiryInterval;
    private final int receiveMaximum;
    private final int topicAliasMaximum;
    private final IntMap<MqttTopicImpl> topicAliasMapping;
    private final int maximumPacketSize;
    private final int subscriptionIdentifierMaximum;
    private final Mqtt5EnhancedAuthProvider enhancedAuthProvider;
    private final boolean hasWillPublish;
    private final boolean problemInformationRequested;
    private final boolean responseInformationRequested;
    private final Channel channel;

    public MqttClientConnectionData(
            final int keepAlive, final long sessionExpiryInterval, final int receiveMaximum,
            final int topicAliasMaximum, final int maximumPacketSize,
            @Nullable final Mqtt5EnhancedAuthProvider enhancedAuthProvider, final boolean hasWillPublish,
            final boolean problemInformationRequested, final boolean responseInformationRequested,
            @NotNull final Channel channel) {

        this.keepAlive = keepAlive;
        this.sessionExpiryInterval = sessionExpiryInterval;
        this.receiveMaximum = receiveMaximum;
        this.topicAliasMaximum = topicAliasMaximum;
        this.topicAliasMapping = (topicAliasMaximum == 0) ? null : IntMap.range(1, topicAliasMaximum);
        this.maximumPacketSize = maximumPacketSize;
        this.subscriptionIdentifierMaximum =
                MqttVariableByteInteger.FOUR_BYTES_MAX_VALUE; // TODO CONNECT + CONNACK user properties
        this.enhancedAuthProvider = enhancedAuthProvider;
        this.hasWillPublish = hasWillPublish;
        this.problemInformationRequested = problemInformationRequested;
        this.responseInformationRequested = responseInformationRequested;
        this.channel = channel;
    }

    @Override
    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(final int keepAlive) {
        this.keepAlive = keepAlive;
    }

    @Override
    public long getSessionExpiryInterval() {
        return sessionExpiryInterval;
    }

    public void setSessionExpiryInterval(final long sessionExpiryInterval) {
        this.sessionExpiryInterval = sessionExpiryInterval;
    }

    @Override
    public int getReceiveMaximum() {
        return receiveMaximum;
    }

    @Override
    public int getTopicAliasMaximum() {
        return topicAliasMaximum;
    }

    @Nullable
    public IntMap<MqttTopicImpl> getTopicAliasMapping() {
        return topicAliasMapping;
    }

    @Override
    public int getSubscriptionIdentifierMaximum() {
        return subscriptionIdentifierMaximum;
    }

    @Override
    public int getMaximumPacketSize() {
        return maximumPacketSize;
    }

    @NotNull
    @Override
    public Optional<MqttUTF8String> getAuthMethod() {
        return (enhancedAuthProvider == null) ? Optional.empty() : Optional.of(enhancedAuthProvider.getMethod());
    }

    @Nullable
    public Mqtt5EnhancedAuthProvider getEnhancedAuthProvider() {
        return enhancedAuthProvider;
    }

    @Override
    public boolean hasWillPublish() {
        return hasWillPublish;
    }

    @Override
    public boolean isProblemInformationRequested() {
        return problemInformationRequested;
    }

    @Override
    public boolean isResponseInformationRequested() {
        return responseInformationRequested;
    }

    public Channel getChannel() {
        return channel;
    }

}
