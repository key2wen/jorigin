package com.key.jorigin.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @link : https://www.jianshu.com/p/1123c9164e3e
 * <p>
 * 1、NioEventLoopGroup 是用来处理I/O操作的线程池，Netty对 EventLoopGroup 接口针对不同的传输协议提供了不同的实现。
 * 在本例子中，需要实例化两个NioEventLoopGroup，通常第一个称为“boss”，用来accept客户端连接，另一个称为“worker”，
 * 处理客户端数据的读写操作。
 * 2、ServerBootstrap 是启动服务的辅助类，有关socket的参数可以通过ServerBootstrap进行设置。
 * 3、这里指定NioServerSocketChannel类初始化channel用来接受客户端请求。
 * 4、通常会为新SocketChannel通过添加一些handler，来设置ChannelPipeline。ChannelInitializer 是一个特殊的handler，
 * 其中initChannel方法可以为SocketChannel 的pipeline添加指定handler。
 * 5、通过绑定端口8080，就可以对外提供服务了。
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup();  // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    //积压
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    //new LoggingHandler(LogLevel.INFO),
                                    new EchoServerHandler());
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(port).sync(); // (5)

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new EchoServer(port).run();
    }
}
