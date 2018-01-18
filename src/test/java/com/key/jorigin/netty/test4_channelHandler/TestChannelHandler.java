package com.key.jorigin.netty.test4_channelHandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;


/**
 * Created by shuguang on 18/1/15.
 */
public class TestChannelHandler extends ChannelInitializer<SocketChannel> {

    /**
     * 如果你想有一些事件流全部通过ChannelPipeline,有两个不同的方法可以做到: 调用Channel的方法
     * 调用ChannelPipeline的方法
     * 这两个方法都可以让事件流全部通过ChannelPipeline。无论从头部还是尾部开始,因为它主要依赖于事件的性质。
     * 如果是一个“入 站”事件,它开始于头部;若是一个“出站”事件,则开始于尾部。
     * 下面的代码显示了一个写事件如何通过ChannelPipeline从尾部开始
     *
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                //Event via Channel
                Channel channel = ctx.channel();
                channel.write(Unpooled.copiedBuffer("netty in action", CharsetUtil.UTF_8));
                //Event via ChannelPipeline
                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.write(Unpooled.copiedBuffer("netty in action", CharsetUtil.UTF_8));


                /**
                 * 可能你想从ChannelPipeline的指定位置开始,不想流经整个ChannelPipeline,如下情况:
                 * 为了节省开销,不感兴趣的ChannelHandler不让通过
                 排除一些ChannelHandler 在这种情况下,你可以使用ChannelHandlerContext的ChannelHandler通知起点。
                 它使用ChannelHandlerContext执行下一个ChannelHandler。下面代码显示了直接使用ChannelHandlerContext操作:

                 该消息流经ChannelPipeline到下一个ChannelHandler,在这种情况下使用ChannelHandlerContext开始下一个ChannelHandler
                 */
                // Get reference of ChannelHandlerContext
                // Write buffer via ChannelHandlerContext
                ctx.write(Unpooled.copiedBuffer("Netty in Action", CharsetUtil.UTF_8));


            }
        }).addLast(new WriteHandler())
        ;
    }

}

/**
 * 调用ChannelHandlerContext的pipeline()方法能访问ChannelPipeline,能在运行时动态的增加、删除、替换ChannelPipeline中的 ChannelHandler。
 * 可以保持ChannelHandlerContext供以后使用,如外部Handler方法触发一个事件,甚至从一个不同的线程。
 * 下面代码显示了保存ChannelHandlerContext供之后使用或其他线程使用
 */
class WriteHandler extends ChannelHandlerAdapter {
    private ChannelHandlerContext ctx;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    public void send(String msg) {
        ctx.write(msg);
    }
}

/**
 * 请注意,ChannelHandler实例如果带有@Sharable注解则可以被添加到多个ChannelPipeline。也就是说单个ChannelHandler实例可 以
 * 有多个ChannelHandlerContext,因此可以调用不同ChannelHandlerContext获取同一个ChannelHandler。如果添加不带@Sharable注解
 * 的ChannelHandler实例到多个ChannelPipeline则会抛出异常;使用@Sharable注解后的ChannelHandler必须在不同的线程和不同的通道
 * 上安全使用。怎么是不安全的使用?看下面代码:
 */
@ChannelHandler.Sharable
class NotSharableHandler extends ChannelInboundHandlerAdapter {

    private int count;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        count++;
        System.out.println("channelRead(...) called the " + count + " time");
        ctx.fireChannelRead(msg);
    }
/**
 * 为什么要共享ChannelHandler?使用@Sharable注解共享一个ChannelHandler在一些需求中还是有很好的作用的,如使用一个
 ChannelHandler来统计连接数或来处理一些全局数据等等
 */
}
