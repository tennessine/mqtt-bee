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

package org.mqttbee.api.mqtt.mqtt3.message.auth;

import org.mqttbee.annotations.DoNotImplement;
import org.jetbrains.annotations.NotNull;
import org.mqttbee.api.mqtt.datatypes.MqttUTF8String;

import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * Simple authentication and/or authorization related data in the MQTT 3 CONNECT packet.
 */
@DoNotImplement
public interface Mqtt3SimpleAuth {

    @NotNull
    static Mqtt3SimpleAuthBuilder<Void> builder() {
        return new Mqtt3SimpleAuthBuilder<>(null);
    }

    /**
     * @return the username.
     */
    @NotNull
    MqttUTF8String getUsername();

    /**
     * @return the optional password.
     */
    @NotNull
    Optional<ByteBuffer> getPassword();

}
