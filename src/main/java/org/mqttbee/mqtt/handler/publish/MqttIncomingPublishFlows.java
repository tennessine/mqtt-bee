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

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.api.mqtt.MqttGlobalPublishFlowType;
import org.mqttbee.api.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAckReasonCode;
import org.mqttbee.api.mqtt.mqtt5.message.unsubscribe.unsuback.Mqtt5UnsubAckReasonCode;
import org.mqttbee.mqtt.datatypes.MqttTopicFilterImpl;
import org.mqttbee.mqtt.datatypes.MqttTopicImpl;
import org.mqttbee.mqtt.ioc.ChannelScope;
import org.mqttbee.mqtt.message.publish.MqttStatefulPublish;
import org.mqttbee.mqtt.message.subscribe.MqttStatefulSubscribe;
import org.mqttbee.mqtt.message.subscribe.MqttSubscription;
import org.mqttbee.mqtt.message.subscribe.suback.MqttSubAck;
import org.mqttbee.mqtt.message.unsubscribe.MqttStatefulUnsubscribe;
import org.mqttbee.mqtt.message.unsubscribe.unsuback.MqttUnsubAck;
import org.mqttbee.mqtt.message.unsubscribe.unsuback.mqtt3.Mqtt3UnsubAckView;
import org.mqttbee.util.collections.ScNodeList;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;

/**
 * @author Silvio Giebl
 */
@ChannelScope
@NotThreadSafe
public class MqttIncomingPublishFlows {

    @NotNull
    private final MqttSubscriptionFlows subscriptionFlows;
    @NotNull
    private final ScNodeList<MqttGlobalIncomingPublishFlow>[] globalFlows;

    @Inject
    @SuppressWarnings("unchecked")
    MqttIncomingPublishFlows(@NotNull final MqttSubscriptionFlows subscriptionFlows) {
        this.subscriptionFlows = subscriptionFlows;
        globalFlows = new ScNodeList[MqttGlobalPublishFlowType.values().length];
    }

    public void subscribe(
            @NotNull final MqttStatefulSubscribe subscribe, @NotNull final MqttSubAck subAck,
            @Nullable final MqttSubscriptionFlow flow) {

        final ImmutableList<MqttSubscription> subscriptions = subscribe.getStatelessMessage().getSubscriptions();
        final ImmutableList<Mqtt5SubAckReasonCode> reasonCodes = subAck.getReasonCodes();
        for (int i = 0; i < subscriptions.size(); i++) {
            if (!reasonCodes.get(i).isError()) {
                subscribe(subscriptions.get(i).getTopicFilter(), flow);
            }
        }
    }

    void subscribe(@NotNull final MqttTopicFilterImpl topicFilter, @Nullable final MqttSubscriptionFlow flow) {
        subscriptionFlows.subscribe(topicFilter, flow);
    }

    public void unsubscribe(@NotNull final MqttStatefulUnsubscribe unsubscribe, @NotNull final MqttUnsubAck unsubAck) {
        final ImmutableList<MqttTopicFilterImpl> topicFilters = unsubscribe.getStatelessMessage().getTopicFilters();
        final ImmutableList<Mqtt5UnsubAckReasonCode> reasonCodes = unsubAck.getReasonCodes();
        final boolean areAllSuccess = reasonCodes == Mqtt3UnsubAckView.REASON_CODES_ALL_SUCCESS;
        for (int i = 0; i < topicFilters.size(); i++) {
            if (areAllSuccess || !reasonCodes.get(i).isError()) {
                unsubscribe(topicFilters.get(i));
            }
        }
    }

    void unsubscribe(@NotNull final MqttTopicFilterImpl topicFilter) {
        subscriptionFlows.unsubscribe(topicFilter, null);
    }

    void cancel(@NotNull final MqttSubscriptionFlow flow) {
        subscriptionFlows.cancel(flow);
    }

    @NotNull
    ScNodeList<MqttIncomingPublishFlow> findMatching(@NotNull final MqttStatefulPublish publish) {
        final ScNodeList<MqttIncomingPublishFlow> matchingFlows = new ScNodeList<>();
        findMatching(publish, matchingFlows);
        return matchingFlows;
    }

    void findMatching(
            @NotNull final MqttStatefulPublish publish,
            @NotNull final ScNodeList<MqttIncomingPublishFlow> matchingFlows) {

        final MqttTopicImpl topic = publish.getStatelessMessage().getTopic();
        if (subscriptionFlows.findMatching(topic, matchingFlows) || !matchingFlows.isEmpty()) {
            add(matchingFlows, globalFlows[MqttGlobalPublishFlowType.ALL_SUBSCRIPTIONS.ordinal()]);
        }
        add(matchingFlows, globalFlows[MqttGlobalPublishFlowType.ALL_PUBLISHES.ordinal()]);
        if (matchingFlows.isEmpty()) {
            add(matchingFlows, globalFlows[MqttGlobalPublishFlowType.REMAINING_PUBLISHES.ordinal()]);
        }
    }

    void subscribeGlobal(@NotNull final MqttGlobalIncomingPublishFlow flow) {
        final int type = flow.getType().ordinal();
        ScNodeList<MqttGlobalIncomingPublishFlow> globalFlow = globalFlows[type];
        if (globalFlow == null) {
            globalFlow = new ScNodeList<>();
            globalFlows[type] = globalFlow;
        }
        flow.setHandle(globalFlow.add(flow));
    }

    void cancelGlobal(@NotNull final MqttGlobalIncomingPublishFlow flow) {
        flow.getHandle().remove();
        final int type = flow.getType().ordinal();
        final ScNodeList<MqttGlobalIncomingPublishFlow> globalFlow = globalFlows[type];
        if (globalFlow.isEmpty()) {
            globalFlows[type] = null;
        }
    }

    private static void add(
            @NotNull final ScNodeList<MqttIncomingPublishFlow> target,
            @Nullable final ScNodeList<? extends MqttIncomingPublishFlow> source) {

        if (source != null) {
            for (final MqttIncomingPublishFlow flow : source) {
                target.add(flow);
            }
        }
    }

}
