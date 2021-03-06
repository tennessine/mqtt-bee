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
 */

package org.mqttbee.mqtt.mqtt3;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.jetbrains.annotations.NotNull;
import org.mqttbee.api.mqtt.MqttGlobalPublishFlowType;
import org.mqttbee.api.mqtt.datatypes.MqttQos;
import org.mqttbee.api.mqtt.mqtt3.exceptions.Mqtt3MessageException;
import org.mqttbee.api.mqtt.mqtt3.message.connect.Mqtt3Connect;
import org.mqttbee.api.mqtt.mqtt3.message.publish.Mqtt3Publish;
import org.mqttbee.api.mqtt.mqtt3.message.subscribe.Mqtt3Subscribe;
import org.mqttbee.api.mqtt.mqtt3.message.subscribe.Mqtt3Subscription;
import org.mqttbee.api.mqtt.mqtt3.message.unsubscribe.Mqtt3Unsubscribe;
import org.mqttbee.api.mqtt.mqtt5.exceptions.Mqtt5MessageException;
import org.mqttbee.api.mqtt.mqtt5.message.connect.Mqtt5Connect;
import org.mqttbee.api.mqtt.mqtt5.message.publish.Mqtt5Publish;
import org.mqttbee.api.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;
import org.mqttbee.mqtt.mqtt5.Mqtt5ClientImpl;
import org.mqttbee.rx.FlowableWithSingleSplit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * @author David Katz
 */
class Mqtt3ClientViewExceptionsTest {

    private Mqtt5ClientImpl mqtt5Client;
    private Mqtt3ClientView mqtt3Client;

    @BeforeEach
    void setUp() {
        mqtt5Client = mock(Mqtt5ClientImpl.class);
        mqtt3Client = new Mqtt3ClientView(mqtt5Client);
    }

    @Test
    void connect() {
        final Mqtt5MessageException mqtt5MessageException =
                new Mqtt5MessageException(Mqtt5Connect.builder().build(), "reason from original exception");
        given(mqtt5Client.connect(any())).willReturn(Single.error(mqtt5MessageException));

        final Mqtt3Connect connect = Mqtt3Connect.builder().build();
        assertMqtt3Exception(() -> mqtt3Client.connect(connect).toCompletable().blockingAwait(), mqtt5MessageException);
    }

    @Test
    void subscribe() {
        final Mqtt5MessageException mqtt5MessageException =
                new Mqtt5MessageException(Mqtt5Connect.builder().build(), "reason from original exception");
        given(mqtt5Client.subscribe(any())).willReturn(Single.error(mqtt5MessageException));

        final Mqtt3Subscribe subscribe = Mqtt3Subscribe.builder()
                .addSubscription(
                        Mqtt3Subscription.builder().topicFilter("topic").qos(MqttQos.AT_LEAST_ONCE).build())
                .build();
        assertMqtt3Exception(() -> mqtt3Client.subscribe(subscribe).toCompletable().blockingAwait(),
                mqtt5MessageException);
    }

    @Test
    void subscribeWithStream() {
        final Mqtt5MessageException mqtt5MessageException =
                new Mqtt5MessageException(Mqtt5Connect.builder().build(), "reason from original exception");
        given(mqtt5Client.subscribeWithStream(any())).willReturn(
                new FlowableWithSingleSplit<>(Flowable.error(mqtt5MessageException), Mqtt5SubAck.class,
                        Mqtt5Publish.class));

        final Mqtt3Subscribe subscribe = Mqtt3Subscribe.builder()
                .addSubscription(
                        Mqtt3Subscription.builder().topicFilter("topic").qos(MqttQos.AT_LEAST_ONCE).build())
                .build();
        assertMqtt3Exception(() -> mqtt3Client.subscribeWithStream(subscribe).blockingSubscribe(),
                mqtt5MessageException);
    }

    @ParameterizedTest
    @EnumSource(MqttGlobalPublishFlowType.class)
    void publishes(final MqttGlobalPublishFlowType type) {
        final Mqtt5MessageException mqtt5MessageException =
                new Mqtt5MessageException(Mqtt5Connect.builder().build(), "reason from original exception");
        given(mqtt5Client.publishes(type)).willReturn(Flowable.error(mqtt5MessageException));

        assertMqtt3Exception(() -> mqtt3Client.publishes(type).blockingSubscribe(), mqtt5MessageException);
    }

    @Test
    void unsubscribe() {
        final Mqtt5MessageException mqtt5MessageException =
                new Mqtt5MessageException(Mqtt5Connect.builder().build(), "reason from original exception");
        given(mqtt5Client.unsubscribe(any())).willReturn(Single.error(mqtt5MessageException));

        final Mqtt3Unsubscribe unsubscribe = Mqtt3Unsubscribe.builder().addTopicFilter("topic").build();
        assertMqtt3Exception(() -> mqtt3Client.unsubscribe(unsubscribe).blockingAwait(), mqtt5MessageException);
    }

    @Test
    void publish() {
        final Mqtt5MessageException mqtt5MessageException =
                new Mqtt5MessageException(Mqtt5Connect.builder().build(), "reason from original exception");
        given(mqtt5Client.publish(any())).willReturn(Flowable.error(mqtt5MessageException));

        final Flowable<Mqtt3Publish> publish =
                Flowable.just(Mqtt3Publish.builder().topic("topic").qos(MqttQos.AT_LEAST_ONCE).build());
        assertMqtt3Exception(() -> mqtt3Client.publish(publish).blockingSubscribe(), mqtt5MessageException);
    }

    @Test
    void disconnect() {
        final Mqtt5MessageException mqtt5MessageException =
                new Mqtt5MessageException(Mqtt5Connect.builder().build(), "reason from original exception");
        given(mqtt5Client.disconnect(any())).willReturn(Completable.error(mqtt5MessageException));

        assertMqtt3Exception(() -> mqtt3Client.disconnect().blockingAwait(), mqtt5MessageException);
    }

    private void assertMqtt3Exception(
            @NotNull final Executable executable, @NotNull final Mqtt5MessageException mqtt5MessageException) {

        final RuntimeException runtimeException = assertThrows(RuntimeException.class, executable);
        assertTrue(runtimeException.getCause() instanceof Mqtt3MessageException);
        final Mqtt3MessageException mqtt3MessageException = (Mqtt3MessageException) runtimeException.getCause();
        assertEquals(mqtt5MessageException.getMqttMessage().getType().getCode(),
                mqtt3MessageException.getMqttMessage().getType().getCode());
        assertEquals(mqtt5MessageException.getMessage(), mqtt3MessageException.getMessage());
    }

}
