package com.key.jorigin.netty.test4_channelHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by shuguang on 18/1/15.
 */
public class DiscardOutBoundHandler extends ChannelOutboundHandlerAdapter {

    /**
     * ChannelOutboundHandler
     * ChannelOutboundHandler用来处理“出站”的数据消息。ChannelOutboundHandler提供了下面一些方法:
     * bind,Channel绑定本地地址
     * connect,Channel连接操作
     * disconnect,Channel断开连接
     * close,关闭Channel
     * deregister,注销Channel read,读取消息,实际是截获ChannelHandlerContext.read() write,写操作,实际是通过
     * ChannelPipeline写消息,Channel.flush()属性到实际通道 flush,刷新消息到通道
     * <p>
     * <p>
     * ChannelOutboundHandler是ChannelHandler的子类,实现了ChannelHandler的所有方法。所有最重要的方法采取ChannelPromise,
     * 因此一旦请求停止从ChannelPipeline转发参数则必须得到通知。Netty提供了ChannelOutboundHandler的实 现:ChannelOutboundHandlerAdapter。
     * ChannelOutboundHandlerAdapter实现了父类的所有方法,并且可以根据需要重写感兴趣的方 法。所有这些方法的实现,在默认情况下,
     * 都是通过调用ChannelHandlerContext的方法将事件转发到ChannelPipeline中下一个 ChannelHandler。
     * <p>
     * <p>
     * 重要的是要记得释放致远并直通ChannelPromise,若ChannelPromise没有被通知可能会导致其中一个ChannelFutureListener不被
     * 通知去处理一个消息。
     * 如果消息被消费并且没有被传递到ChannelPipeline中的下一个ChannelOutboundHandler,那么就需要调用
     * ReferenceCountUtil.release(message)来释放消息资源。
     * 一旦消息被传递到实际的通道,它会自动写入消息或在通道关闭是释放
     */

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ReferenceCountUtil.release(msg);
        promise.setSuccess();
    }
}
