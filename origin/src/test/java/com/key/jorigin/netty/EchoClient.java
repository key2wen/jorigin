package com.key.jorigin.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoClient {

    private final String host;
    private final int port;
    private final int firstMessageSize;

    public EchoClient(String host, int port, int firstMessageSize) {
        this.host = host;
        this.port = port;
        this.firstMessageSize = firstMessageSize;
    }

    public void run() throws Exception {
        // Configure the client.  
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    //刻不容缓
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    //new LoggingHandler(LogLevel.INFO),
                                    new EchoClientHandler(firstMessageSize));
                        }
                    });

            // Start the client.  
            ChannelFuture f = b.connect(host, port).sync();

            // Wait until the connection is closed.  
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.  
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        final String host = "127.0.0.1";
        final int firstMessageSize = 2;

        new EchoClient(host, 8080, firstMessageSize).run();

        ByteBuf byteBuf = null;
    }
}
