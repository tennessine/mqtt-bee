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

package org.mqttbee.mqtt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.api.mqtt.MqttClientSslConfig;
import org.mqttbee.api.mqtt.MqttWebSocketConfig;
import org.mqttbee.api.mqtt.datatypes.MqttClientIdentifier;
import org.mqttbee.api.mqtt.mqtt5.Mqtt5ClientConnectionData;
import org.mqttbee.api.mqtt.mqtt5.Mqtt5ClientData;
import org.mqttbee.api.mqtt.mqtt5.Mqtt5ServerConnectionData;
import org.mqttbee.api.mqtt.mqtt5.advanced.Mqtt5AdvancedClientData;
import org.mqttbee.mqtt.advanced.MqttAdvancedClientData;
import org.mqttbee.mqtt.datatypes.MqttClientIdentifierImpl;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Silvio Giebl
 */
public class MqttClientData implements Mqtt5ClientData {

    private final MqttVersion mqttVersion;
    private MqttClientIdentifierImpl clientIdentifier;
    private final String serverHost;
    private final int serverPort;
    private final MqttWebSocketConfig webSocketConfig;
    private final MqttClientSslConfig sslConfig;
    private final AtomicBoolean connecting;
    private final AtomicBoolean connected;
    private final boolean followsRedirects;
    private final boolean allowsServerReAuth;
    private final MqttClientExecutorConfigImpl executorConfig;
    private final MqttAdvancedClientData advancedClientData;
    private MqttClientConnectionData clientConnectionData;
    private MqttServerConnectionData serverConnectionData;

    public MqttClientData(
            @NotNull final MqttVersion mqttVersion, @Nullable final MqttClientIdentifierImpl clientIdentifier,
            @NotNull final String serverHost, final int serverPort, @Nullable final MqttClientSslConfig sslConfig,
            @Nullable final MqttWebSocketConfig webSocketConfig, final boolean followsRedirects,
            final boolean allowsServerReAuth, @NotNull final MqttClientExecutorConfigImpl executorConfig,
            @Nullable final MqttAdvancedClientData advancedClientData) {

        this.mqttVersion = mqttVersion;
        this.clientIdentifier = clientIdentifier;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.sslConfig = sslConfig;
        this.webSocketConfig = webSocketConfig;
        this.connecting = new AtomicBoolean();
        this.connected = new AtomicBoolean();
        this.followsRedirects = followsRedirects;
        this.allowsServerReAuth = allowsServerReAuth;
        this.executorConfig = executorConfig;
        this.advancedClientData = advancedClientData;
    }

    @NotNull
    public MqttVersion getMqttVersion() {
        return mqttVersion;
    }

    @NotNull
    @Override
    public Optional<MqttClientIdentifier> getClientIdentifier() {
        return (clientIdentifier == MqttClientIdentifierImpl.REQUEST_CLIENT_IDENTIFIER_FROM_SERVER) ? Optional.empty() :
                Optional.of(clientIdentifier);
    }

    @NotNull
    public MqttClientIdentifierImpl getRawClientIdentifier() {
        return clientIdentifier;
    }

    public void setClientIdentifier(@NotNull final MqttClientIdentifierImpl clientIdentifier) {
        this.clientIdentifier = clientIdentifier;
    }

    @NotNull
    @Override
    public String getServerHost() {
        return serverHost;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    @Override
    public boolean usesSsl() {
        return sslConfig != null;
    }

    @NotNull
    @Override
    public Optional<MqttClientSslConfig> getSslConfig() {
        return Optional.ofNullable(sslConfig);
    }

    @Nullable
    public MqttClientSslConfig getRawSslConfig() {
        return sslConfig;
    }

    @Override
    public boolean usesWebSockets() {
        return webSocketConfig != null;
    }

    @NotNull
    @Override
    public Optional<MqttWebSocketConfig> getWebSocketConfig() {
        return Optional.ofNullable(webSocketConfig);
    }

    @Nullable
    public MqttWebSocketConfig getRawWebSocketConfig() {
        return webSocketConfig;
    }

    @Override
    public boolean isConnecting() {
        return connecting.get();
    }

    public boolean setConnecting(final boolean connecting) {
        return this.connecting.compareAndSet(!connecting, connecting);
    }

    @Override
    public boolean isConnected() {
        return connected.get();
    }

    public boolean setConnected(final boolean connected) {
        return this.connected.compareAndSet(!connected, connected);
    }

    @Override
    public boolean followsRedirects() {
        return followsRedirects;
    }

    @Override
    public boolean allowsServerReAuth() {
        return allowsServerReAuth;
    }

    @NotNull
    @Override
    public MqttClientExecutorConfigImpl getExecutorConfig() {
        return executorConfig;
    }

    @NotNull
    public Optional<Mqtt5AdvancedClientData> getAdvancedClientData() {
        return Optional.ofNullable(advancedClientData);
    }

    @Nullable
    public MqttAdvancedClientData getRawAdvancedClientData() {
        return advancedClientData;
    }

    @NotNull
    @Override
    public Optional<Mqtt5ClientConnectionData> getClientConnectionData() {
        return Optional.of(clientConnectionData);
    }

    @Nullable
    public MqttClientConnectionData getRawClientConnectionData() {
        return clientConnectionData;
    }

    public void setClientConnectionData(@Nullable final MqttClientConnectionData clientConnectionData) {
        this.clientConnectionData = clientConnectionData;
    }

    @NotNull
    @Override
    public Optional<Mqtt5ServerConnectionData> getServerConnectionData() {
        return Optional.of(serverConnectionData);
    }

    @Nullable
    public MqttServerConnectionData getRawServerConnectionData() {
        return serverConnectionData;
    }

    public void setServerConnectionData(@Nullable final MqttServerConnectionData serverConnectionData) {
        this.serverConnectionData = serverConnectionData;
    }

}
