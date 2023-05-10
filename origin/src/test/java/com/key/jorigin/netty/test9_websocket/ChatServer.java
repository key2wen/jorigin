package com.key.jorigin.netty.test9_websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

/**
 * Created by tbj on 18/1/22.
 * <p>
 * 结合在一起使用 一如既往,我们要将它们结合在一起使用。使用Bootstrap引导服务器和设置正确的ChannelInitializer。看下面代码:
 */
public class ChatServer {


    private final ChannelGroup group = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel;

    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap b = new ServerBootstrap();
        b.group(workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(createInitializer(group));
        ChannelFuture f = b.bind(address).syncUninterruptibly();
        channel = f.channel();
        return f;
    }

    public void destroy() {
        if (channel != null) channel.close();
        group.close();
        workerGroup.shutdownGracefully();
    }

    protected ChannelInitializer<Channel> createInitializer(ChannelGroup group) {
        return new ChatServerInitializer(group);
    }

    public static void main(String[] args) {
        final ChatServer server = new ChatServer();
        ChannelFuture f = server.start(new InetSocketAddress(2048));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.destroy();
            }
        });
        f.channel().closeFuture().syncUninterruptibly();
    }

}

/**
 * 另外需要将index.html放在根目录下
 * 最后在浏览器中输入:http://localhost:2048,多开几个窗口就可以聊天了。
 */
