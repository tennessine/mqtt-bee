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

package org.mqttbee.mqtt.codec.encoder.mqtt3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;
import org.mqttbee.api.mqtt.datatypes.MqttQos;
import org.mqttbee.api.mqtt.mqtt3.message.Mqtt3MessageType;
import org.mqttbee.mqtt.datatypes.MqttVariableByteInteger;
import org.mqttbee.mqtt.message.publish.MqttPublish;
import org.mqttbee.mqtt.message.publish.MqttStatefulPublish;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.ByteBuffer;

/**
 * @author Silvio Giebl
 */
@Singleton
public class Mqtt3PublishEncoder extends Mqtt3MessageEncoder<MqttStatefulPublish> {

    private static final int FIXED_HEADER = Mqtt3MessageType.PUBLISH.getCode() << 4;

    @Inject
    Mqtt3PublishEncoder() {
    }

    @Override
    int remainingLength(@NotNull final MqttStatefulPublish message) {
        final MqttPublish stateless = message.getStatelessMessage();

        int remainingLength = 0;

        remainingLength += stateless.getTopic().encodedLength();

        if (stateless.getQos() != MqttQos.AT_MOST_ONCE) {
            remainingLength += 2;
        }

        final ByteBuffer payload = stateless.getRawPayload();
        if (payload != null) {
            remainingLength += payload.remaining();
        }

        return remainingLength;
    }

    @NotNull
    @Override
    ByteBuf encode(
            @NotNull final MqttStatefulPublish message, @NotNull final ByteBufAllocator allocator,
            final int encodedLength, final int remainingLength) {

        final ByteBuffer payload = message.getStatelessMessage().getRawPayload();
        if ((payload != null) && payload.isDirect()) {
            final int encodedLengthWithoutPayload = encodedLength - payload.remaining();
            final ByteBuf out = allocator.ioBuffer(encodedLengthWithoutPayload, encodedLengthWithoutPayload);
            encode(message, out, remainingLength);
            return Unpooled.unmodifiableBuffer(out, Unpooled.wrappedBuffer(payload));
        }
        final ByteBuf out = allocator.ioBuffer(encodedLength, encodedLength);
        encode(message, out, remainingLength);
        return out;
    }

    @Override
    void encode(
            @NotNull final MqttStatefulPublish message, @NotNull final ByteBuf out, final int remainingLength) {

        encodeFixedHeader(message, out, remainingLength);
        encodeVariableHeader(message, out);
        encodePayload(message, out);
    }

    private void encodeFixedHeader(
            @NotNull final MqttStatefulPublish message, @NotNull final ByteBuf out, final int remainingLength) {

        final MqttPublish stateless = message.getStatelessMessage();

        int flags = 0;
        if (message.isDup()) {
            flags |= 0b1000;
        }
        flags |= stateless.getQos().getCode() << 1;
        if (stateless.isRetain()) {
            flags |= 0b0001;
        }

        out.writeByte(FIXED_HEADER | flags);

        MqttVariableByteInteger.encode(remainingLength, out);
    }

    private void encodeVariableHeader(@NotNull final MqttStatefulPublish message, @NotNull final ByteBuf out) {
        final MqttPublish stateless = message.getStatelessMessage();

        stateless.getTopic().to(out);

        if (stateless.getQos() != MqttQos.AT_MOST_ONCE) {
            out.writeShort(message.getPacketIdentifier());
        }
    }

    private void encodePayload(@NotNull final MqttStatefulPublish message, @NotNull final ByteBuf out) {
        final ByteBuffer payload = message.getStatelessMessage().getRawPayload();
        if ((payload != null) && !payload.isDirect()) {
            out.writeBytes(payload.duplicate());
        }
    }

}
