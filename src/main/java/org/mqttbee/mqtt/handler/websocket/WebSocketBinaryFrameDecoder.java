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

package org.mqttbee.mqtt.handler.websocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * @author David Katz
 * @author Silvio Giebl
 */
@ChannelHandler.Sharable
@Singleton
public class WebSocketBinaryFrameDecoder extends MessageToMessageDecoder<WebSocketFrame> {

    public static final String NAME = "ws.decoder";

    @Inject
    WebSocketBinaryFrameDecoder() {
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final WebSocketFrame msg, final List<Object> out) {
        if (msg instanceof BinaryWebSocketFrame) {
            out.add(msg.retain().content());
        }
    }

}
