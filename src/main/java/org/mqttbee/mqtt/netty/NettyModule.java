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

package org.mqttbee.mqtt.netty;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import io.netty.channel.epoll.Epoll;

import javax.inject.Singleton;

/**
 * @author Silvio Giebl
 */
@Module
public abstract class NettyModule {

    @Provides
    @Singleton
    static NettyEventLoopProvider provideNettyEventLoopProvider(
            final Lazy<NettyNioEventLoopProvider> nioBootstrapLazy,
            final Lazy<NettyEpollEventLoopProvider> epollBootstrapLazy) {

        if (Epoll.isAvailable()) {
            return epollBootstrapLazy.get();
        } else {
            return nioBootstrapLazy.get();
        }
    }

}
