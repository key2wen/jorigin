package com.key.jorigin.netty.test8_nettyTest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ThreadPoolExecutor;

public class FixedLengthFrameDecoderTest {
    @Test
    public void testFramesDecoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();

        buf.readBytes(3);

//        System.out.print(input.capacity());

        EmbeddedChannel channel = new EmbeddedChannel(
                new FixedLengthFrameDecoder(3));
        // write bytes
        Assert.assertTrue(channel.writeInbound(input));
        Assert.assertTrue(channel.finish());
        // read message
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertNull(channel.readInbound());
    }

    /**
     * 如上面代码,testFramesDecoded()方法想测试一个ByteBuf,这个ByteBuf包含9个可读字节,被解码成包含了3个可读字节的 ByteBuf。
     * 你可能注意到,它写入9字节到通道是通过调用writeInbound()方法,之后再执行finish()来将EmbeddedChannel标记为已完成,
     * 最后调用readInbound()方法来获取EmbeddedChannel中的数据,直到没有可读字节。
     * testFramesDecoded2()方法采取同样的方式,
     * 但有 一个区别就是入站ByteBuf分两步写的,当调用writeInbound(input.readBytes(2))后返回false时,
     * FixedLengthFrameDecoder值会产生输 出,至少有3个字节是可读,testFramesDecoded2()测试的工作相当于testFramesDecoded()。
     */

    @Test
    public void testFramesDecoded2() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();

        buf.readBytes(3);

        EmbeddedChannel channel = new EmbeddedChannel(
                new FixedLengthFrameDecoder(3));

        Assert.assertFalse(channel.writeInbound(input.readBytes(2)));
        Assert.assertTrue(channel.writeInbound(input.readBytes(7)));

        Assert.assertTrue(channel.finish());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
        Assert.assertEquals(buf.readBytes(3), channel.readInbound());
    }
}