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

package org.mqttbee.mqtt.message.publish.mqtt3;

import io.reactivex.functions.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.api.mqtt.mqtt3.message.publish.Mqtt3Publish;
import org.mqttbee.api.mqtt.mqtt3.message.publish.Mqtt3PublishResult;
import org.mqttbee.api.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
import org.mqttbee.mqtt.message.publish.MqttPublishResult;
import org.mqttbee.mqtt.mqtt3.exceptions.Mqtt3ExceptionFactory;
import org.mqttbee.util.MustNotBeImplementedUtil;

/**
 * @author Silvio Giebl
 */
public class Mqtt3PublishResultView implements Mqtt3PublishResult {

    @NotNull
    public static final Function<Mqtt5PublishResult, Mqtt3PublishResult> MAPPER = Mqtt3PublishResultView::of;

    @NotNull
    private static Mqtt3PublishResultView of(@NotNull final Mqtt5PublishResult publishResult) {
        return new Mqtt3PublishResultView(
                MustNotBeImplementedUtil.checkNotImplemented(publishResult, MqttPublishResult.class));
    }

    private final MqttPublishResult delegate;

    private Mqtt3PublishResultView(@NotNull final MqttPublishResult delegate) {
        this.delegate = delegate;
    }

    @NotNull
    @Override
    public Mqtt3Publish getPublish() {
        return Mqtt3PublishView.of(delegate.getPublish());
    }

    @Override
    public boolean isSuccess() {
        return delegate.isSuccess();
    }

    @Nullable
    @Override
    public Throwable getError() {
        final Throwable error = delegate.getError();
        return (error == null) ? null : Mqtt3ExceptionFactory.map(error);
    }

}
