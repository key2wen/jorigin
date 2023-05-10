package com.key.jorigin.netty.test1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * 实现客户端的业务逻辑 客户端的业务逻辑的实现依然很简单,更复杂的用法将在后面章节详细介绍。
 * 和编写服务器的ChannelHandler一样,在这里将自定义一个继承
 * SimpleChannelInboundHandler的ChannelHandler来处理业务;通过重写父类的三个方法来处理感兴趣的事件:
 * channelActive():客户端连接服务器后被调用
 * channelRead0():从服务器接收到数据后调用
 * exceptionCaught():发生异常时被调用
 * <p>
 * <p>
 * 可能你会问为什么在这里使用的是SimpleChannelInboundHandler而不使用ChannelInboundHandlerAdapter?
 * 主要原因是 ChannelInboundHandlerAdapter在处理完消息后需要负责释放资源。在这里将调用ByteBuf.release()来释放资源。
 * SimpleChannelInboundHandler会在 完成channelRead0后释放消息,这是通过Netty处理所有消息的ChannelHandler
 * 实现了ReferenceCounted接口达到的。
 * <p>
 * 为什么在服务器中不使用SimpleChannelInboundHandler呢?因为服务器要返回相同的消息给客户端,在服务器执行完成写操作之前不能释放调
 * 用读取到的消息,因为写操作是异步的,一旦写操作完成后,Netty中会自动释放消息。 客户端的编写完了,下面让我们来测试一下
 * <p>
 * 本章介绍了如何编写一个简单的基于Netty的服务器和客户端并进行通信发送数据。介绍了如何创建服务器和客户端以及Netty的异常处理机制
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client ----> channelActive!");
//        ctx.write(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));

//        repeatSend(ctx);
    }

    private void repeatSend(ChannelHandlerContext ctx) {
        Thread t = new Thread(new Runnable() {
            @Autowired
            public void run() {
                while (true) {
                    ctx.write(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
                    try {
                        int s = new Random().nextInt(5);
                        System.out.println("client ----> sleep " + s);
                        TimeUnit.SECONDS.sleep(s);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("Client received: " + ByteBufUtil.hexDump(msg.readBytes(msg.readableBytes())));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}