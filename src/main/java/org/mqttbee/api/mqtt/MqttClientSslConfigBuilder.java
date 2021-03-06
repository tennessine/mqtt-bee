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

package org.mqttbee.api.mqtt;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.mqtt.MqttClientSslConfigImpl;
import org.mqttbee.util.FluentBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Christian Hoff
 */
public class MqttClientSslConfigBuilder<P> extends FluentBuilder<MqttClientSslConfig, P> {

    private KeyManagerFactory keyManagerFactory = null;
    private TrustManagerFactory trustManagerFactory = null;
    private ImmutableList<String> cipherSuites = null;
    private ImmutableList<String> protocols = null;
    private long handshakeTimeoutMs = MqttClientSslConfig.DEFAULT_HANDSHAKE_TIMEOUT_MS;

    public MqttClientSslConfigBuilder(@Nullable final Function<? super MqttClientSslConfig, P> parentConsumer) {
        super(parentConsumer);
    }

    @NotNull
    public MqttClientSslConfigBuilder<P> keyManagerFactory(@Nullable final KeyManagerFactory keyManagerFactory) {
        this.keyManagerFactory = keyManagerFactory;
        return this;
    }

    @NotNull
    public MqttClientSslConfigBuilder<P> trustManagerFactory(@Nullable final TrustManagerFactory trustManagerFactory) {
        this.trustManagerFactory = trustManagerFactory;
        return this;
    }

    /**
     * @param cipherSuites if <code>null</code>, netty's default cipher suites will be used
     */
    @NotNull
    public MqttClientSslConfigBuilder<P> cipherSuites(@Nullable final List<String> cipherSuites) {
        this.cipherSuites = (cipherSuites == null) ? null : ImmutableList.copyOf(cipherSuites);
        return this;
    }

    /**
     * @param protocols if <code>null</code>, netty's default protocols will be used
     */
    @NotNull
    public MqttClientSslConfigBuilder<P> protocols(@Nullable final List<String> protocols) {
        this.protocols = (protocols == null) ? null : ImmutableList.copyOf(protocols);
        return this;
    }

    @NotNull
    public MqttClientSslConfigBuilder<P> handshakeTimeout(final long timeout, @NotNull final TimeUnit timeUnit) {
        this.handshakeTimeoutMs = TimeUnit.MILLISECONDS.convert(timeout, timeUnit);
        return this;
    }

    @NotNull
    @Override
    public MqttClientSslConfig build() {
        return new MqttClientSslConfigImpl(
                keyManagerFactory, trustManagerFactory, cipherSuites, protocols, handshakeTimeoutMs);
    }

}
