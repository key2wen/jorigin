package com.key.jorigin.netty.test1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * 服务器写好了,现在来写一个客户端连接服务器。应答程序的客户端包括以下几步:
 * 连接服务器
 * 写数据到服务器
 * 等待接受服务器返回相同的数据
 * 关闭连接
 * <p>
 * <p>
 * 创建启动一个客户端包含下面几步:
 * 创建Bootstrap对象用来引导启动客户端 创建EventLoopGroup对象并设置到Bootstrap中,EventLoopGroup可以理解为是一个线程池,
 * 这个线程池用来处理连接、接受数据、发送数据 创建InetSocketAddress并设置到Bootstrap中,
 * InetSocketAddress是指定连接的服务器地址 添加一个ChannelHandler,客户端成功连接服务器后就会被执行
 * 调用Bootstrap.connect()来连接服务器
 * 最后关闭EventLoopGroup来释放资源
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {

        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            System.out.println("client -----> b.connect start");
            ChannelFuture f = b.connect().sync();
            System.out.println("client -----> b.connect end");


            System.out.println("client -----> f.channel.closeFuture start");
            f.channel().closeFuture().sync();

            /**
             *  当在EchoClientHandler channelActive方法中
             *   1. 使用 ctx.write(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
             *   那么server会收不到消息，client 的 EchoClientHandler(SimpleChannelInboundHandler)
             *   的channelRead方法也就不会执行，也就不会执行释放资源，不能释放资源，上面的
             *   f.channel().closeFuture().sync() 代码就一直阻塞，下面的代码就执行不了，程序不能推出；
             *   同时，拿不到数据，channelRead0方法也就执行不到
             *
             *   2. 使用 ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));时
             *   那么上面的问题就不会存在，client进程也能正常结束
             *
             *   可以发现 writeAndFlush 方法会清空发送通道 消息才能达到server
             */
            //这里执行不到 ，被上面阻塞了
            System.out.println("client -----> f.channel.closeFuture end");

        } finally {
            System.out.println("client -----> group.shutdownGracefully start");
            group.shutdownGracefully().sync();
            System.out.println("client -----> group.shutdownGracefully end");
        }
    }

    public static void main(String[] args) throws Exception {
        new EchoClient("localhost", 8080).start();
    }
}