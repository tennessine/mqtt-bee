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

package org.mqttbee.api.mqtt.mqtt5.advanced.qos2;

import org.jetbrains.annotations.NotNull;
import org.mqttbee.api.mqtt.mqtt5.Mqtt5ClientData;
import org.mqttbee.api.mqtt.mqtt5.message.publish.Mqtt5Publish;
import org.mqttbee.api.mqtt.mqtt5.message.publish.pubcomp.Mqtt5PubCompBuilder;
import org.mqttbee.api.mqtt.mqtt5.message.publish.pubrec.Mqtt5PubRecBuilder;
import org.mqttbee.api.mqtt.mqtt5.message.publish.pubrel.Mqtt5PubRel;

/**
 * Interface for providers for controlling the QoS 2 control flow of incoming PUBLISH messages.
 *
 * @author Silvio Giebl
 */
public interface Mqtt5IncomingQos2ControlProvider {

    /**
     * Called when a server sent a PUBLISH message with QoS 2.
     * <p>
     * This method must not block and just add some properties to the outgoing PUBREC message.
     *
     * @param clientData    the data of the client.
     * @param publish       the Publish message with QoS 2 sent by the server.
     * @param pubAckBuilder the builder for the outgoing PUBREC message.
     */
    void onPublish(
            @NotNull Mqtt5ClientData clientData, @NotNull Mqtt5Publish publish,
            @NotNull Mqtt5PubRecBuilder pubAckBuilder);

    /**
     * Called when a server sent a PUBREL message for a PUBLISH with QoS 2.
     * <p>
     * This method must not block and just add some properties to the outgoing PUBCOMP message.
     *
     * @param clientData     the data of the client.
     * @param pubRel         the PubRel message sent by the server.
     * @param pubCompBuilder the builder for the outgoing PUBCOMP message.
     */
    void onPubRel(
            @NotNull Mqtt5ClientData clientData, @NotNull Mqtt5PubRel pubRel,
            @NotNull Mqtt5PubCompBuilder pubCompBuilder);

}
