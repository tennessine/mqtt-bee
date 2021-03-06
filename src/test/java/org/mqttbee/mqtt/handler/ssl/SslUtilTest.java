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
package org.mqttbee.mqtt.handler.ssl;

import com.google.common.collect.ImmutableList;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Before;
import org.junit.Test;
import org.mqttbee.api.mqtt.MqttClientSslConfig;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Christoph Schäbel
 */
public class SslUtilTest {

    private EmbeddedChannel embeddedChannel;

    @Before
    public void before() {
        embeddedChannel = new EmbeddedChannel();
    }

    @Test
    public void test_createSslEngine_null_values() throws Exception {

        final TrustManagerFactory tmf = null;
        final TestSslConfig sslConfig = new TestSslConfig(null, tmf, null, null, 0);

        final SSLEngine sslEngine = SslUtil.createSslEngine(embeddedChannel, sslConfig);

        assertNotNull(sslEngine);
        assertTrue(sslEngine.getUseClientMode());
        assertTrue(sslEngine.getEnabledProtocols().length > 0);
        assertTrue(sslEngine.getEnabledCipherSuites().length > 0);
    }

    @Test
    public void test_createSslEngine_cipher_suite() throws Exception {

        final TrustManagerFactory tmf = null;

        final ImmutableList<String> cipherSuite = getFirstSupportedCipherSuite();
        final TestSslConfig sslConfig = new TestSslConfig(null, tmf, cipherSuite, null, 0);

        final SSLEngine sslEngine = SslUtil.createSslEngine(embeddedChannel, sslConfig);

        assertNotNull(sslEngine);

        final String[] enabledCipherSuites = sslEngine.getEnabledCipherSuites();

        assertEquals(1, enabledCipherSuites.length);
        assertEquals(cipherSuite.get(0), enabledCipherSuites[0]);
    }

    @Test
    public void test_createSslEngine_multiple_cipher_suites() throws Exception {

        final TrustManagerFactory tmf = null;

        final ImmutableList<String> cipherSuites = getOtherSupportedCipherSuites();
        final TestSslConfig sslConfig = new TestSslConfig(null, tmf, cipherSuites, null, 0);

        final SSLEngine sslEngine = SslUtil.createSslEngine(embeddedChannel, sslConfig);

        assertNotNull(sslEngine);

        final String[] enabledCipherSuites = sslEngine.getEnabledCipherSuites();

        assertEquals(2, enabledCipherSuites.length);
        assertEquals(cipherSuites.get(0), enabledCipherSuites[0]);
        assertEquals(cipherSuites.get(1), enabledCipherSuites[1]);
    }

    @Test
    public void test_createSslEngine_protocol() throws Exception {

        System.out.println(getEnabledProtocols());

        final TrustManagerFactory tmf = null;

        final ImmutableList<String> protocol = ImmutableList.of("TLSv1");
        final TestSslConfig sslConfig = new TestSslConfig(null, tmf, null, protocol, 0);

        final SSLEngine sslEngine = SslUtil.createSslEngine(embeddedChannel, sslConfig);

        assertNotNull(sslEngine);

        final String[] enabledProtocols = sslEngine.getEnabledProtocols();

        assertEquals(1, enabledProtocols.length);
        assertEquals(protocol.get(0), enabledProtocols[0]);
    }

    @Test
    public void test_createSslEngine_multiple_protocols() throws Exception {

        final TrustManagerFactory tmf = null;

        final ImmutableList<String> protocols = ImmutableList.of("TLSv1.1", "TLSv1.2");
        final TestSslConfig sslConfig = new TestSslConfig(null, tmf, null, protocols, 0);

        final SSLEngine sslEngine = SslUtil.createSslEngine(embeddedChannel, sslConfig);

        assertNotNull(sslEngine);

        final String[] enabledProtocols = sslEngine.getEnabledProtocols();

        assertEquals(2, enabledProtocols.length);
        assertEquals(protocols.get(0), enabledProtocols[0]);
        assertEquals(protocols.get(1), enabledProtocols[1]);
    }


    private ImmutableList<String> getFirstSupportedCipherSuite() throws Exception {

        final List<String> supportedCipherSuites = getEnabledCipherSuites();

        final List<String> valueList = new ArrayList<>();
        valueList.add(supportedCipherSuites.get(0));

        return ImmutableList.copyOf(valueList);
    }

    private ImmutableList<String> getOtherSupportedCipherSuites() throws Exception {

        final List<String> supportedCipherSuites = getEnabledCipherSuites();

        final List<String> valueList = new ArrayList<>();
        valueList.add(supportedCipherSuites.get(1));
        valueList.add(supportedCipherSuites.get(2));

        return ImmutableList.copyOf(valueList);
    }

    private List<String> getEnabledCipherSuites() throws Exception {
        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        final SSLEngine sslEngine = context.createSSLEngine();
        return Arrays.asList(sslEngine.getEnabledCipherSuites());
    }

    private List<String> getEnabledProtocols() throws Exception {
        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        final SSLEngine sslEngine = context.createSSLEngine();
        return Arrays.asList(sslEngine.getEnabledProtocols());
    }

    private static class TestSslConfig implements MqttClientSslConfig {

        private final KeyManagerFactory keyManagerFactory;
        private final TrustManagerFactory trustManagerFactory;
        private final ImmutableList<String> cipherSuites;
        private final ImmutableList<String> protocols;
        private final int handshakeTimeout;

        private TestSslConfig(
                final KeyManagerFactory keyManagerFactory, final TrustManagerFactory trustManagerFactory,
                final ImmutableList<String> cipherSuites, final ImmutableList<String> protocols, final int handshakeTimeout) {
            this.keyManagerFactory = keyManagerFactory;
            this.trustManagerFactory = trustManagerFactory;
            this.cipherSuites = cipherSuites;
            this.protocols = protocols;
            this.handshakeTimeout = handshakeTimeout;
        }

        @Override
        public KeyManagerFactory getKeyManagerFactory() {
            return keyManagerFactory;
        }

        @Override
        public TrustManagerFactory getTrustManagerFactory() {
            return trustManagerFactory;
        }

        @Override
        public ImmutableList<String> getCipherSuites() {
            return cipherSuites;
        }

        @Override
        public ImmutableList<String> getProtocols() {
            return protocols;
        }

        @Override
        public long getHandshakeTimeoutMs() {
            return handshakeTimeout;
        }
    }

}