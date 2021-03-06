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

package org.mqttbee.mqtt.handler.subscribe;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.internal.disposables.EmptyDisposable;
import org.jetbrains.annotations.NotNull;
import org.mqttbee.api.mqtt.exceptions.NotConnectedException;
import org.mqttbee.api.mqtt.mqtt5.message.unsubscribe.unsuback.Mqtt5UnsubAck;
import org.mqttbee.mqtt.MqttClientConnectionData;
import org.mqttbee.mqtt.MqttClientData;
import org.mqttbee.mqtt.ioc.ChannelComponent;
import org.mqttbee.mqtt.message.unsubscribe.MqttUnsubscribe;
import org.mqttbee.rx.SingleFlow.DefaultSingleFlow;

/**
 * @author Silvio Giebl
 */
public class MqttUnsubAckSingle extends Single<Mqtt5UnsubAck> {

    private final MqttUnsubscribe unsubscribe;
    private final MqttClientData clientData;

    public MqttUnsubAckSingle(@NotNull final MqttUnsubscribe unsubscribe, @NotNull final MqttClientData clientData) {
        this.unsubscribe = unsubscribe;
        this.clientData = clientData;
    }

    @Override
    protected void subscribeActual(final SingleObserver<? super Mqtt5UnsubAck> observer) {
        final MqttClientConnectionData clientConnectionData = clientData.getRawClientConnectionData();
        if (clientConnectionData == null) {
            EmptyDisposable.error(new NotConnectedException(), observer);
        } else {
            final ChannelComponent channelComponent = ChannelComponent.get(clientConnectionData.getChannel());
            final MqttSubscriptionHandler subscriptionHandler = channelComponent.subscriptionHandler();

            final DefaultSingleFlow<Mqtt5UnsubAck> flow = new DefaultSingleFlow<>(observer);
            observer.onSubscribe(flow);
            subscriptionHandler.unsubscribe(new MqttUnsubscribeWithFlow(unsubscribe, flow));
        }
    }

}
