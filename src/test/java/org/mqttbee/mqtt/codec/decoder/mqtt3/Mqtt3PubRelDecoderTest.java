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

package org.mqttbee.mqtt.codec.decoder.mqtt3;

import com.google.common.primitives.Bytes;
import io.netty.buffer.ByteBuf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.api.mqtt.mqtt3.message.Mqtt3MessageType;
import org.mqttbee.mqtt.codec.decoder.MqttMessageDecoder;
import org.mqttbee.mqtt.codec.decoder.MqttMessageDecoders;
import org.mqttbee.mqtt.message.publish.pubrel.MqttPubRel;

import static org.junit.jupiter.api.Assertions.*;

class Mqtt3PubRelDecoderTest extends AbstractMqtt3DecoderTest {

    private static final byte[] WELLFORMED_PUBREL_BEGIN = {
            //   type, flags
            0b0110_0010,
            //remaining length
            0b0000_0010
    };
    private static final byte[] MALFORMED_PUBREL_BEGIN_WRONG_FLAGS = {
            //   type, flags
            0b0110_0100,
            //remaining length
            0b0000_0010
    };
    private static final byte[] MALFORMED_PUBREL_BEGIN_TOO_LONG_LENGTH = {
            //   type, flags
            0b0110_0100,
            //remaining length
            0b0000_0011
    };
    private static final byte[] ENDING_TOO_LONG_MALFORMED = {0x01};
    private static final byte[] MAX_PACKET_ID = {(byte) 0b1111_1111, (byte) 0b1111_1111};
    private static final byte[] MIN_PACKET_ID = {0x00, 0x00};

    Mqtt3PubRelDecoderTest() {
        super(new Mqtt3PubRelTestMessageDecoders());
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void decode_SUCESS(final boolean useMaxPacketId) {

        final byte[] encoded = Bytes.concat(WELLFORMED_PUBREL_BEGIN, useMaxPacketId ? MAX_PACKET_ID : MIN_PACKET_ID);
        final ByteBuf byteBuf = channel.alloc().buffer();
        byteBuf.writeBytes(encoded);
        channel.writeInbound(byteBuf);
        final MqttPubRel pubRel = channel.readInbound();
        assertNotNull(pubRel);
        assertEquals(useMaxPacketId ? 65535 : 0, pubRel.getPacketIdentifier());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2"})
    void decode_ERROR_CASES(final int errorcase) throws Exception {
        final byte[] encoded;
        switch (errorcase) {
            case 1:
                encoded = Bytes.concat(MALFORMED_PUBREL_BEGIN_WRONG_FLAGS, MAX_PACKET_ID);
                break;
            case 2:
                encoded =
                        Bytes.concat(MALFORMED_PUBREL_BEGIN_TOO_LONG_LENGTH, MIN_PACKET_ID, ENDING_TOO_LONG_MALFORMED);
                break;
            default:
                throw new Exception();
        }

        final ByteBuf byteBuf = channel.alloc().buffer();
        byteBuf.writeBytes(encoded);
        channel.writeInbound(byteBuf);
        final MqttPubRel pubRel = channel.readInbound();
        assertFalse(channel.isOpen());
        assertNull(pubRel);
    }

    private static class Mqtt3PubRelTestMessageDecoders implements MqttMessageDecoders {
        @Nullable
        @Override
        public MqttMessageDecoder get(final int code) {
            if (code == Mqtt3MessageType.PUBREL.getCode()) {
                return new Mqtt3PubRelDecoder();
            }
            return null;
        }
    }

}