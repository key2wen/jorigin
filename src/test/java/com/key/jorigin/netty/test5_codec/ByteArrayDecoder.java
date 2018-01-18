package com.key.jorigin.netty.test5_codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.*;
import io.netty.handler.codec.http.websocketx.*;

import java.util.List;

/**
 * 7.4 编解码器 实际编码中,一般会将编码和解码操作封装太一个类中,解码处理“入站”数据,编码处理“出站”数据。知道了编码和解码器,对于下
 * 面的情况不会感觉惊讶: byte-to-message编码和解码
 * message-to-message编码和解码
 * 如果确定需要在ChannelPipeline中使用编码器和解码器,需要更好的使用一个抽象的编解码器。同样,使用编解码器的时候,
 * 不可 能只删除解码器或编码器而离开ChannelPipeline导致某种不一致的状态。使用编解码器将强制性的要么都在ChannelPipeline,要么都不
 * 在ChannelPipeline。
 * <p>
 * 考虑到这一点,我们在下面几节将更深入的分析Netty提供的编解码抽象类。
 * 7.4.1 byte-to-byte编解码器 Netty4较之前的版本,其结构有很大的变化,在Netty4中实现byte-to-byte提供了2个类:ByteArrayEncoder和ByteArrayDecoder。
 * 这两个类用来处理字节到字节的编码和解码。下面是这两个类的源码,一看就知道是如何处理的:
 */
public class ByteArrayDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {

        // copy the ByteBuf content to a byte array
        byte[] array = new byte[msg.readableBytes()];
        msg.getBytes(0, array);

        out.add(array);
    }
}

@ChannelHandler.Sharable
class ByteArrayEncoder extends MessageToMessageEncoder<byte[]> {
    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
        out.add(Unpooled.wrappedBuffer(msg));
    }

    /**
     * 7.4.2 ByteToMessageCodec
     ByteToMessageCodec用来处理byte-to-message和message-to-byte。如果想要解码字节消息成POJO或编码POJO消息成字节,对 于这种情况,
     ByteToMessageCodec<I>是一个不错的选择。ByteToMessageCodec是一种组合,其等同于ByteToMessageDecoder和 MessageToByteEncoder的
     组合。MessageToByteEncoder是个抽象类,其中有2个方法需要我们自己实现:
     encode(ChannelHandlerContext, I, ByteBuf),编码 decode(ChannelHandlerContext, ByteBuf, List<Object>),解码
     7.4.3 MessageToMessageCodec
     MessageToMessageCodec用于message-to-message的编码和解码,可以看成是MessageToMessageDecoder和 MessageToMessageEncoder的组合体。MessageToMessageCodec是抽象类,其中有2个方法需要我们自己实现:
     encode(ChannelHandlerContext, OUTBOUND_IN, List<Object>) decode(ChannelHandlerContext, INBOUND_IN, List<Object>)
     但是,这种编解码器能有用吗? 有许多用例,最常见的就是需要将消息从一个API转到另一个API。这种情况下需要自定义API或旧的API使用另一种消息类型。下面
     的代码显示了在WebSocket框架APIs之间转换消息:
     */

}


@ChannelHandler.Sharable
class WebSocketConvertHandler extends
        MessageToMessageCodec<WebSocketFrame, WebSocketConvertHandler.MyWebSocketFrame> {
    public static final WebSocketConvertHandler INSTANCE = new WebSocketConvertHandler();

    @Override
    protected void encode(ChannelHandlerContext ctx, MyWebSocketFrame msg, List<Object> out) throws Exception {
        switch (msg.getType()) {
            case BINARY:
                out.add(new BinaryWebSocketFrame(msg.getData()));
                break;
            case CLOSE:
                out.add(new CloseWebSocketFrame(true, 0, msg.getData()));
                break;
            case PING:
                out.add(new PingWebSocketFrame(msg.getData()));
                break;
            case PONG:
                out.add(new PongWebSocketFrame(msg.getData()));
                break;
            case TEXT:
                out.add(new TextWebSocketFrame(msg.getData()));
                break;
            case CONTINUATION:
                out.add(new ContinuationWebSocketFrame(msg.getData()));
                break;
            default:
                throw new IllegalStateException("Unsupported websocket msg " + msg);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        if (msg instanceof BinaryWebSocketFrame) {
            out.add(new MyWebSocketFrame(MyWebSocketFrame.FrameType.BINARY, msg.content().copy()));
            return;
        }
        if (msg instanceof CloseWebSocketFrame) {
            out.add(new MyWebSocketFrame(MyWebSocketFrame.FrameType.CLOSE, msg.content().copy()));
            return;
        }
        if (msg instanceof PingWebSocketFrame) {
            out.add(new MyWebSocketFrame(MyWebSocketFrame.FrameType.PING, msg.content().copy()));
            return;
        }
        if (msg instanceof PongWebSocketFrame) {
            out.add(new MyWebSocketFrame(MyWebSocketFrame.FrameType.PONG, msg.content().copy()));
            return;
        }
        if (msg instanceof TextWebSocketFrame) {
            out.add(new MyWebSocketFrame(MyWebSocketFrame.FrameType.TEXT, msg.content().copy()));
            return;
        }
        if (msg instanceof ContinuationWebSocketFrame) {
            out.add(new MyWebSocketFrame(MyWebSocketFrame.FrameType.CONTINUATION, msg.content().copy()));
            return;
        }
        throw new IllegalStateException("Unsupported websocket msg " + msg);
    }

    public static final class MyWebSocketFrame {
        public enum FrameType {
            BINARY, CLOSE, PING, PONG, TEXT, CONTINUATION
        }

        private final FrameType type;
        private final ByteBuf data;

        public MyWebSocketFrame(FrameType type, ByteBuf data) {
            this.type = type;
            this.data = data;
        }

        public FrameType getType() {
            return type;
        }

        public ByteBuf getData() {
            return data;
        }
    }

    /**
     * 7.5 其他编解码方式 使用编解码器来充当编码器和解码器的组合失去了单独使用编码器或解码器的灵活性,编解码器是要么都有要么都没有。你可能想
     知道是否有解决这个僵化问题的方式,还可以让编码器和解码器在ChannelPipeline中作为一个逻辑单元。幸运的是,Netty提供了一种解
     决方案,使用CombinedChannelDuplexHandler。虽然这个类不是编解码器API的一部分,但是它经常被用来简历一个编解码器。

     7.5.1 CombinedChannelDuplexHandler
     如何使用CombinedChannelDuplexHandler来结合解码器和编码器呢?下面我们从两个简单的例子看了解
     */
}

class ByteToCharDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 2) {
            out.add(Character.valueOf(in.readChar()));
        }
    }
}

class CharToByteEncoder extends MessageToByteEncoder<Character> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Character msg, ByteBuf out) throws Exception {
        out.writeChar(msg);
    }
}

class CharCodec extends CombinedChannelDuplexHandler<ByteToCharDecoder, CharToByteEncoder> {
    public CharCodec() {
        super(new ByteToCharDecoder(), new CharToByteEncoder());
    }
    /**
     * 从上面代码可以看出,使用CombinedChannelDuplexHandler绑定解码器和编码器很容易实现,比使用*Codec更灵活。 Netty还提供了其他的协议支持,放在io.netty.handler.codec包下,如:
     Google的protobuf,在io.netty.handler.codec.protobuf包下
     Google的SPDY协议
     RTSP(Real Time Streaming Protocol,实时流传输协议),在io.netty.handler.codec.rtsp包下
     SCTP(Stream Control Transmission Protocol,流控制传输协议),在io.netty.handler.codec.sctp包下 ......
     */
}