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

package org.mqttbee.mqtt.message.publish.pubrec;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.api.mqtt.mqtt5.datatypes.Mqtt5UserProperties;
import org.mqttbee.api.mqtt.mqtt5.message.publish.pubrec.Mqtt5PubRecBuilder;
import org.mqttbee.api.mqtt.mqtt5.message.publish.pubrec.Mqtt5PubRecReasonCode;
import org.mqttbee.mqtt.datatypes.MqttUTF8StringImpl;
import org.mqttbee.mqtt.datatypes.MqttUserPropertiesImpl;
import org.mqttbee.mqtt.message.publish.MqttStatefulPublish;
import org.mqttbee.mqtt.util.MqttBuilderUtil;

/**
 * @author Silvio Giebl
 */
public class MqttPubRecBuilder implements Mqtt5PubRecBuilder {

    private final MqttStatefulPublish publish;
    private Mqtt5PubRecReasonCode reasonCode = MqttPubRec.DEFAULT_REASON_CODE;
    private MqttUTF8StringImpl reasonString;
    private MqttUserPropertiesImpl userProperties = MqttUserPropertiesImpl.NO_USER_PROPERTIES;

    public MqttPubRecBuilder(@NotNull final MqttStatefulPublish publish) {
        this.publish = publish;
    }

    @NotNull
    @Override
    public MqttPubRecBuilder userProperties(@NotNull final Mqtt5UserProperties userProperties) {
        this.userProperties = MqttBuilderUtil.userProperties(userProperties);
        return this;
    }

    @NotNull
    public MqttPubRecBuilder reasonCode(@NotNull final Mqtt5PubRecReasonCode reasonCode) {
        this.reasonCode = reasonCode;
        return this;
    }

    @NotNull
    public MqttPubRecBuilder reasonString(@Nullable final MqttUTF8StringImpl reasonString) {
        this.reasonString = reasonString;
        return this;
    }

    @NotNull
    @Override
    public Mqtt5PubRecReasonCode getReasonCode() {
        return reasonCode;
    }

    @NotNull
    @Override
    public MqttUTF8StringImpl getReasonString() {
        return reasonString;
    }

    @NotNull
    public MqttStatefulPublish getPublish() {
        return publish;
    }

    public MqttPubRec build() {
        return new MqttPubRec(publish.getPacketIdentifier(), reasonCode, reasonString, userProperties);
    }

}
