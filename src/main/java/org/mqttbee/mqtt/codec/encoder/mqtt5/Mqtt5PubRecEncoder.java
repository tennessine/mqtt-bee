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

package org.mqttbee.mqtt.codec.encoder.mqtt5;

import org.mqttbee.api.mqtt.mqtt5.message.Mqtt5MessageType;
import org.mqttbee.api.mqtt.mqtt5.message.publish.pubrec.Mqtt5PubRecReasonCode;
import org.mqttbee.mqtt.codec.encoder.mqtt5.Mqtt5MessageWithUserPropertiesEncoder.Mqtt5MessageWithIdAndOmissibleReasonCodeEncoder;
import org.mqttbee.mqtt.message.publish.pubrec.MqttPubRec;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.mqttbee.mqtt.message.publish.pubrec.MqttPubRec.DEFAULT_REASON_CODE;

/**
 * @author Silvio Giebl
 */
@Singleton
public class Mqtt5PubRecEncoder
        extends Mqtt5MessageWithIdAndOmissibleReasonCodeEncoder<MqttPubRec, Mqtt5PubRecReasonCode> {

    private static final int FIXED_HEADER = Mqtt5MessageType.PUBREC.getCode() << 4;

    @Inject
    Mqtt5PubRecEncoder() {
    }

    @Override
    int getFixedHeader() {
        return FIXED_HEADER;
    }

    @Override
    Mqtt5PubRecReasonCode getDefaultReasonCode() {
        return DEFAULT_REASON_CODE;
    }

}
