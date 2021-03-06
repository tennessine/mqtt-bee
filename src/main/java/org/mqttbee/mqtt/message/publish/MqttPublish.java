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

package org.mqttbee.mqtt.message.publish;

import com.google.common.primitives.ImmutableIntArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.api.mqtt.datatypes.MqttQos;
import org.mqttbee.api.mqtt.datatypes.MqttTopic;
import org.mqttbee.api.mqtt.datatypes.MqttUTF8String;
import org.mqttbee.api.mqtt.mqtt5.message.publish.Mqtt5PayloadFormatIndicator;
import org.mqttbee.api.mqtt.mqtt5.message.publish.Mqtt5Publish;
import org.mqttbee.api.mqtt.mqtt5.message.publish.TopicAliasUsage;
import org.mqttbee.mqtt.datatypes.MqttTopicImpl;
import org.mqttbee.mqtt.datatypes.MqttUTF8StringImpl;
import org.mqttbee.mqtt.datatypes.MqttUserPropertiesImpl;
import org.mqttbee.mqtt.message.MqttMessageWithUserProperties.MqttMessageWithUserPropertiesImpl;
import org.mqttbee.util.ByteBufferUtil;

import javax.annotation.concurrent.Immutable;
import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * @author Silvio Giebl
 */
@Immutable
public class MqttPublish extends MqttMessageWithUserPropertiesImpl implements Mqtt5Publish {

    public static final long MESSAGE_EXPIRY_INTERVAL_INFINITY = Long.MAX_VALUE;

    private final MqttTopicImpl topic;
    private final ByteBuffer payload;
    private final MqttQos qos;
    private final boolean isRetain;
    private final long messageExpiryInterval;
    private final Mqtt5PayloadFormatIndicator payloadFormatIndicator;
    private final MqttUTF8StringImpl contentType;
    private final MqttTopicImpl responseTopic;
    private final ByteBuffer correlationData;
    private final TopicAliasUsage topicAliasUsage;

    public MqttPublish(
            @NotNull final MqttTopicImpl topic, @Nullable final ByteBuffer payload, @NotNull final MqttQos qos,
            final boolean isRetain, final long messageExpiryInterval,
            @Nullable final Mqtt5PayloadFormatIndicator payloadFormatIndicator,
            @Nullable final MqttUTF8StringImpl contentType, @Nullable final MqttTopicImpl responseTopic,
            @Nullable final ByteBuffer correlationData, @NotNull final TopicAliasUsage topicAliasUsage,
            @NotNull final MqttUserPropertiesImpl userProperties) {

        super(userProperties);
        this.topic = topic;
        this.payload = payload;
        this.qos = qos;
        this.isRetain = isRetain;
        this.messageExpiryInterval = messageExpiryInterval;
        this.payloadFormatIndicator = payloadFormatIndicator;
        this.contentType = contentType;
        this.responseTopic = responseTopic;
        this.correlationData = correlationData;
        this.topicAliasUsage = topicAliasUsage;
    }

    @NotNull
    @Override
    public MqttTopicImpl getTopic() {
        return topic;
    }

    @NotNull
    @Override
    public Optional<ByteBuffer> getPayload() {
        return ByteBufferUtil.optionalReadOnly(payload);
    }

    @Nullable
    public ByteBuffer getRawPayload() {
        return payload;
    }

    @NotNull
    @Override
    public byte[] getPayloadAsBytes() {
        if (payload == null) {
            return new byte[0];
        }
        return ByteBufferUtil.getBytes(payload);
    }

    @NotNull
    @Override
    public MqttQos getQos() {
        return qos;
    }

    @Override
    public boolean isRetain() {
        return isRetain;
    }

    @NotNull
    @Override
    public Optional<Long> getMessageExpiryInterval() {
        return (messageExpiryInterval == MESSAGE_EXPIRY_INTERVAL_INFINITY) ? Optional.empty() :
                Optional.of(messageExpiryInterval);
    }

    public long getRawMessageExpiryInterval() {
        return messageExpiryInterval;
    }

    @NotNull
    @Override
    public Optional<Mqtt5PayloadFormatIndicator> getPayloadFormatIndicator() {
        return Optional.ofNullable(payloadFormatIndicator);
    }

    @Nullable
    public Mqtt5PayloadFormatIndicator getRawPayloadFormatIndicator() {
        return payloadFormatIndicator;
    }

    @NotNull
    @Override
    public Optional<MqttUTF8String> getContentType() {
        return Optional.ofNullable(contentType);
    }

    @Nullable
    public MqttUTF8StringImpl getRawContentType() {
        return contentType;
    }

    @NotNull
    @Override
    public Optional<MqttTopic> getResponseTopic() {
        return Optional.ofNullable(responseTopic);
    }

    @Nullable
    public MqttTopicImpl getRawResponseTopic() {
        return responseTopic;
    }

    @NotNull
    @Override
    public Optional<ByteBuffer> getCorrelationData() {
        return ByteBufferUtil.optionalReadOnly(correlationData);
    }

    @Nullable
    public ByteBuffer getRawCorrelationData() {
        return correlationData;
    }

    @NotNull
    @Override
    public TopicAliasUsage usesTopicAlias() {
        return topicAliasUsage;
    }

    public MqttStatefulPublish createStateful(
            final int packetIdentifier, final boolean isDup, final int topicAlias, final boolean isNewTopicAlias,
            @NotNull final ImmutableIntArray subscriptionIdentifiers) {

        return new MqttStatefulPublish(
                this, packetIdentifier, isDup, topicAlias, isNewTopicAlias, subscriptionIdentifiers);
    }

}
