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

package org.mqttbee.api.mqtt.mqtt3.message.connect.connack;

import org.mqttbee.annotations.DoNotImplement;
import org.jetbrains.annotations.NotNull;
import org.mqttbee.api.mqtt.mqtt3.message.Mqtt3Message;
import org.mqttbee.api.mqtt.mqtt3.message.Mqtt3MessageType;

/**
 * MQTT 3 CONNACK packet.
 */
@DoNotImplement
public interface Mqtt3ConnAck extends Mqtt3Message {

    /**
     * @return the return code of this CONNACK packet.
     */
    @NotNull
    Mqtt3ConnAckReturnCode getReturnCode();

    /**
     * @return whether the server has a session present.
     */
    boolean isSessionPresent();

    @NotNull
    @Override
    default Mqtt3MessageType getType() {
        return Mqtt3MessageType.CONNACK;
    }

}
