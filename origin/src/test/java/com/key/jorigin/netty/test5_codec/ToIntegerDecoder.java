package com.key.jorigin.netty.test5_codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * Created by shuguang on 18/1/15.
 */
public class ToIntegerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //int 4个字节 , short/char 2 个字节
        if (in.readableBytes() >= 4) {
            out.add(in.readInt());
        }
        /**
         * 从上面的代码可能会发现,我们需要检查ByteBuf读之前是否有足够的字节,若没有这个检查岂不更好?是的,
         * Netty提供了这样的处理允 许byte-to-message解码,在下一节讲解。除了ByteToMessageDecoder之外,
         * Netty还提供了许多其他的解码接口。
         */
    }
    /**
     * 7.2.2 ReplayingDecoder
     ReplayingDecoder是byte-to-message解码的一种特殊的抽象基类,读取缓冲区的数据之前需要检查缓冲区是否有足够的字节,
     使 用ReplayingDecoder就无需自己检查;若ByteBuf中有足够的字节,则会正常读取;若没有足够的字节则会停止解码。
     也正因为这样的包 装使得ReplayingDecoder带有一定的局限性。
     不是所有的操作都被ByteBuf支持,如果调用一个不支持的操作会抛出DecoderException。
     ByteBuf.readableBytes()大部分时间不会返回期望值
     如果你能忍受上面列出的限制,相比ByteToMessageDecoder,你可能更喜欢ReplayingDecoder。
     在满足需求的情况下推荐使用 ByteToMessageDecoder,因为它的处理比较简单,没有ReplayingDecoder实现的那么复杂。
     ReplayingDecoder继承与 ByteToMessageDecoder,所以他们提供的接口是相同的。下面代码是ReplayingDecoder的实现:
     */
}

class ToIntegerReplayingDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        out.add(in.readInt());
    }
    /**
     * 当从接收的数据ByteBuf读取integer,若没有足够的字节可读,decode(...)会停止解码,若有足够的字节可读,则会读取数据添加到 List列表中。使用ReplayingDecoder或ByteToMessageDecoder是个人喜好的问题,Netty提供了这两种实现,选择哪一个都可以。
     上面讲了byte-to-message的解码实现方式,那message-to-message该如何实现呢?Netty提供了MessageToMessageDecoder抽象 类。
     7.2.3 MessageToMessageDecoder
     将消息对象转成消息对象可是使用MessageToMessageDecoder,它是一个抽象类,需要我们自己实现其decode(...)。
     message-to- message同上面讲的byte-to-message的处理机制一样,看下图:
     */
}

class IntegerToStringDecoder extends MessageToMessageDecoder<Integer> {

    @Override
    protected void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        out.add(String.valueOf(msg));
    }
    /**
     * 7.2.4 解码器总结 解码器是用来处理入站数据,Netty提供了很多解码器的实现,可以根据需求详细了解。那我们发送数据需要将数据编码,Netty中
     也提供了编码器的支持。下一节将讲解如何实现编码器
     */
}
