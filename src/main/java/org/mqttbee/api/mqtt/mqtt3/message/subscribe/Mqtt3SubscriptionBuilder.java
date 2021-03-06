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

package org.mqttbee.api.mqtt.mqtt3.message.subscribe;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.api.mqtt.datatypes.MqttQos;
import org.mqttbee.api.mqtt.datatypes.MqttTopicFilter;
import org.mqttbee.api.mqtt.datatypes.MqttTopicFilterBuilder;
import org.mqttbee.mqtt.datatypes.MqttTopicFilterImpl;
import org.mqttbee.mqtt.message.subscribe.mqtt3.Mqtt3SubscriptionView;
import org.mqttbee.mqtt.util.MqttBuilderUtil;
import org.mqttbee.util.FluentBuilder;

import java.util.function.Function;

/**
 * @author Silvio Giebl
 */
public class Mqtt3SubscriptionBuilder<P> extends FluentBuilder<Mqtt3Subscription, P> {

    private MqttTopicFilterImpl topicFilter;
    private MqttQos qos;

    public Mqtt3SubscriptionBuilder(@Nullable final Function<? super Mqtt3Subscription, P> parentConsumer) {
        super(parentConsumer);
    }

    @NotNull
    public Mqtt3SubscriptionBuilder<P> topicFilter(@NotNull final String topicFilter) {
        this.topicFilter = MqttBuilderUtil.topicFilter(topicFilter);
        return this;
    }

    @NotNull
    public Mqtt3SubscriptionBuilder<P> topicFilter(@NotNull final MqttTopicFilter topicFilter) {
        this.topicFilter = MqttBuilderUtil.topicFilter(topicFilter);
        return this;
    }

    @NotNull
    public MqttTopicFilterBuilder<? extends Mqtt3SubscriptionBuilder<P>> topicFilter() {
        return new MqttTopicFilterBuilder<>("", this::topicFilter);
    }

    @NotNull
    public Mqtt3SubscriptionBuilder<P> qos(@NotNull final MqttQos qos) {
        this.qos = Preconditions.checkNotNull(qos, "QoS must not be null.");
        return this;
    }

    @NotNull
    @Override
    public Mqtt3Subscription build() {
        Preconditions.checkNotNull(topicFilter, "Topic filter must not be null.");
        Preconditions.checkNotNull(qos, "QoS must not be null.");
        return Mqtt3SubscriptionView.of(topicFilter, qos);
    }

}
