package com.key.jorigin.netty.test9_websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 初始化ChannelPipeline
 * <p>
 * WebSocket,初始化ChannelHandler
 * <p>
 * WebSocketServerProtcolHandler不仅处理Ping/Pong/CloseWebSocketFrame,还和它自己握手并帮助升级WebSocket。
 * 这是执行完成握手和成功修改ChannelPipeline,并且添加需要的编码器/解码器和删除不需要的ChannelHandler。
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {

    private final ChannelGroup group;

    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        //编解码http请求  out/inbound
        pipeline.addLast(new HttpServerCodec());

        //写文件内容 out/inbound
        pipeline.addLast(new ChunkedWriteHandler());

        //聚合解码HttpRequest/HttpContent/LastHttpContent到FullHttpRequest
        //保证接收的Http请求的完整性   inbound
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));

        //处理FullHttpRequest  inbound
        pipeline.addLast(new HttpRequestHandler("/ws"));

        //处理其他的WebSocketFrame  inbound
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        //处理TextWebSocketFrame  inbound
        pipeline.addLast(new TextWebSocketFrameHandler(group));
    }
}
/**
 * ChannelPipeline通过ChannelInitializer的initChannel(...)方法完成初始化,完成握手后就会更改事情。
 * 一旦这样做 了,WebSocketServerProtocolHandler将取代HttpRequestDecoder、WebSocketFrameDecoder13和
 * HttpResponseEncoder、 WebSocketFrameEncoder13。另外也要删除所有不需要的ChannelHandler已获得最佳性能。
 * 这些都是HttpObjectAggregator和 HttpRequestHandler。下图显示ChannelPipeline握手完成:
 * <p>
 * 我们甚至没注意到它,因为它是在底层执行的。以非常灵活的方式动态更新ChannelPipeline让单独的任务在不同的ChannelHandler 中实现。
 */