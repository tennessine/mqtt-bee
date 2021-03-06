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

package org.mqttbee.mqtt.handler.publish;

import org.jetbrains.annotations.NotNull;
import org.mqttbee.api.mqtt.mqtt5.message.subscribe.Mqtt5SubscribeResult;
import org.mqttbee.api.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;
import org.mqttbee.mqtt.datatypes.MqttTopicFilterImpl;
import org.mqttbee.mqtt.message.subscribe.MqttStatefulSubscribe;
import org.mqttbee.rx.SingleFlow;
import org.mqttbee.util.collections.ScNodeList;
import org.reactivestreams.Subscriber;

/**
 * @author Silvio Giebl
 */
public class MqttSubscriptionFlow extends MqttIncomingPublishFlow<Subscriber<? super Mqtt5SubscribeResult>>
        implements SingleFlow<Mqtt5SubAck> {

    private final ScNodeList<MqttTopicFilterImpl> topicFilters;
    private int subscriptionIdentifier = MqttStatefulSubscribe.DEFAULT_NO_SUBSCRIPTION_IDENTIFIER;

    MqttSubscriptionFlow(
            @NotNull final Subscriber<? super Mqtt5SubscribeResult> subscriber,
            @NotNull final MqttIncomingPublishService incomingPublishService) {

        super(incomingPublishService, subscriber);
        this.topicFilters = new ScNodeList<>();
    }

    @Override
    public void onSuccess(@NotNull final Mqtt5SubAck subAck) {
        if (done) {
            return;
        }
        subscriber.onNext(subAck);
        if (requested != Long.MAX_VALUE) {
            requested--;
        }
    }

    @Override
    void runCancel() {
        incomingPublishService.getIncomingPublishFlows().cancel(this);
        super.runCancel();
    }

    @NotNull
    ScNodeList<MqttTopicFilterImpl> getTopicFilters() {
        return topicFilters;
    }

    int getSubscriptionIdentifier() {
        return subscriptionIdentifier;
    }

    void setSubscriptionIdentifier(final int subscriptionIdentifier) {
        this.subscriptionIdentifier = subscriptionIdentifier;
    }

}
