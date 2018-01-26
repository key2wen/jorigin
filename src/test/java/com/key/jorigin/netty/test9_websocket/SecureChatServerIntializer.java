package com.key.jorigin.netty.test9_websocket;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * 上面的应用程序虽然工作的很好,但是在网络上收发消息存在很大的安全隐患,所以有必要对消息进行加密。添加这样一个加密的
 * 功能一般比较复杂,需要对代码有较大的改动。但是使用Netty就可以很容易的添加这样的功能,
 * 只需要将SslHandler加入到 ChannelPipeline中就可以了。实际上还需要添加SslContext,但这不在本例子范围内。
 * 首先我们创建一个用于添加加密Handler的handler初始化类,看下面代码:
 */
public class SecureChatServerIntializer extends ChatServerInitializer {

    private final SSLContext context;

    public SecureChatServerIntializer(ChannelGroup group, SSLContext context) {
        super(group);
        this.context = context;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {

        super.initChannel(ch);

        SSLEngine engine = context.createSSLEngine();
        engine.setUseClientMode(false);

        //inbound/ outbound
        ch.pipeline().addFirst(new SslHandler(engine));
    }
}