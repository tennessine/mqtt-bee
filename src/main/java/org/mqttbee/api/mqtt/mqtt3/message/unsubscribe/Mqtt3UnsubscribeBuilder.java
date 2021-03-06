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

package org.mqttbee.api.mqtt.mqtt3.message.unsubscribe;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.api.mqtt.datatypes.MqttTopicFilter;
import org.mqttbee.api.mqtt.datatypes.MqttTopicFilterBuilder;
import org.mqttbee.api.mqtt.mqtt3.message.subscribe.Mqtt3Subscribe;
import org.mqttbee.api.mqtt.mqtt3.message.subscribe.Mqtt3Subscription;
import org.mqttbee.mqtt.datatypes.MqttTopicFilterImpl;
import org.mqttbee.mqtt.message.unsubscribe.mqtt3.Mqtt3UnsubscribeView;
import org.mqttbee.mqtt.util.MqttBuilderUtil;
import org.mqttbee.util.FluentBuilder;
import org.mqttbee.util.MustNotBeImplementedUtil;

import java.util.function.Function;

/**
 * @author Silvio Giebl
 */
public class Mqtt3UnsubscribeBuilder<P> extends FluentBuilder<Mqtt3Unsubscribe, P> {

    private final ImmutableList.Builder<MqttTopicFilterImpl> topicFiltersBuilder;

    public Mqtt3UnsubscribeBuilder(@Nullable final Function<? super Mqtt3Unsubscribe, P> parentConsumer) {
        super(parentConsumer);
        topicFiltersBuilder = ImmutableList.builder();
    }

    Mqtt3UnsubscribeBuilder(@NotNull final Mqtt3Unsubscribe unsubscribe) {
        super(null);
        final Mqtt3UnsubscribeView unsubscribeView =
                MustNotBeImplementedUtil.checkNotImplemented(unsubscribe, Mqtt3UnsubscribeView.class);
        final ImmutableList<MqttTopicFilterImpl> topicFilters = unsubscribeView.getDelegate().getTopicFilters();
        topicFiltersBuilder = ImmutableList.builderWithExpectedSize(topicFilters.size() + 1);
        topicFiltersBuilder.addAll(topicFilters);
    }

    @NotNull
    public Mqtt3UnsubscribeBuilder<P> addTopicFilter(@NotNull final String topicFilter) {
        topicFiltersBuilder.add(MqttBuilderUtil.topicFilter(topicFilter));
        return this;
    }

    @NotNull
    public Mqtt3UnsubscribeBuilder<P> addTopicFilter(@NotNull final MqttTopicFilter topicFilter) {
        topicFiltersBuilder.add(MqttBuilderUtil.topicFilter(topicFilter));
        return this;
    }

    @NotNull
    public MqttTopicFilterBuilder<? extends Mqtt3UnsubscribeBuilder<P>> addTopicFilter() {
        return new MqttTopicFilterBuilder<>("", this::addTopicFilter);
    }

    @NotNull
    public Mqtt3UnsubscribeBuilder<P> reverse(@NotNull final Mqtt3Subscribe subscribe) {
        final ImmutableList<? extends Mqtt3Subscription> subscriptions = subscribe.getSubscriptions();
        for (final Mqtt3Subscription subscription : subscriptions) {
            addTopicFilter(subscription.getTopicFilter());
        }
        return this;
    }

    @NotNull
    @Override
    public Mqtt3Unsubscribe build() {
        final ImmutableList<MqttTopicFilterImpl> topicFilters = topicFiltersBuilder.build();
        Preconditions.checkState(!topicFilters.isEmpty());
        return Mqtt3UnsubscribeView.of(topicFilters);
    }

}
