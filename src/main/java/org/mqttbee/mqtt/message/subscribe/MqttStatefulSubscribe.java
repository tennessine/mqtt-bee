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

package org.mqttbee.mqtt.message.subscribe;

import org.jetbrains.annotations.NotNull;
import org.mqttbee.mqtt.message.MqttStatefulMessage.MqttStatefulMessageWithId;

import javax.annotation.concurrent.Immutable;

/**
 * @author Silvio Giebl
 */
@Immutable
public class MqttStatefulSubscribe extends MqttStatefulMessageWithId<MqttSubscribe> {

    public static final int DEFAULT_NO_SUBSCRIPTION_IDENTIFIER = -1;

    private final int subscriptionIdentifier;

    MqttStatefulSubscribe(
            @NotNull final MqttSubscribe subscribe, final int packetIdentifier, final int subscriptionIdentifier) {

        super(subscribe, packetIdentifier);
        this.subscriptionIdentifier = subscriptionIdentifier;
    }

    public int getSubscriptionIdentifier() {
        return subscriptionIdentifier;
    }

}
