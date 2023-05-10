package com.key.jorigin.netty.test1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //create ServerBootstrap instance
            ServerBootstrap b = new ServerBootstrap();
            //Specifies NIO transport, local socket address
            //Adds handler to channel pipeline
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            //Binds server, waits for server to close, and releases resources
            System.out.println("server -----> b.bind start");
            ChannelFuture f = b.bind().sync();
            System.out.println("server -----> b.bind end");

            System.out.println(EchoServer.class.getName() + "started and listen on " + f.channel().localAddress());

            System.out.println("server -----> f.channel.closeFuture start");
            f.channel().closeFuture().sync();

            //上面被阻塞了，这里不会执行到
            System.out.println("server -----> f.channel.closeFuture end");

        } finally {
            System.out.println("server -----> group.shutdownGracefully start");
            group.shutdownGracefully().sync();
            System.out.println("server -----> group.shutdownGracefully end");
        }

        /**
         * 最后绑定服务器等待直到绑定完成,调用sync()方法会阻塞直到服务器完成绑定,
         * 然后服务器等待通道关闭,因为使用sync(),所以关闭操作也 会被阻塞。
         * 现在你可以关闭EventLoopGroup和释放所有资源,包括创建的线程。
         这个例子中使用NIO,因为它是目前最常用的传输方式,你可能会使用NIO很长时间,
         但是你可以选择不同的传输实现。例如,这个例子使用 OIO方式传输,你需要指定OioServerSocketChannel。
         Netty框架中实现了多重传输方式,将再后面讲述
         */
    }

    public static void main(String[] args) throws Exception {
        new EchoServer(8080).start();
    }
}