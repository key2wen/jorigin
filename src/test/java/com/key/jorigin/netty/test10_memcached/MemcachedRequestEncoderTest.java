package com.key.jorigin.netty.test10_memcached;

import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * 编码器和解码器完成,但仍有一些缺失:测试。
 * 没有测试你只看到如果编解码器工作对一些真正的服务器运行时,这并不是你应该是依靠什么。第十章所示,为一个自定义编写测试
 * ChannelHandler通常是通过 EmbeddedChannel。
 * 所以这正是现在做测试我们定制的编解码器,其中包括一个编码器和解码器。让重新开始编码器。后面的清单显示了简单的编写单元测试。
 */

public class MemcachedRequestEncoderTest {
    @Test
    public void testMemcachedRequestEncoder() {
        MemcachedRequest request = new MemcachedRequest(Opcode.SET, "key1", "value1"); //1

        EmbeddedChannel channel = new EmbeddedChannel(new MemcachedRequestEncoder());  //2
        channel.writeOutbound(request); //3

        ByteBuf encoded = (ByteBuf) channel.readOutbound();

        Assert.assertNotNull(encoded);  //4
        Assert.assertEquals(request.magic(), encoded.readUnsignedByte());  //5
        Assert.assertEquals(request.opCode(), encoded.readByte());  //6
        Assert.assertEquals(4, encoded.readShort());//7
        Assert.assertEquals((byte) 0x08, encoded.readByte()); //8
        Assert.assertEquals((byte) 0, encoded.readByte());//9
        Assert.assertEquals(0, encoded.readShort());//10
        Assert.assertEquals(4 + 6 + 8, encoded.readInt());//11
        Assert.assertEquals(request.id(), encoded.readInt());//12
        Assert.assertEquals(request.cas(), encoded.readLong());//13
        Assert.assertEquals(request.flags(), encoded.readInt()); //14
        Assert.assertEquals(request.expires(), encoded.readInt()); //15

        byte[] data = new byte[encoded.readableBytes()]; //16
        encoded.readBytes(data);
        Assert.assertArrayEquals((request.key() + request.body()).getBytes(CharsetUtil.UTF_8), data);
        Assert.assertFalse(encoded.isReadable());  //17

        Assert.assertFalse(channel.finish());
        Assert.assertNull(channel.readInbound());
    }
}