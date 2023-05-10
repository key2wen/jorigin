package com.key.jorigin.netty.test5_codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 7.3 编码器 Netty提供了一些基类,我们可以很简单的编码器。同样的,编码器有下面两种类型:
 * 消息对象编码成消息对象
 * 消息对象编码成字节码
 * 相对解码器,编码器少了一个byte-to-byte的类型,因为出站数据这样做没有意义。编码器的作用就是将处理好的数据转成字节码以 便在网络中传输。
 * 对照上面列出的两种编码器类型,Netty也分别提供了两个抽象类:MessageToByteEncoder和 MessageToMessageEncoder。下面是类关系图
 * <p>
 * 7.3.1 MessageToByteEncoder
 * MessageToByteEncoder是抽象类,我们自定义一个继承MessageToByteEncoder的编码器只需要实现其提供的encode(...)方法。其 工作流程如下图
 */
public class IntegerToByteEncoder extends MessageToByteEncoder<Integer> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Integer msg, ByteBuf out) throws Exception {
        out.writeInt(msg);
    }
    /**
     * 7.3.2 MessageToMessageEncoder
     需要将消息编码成其他的消息时可以使用Netty提供的MessageToMessageEncoder抽象类来实现。例如将Integer编码成String,其 工作流程如下图:
     */
}

class IntegerToStringEncoder extends MessageToMessageEncoder<Integer> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        out.add(String.valueOf(msg));
    }
}