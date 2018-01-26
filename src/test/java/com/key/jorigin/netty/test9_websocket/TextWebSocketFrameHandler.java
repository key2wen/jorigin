package com.key.jorigin.netty.test9_websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * 我们的程序只需要使用下面4个框架:
 * CloseWebSocketFrame PingWebSocketFrame PongWebSocketFrame Te x t W e b S o c k e t F r a m e
 * 我们只需要显示处理TextWebSocketFrame,其他的会自动由WebSocketServerProtocolHandler处理,看下面代码
 * <p>
 * <p>
 * WebSocket,处理消息
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        //如果WebSocket握手完成
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {

            //删除ChannelPipeline中的HttpRequestHandler
            ctx.pipeline().remove(HttpRequestHandler.class);

            // 写一个消息到ChannelGroup
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel()
                    + " joined"));

            //将Channel添加到ChannelGroup
            group.add(ctx.channel());

        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //将接收的消息通过ChannelGroup转发到所以已连接的客户端
        group.writeAndFlush(msg.retain());
    }
}