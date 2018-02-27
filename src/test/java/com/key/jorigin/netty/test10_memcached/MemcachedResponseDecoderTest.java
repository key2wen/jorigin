package com.key.jorigin.netty.test10_memcached;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import org.junit.Assert;
import org.junit.Test;

public class MemcachedResponseDecoderTest {

    @Test
    public void testMemcachedResponseDecoder() {
        EmbeddedChannel channel = new EmbeddedChannel(new MemcachedResponseDecoder());  //1

        byte magic = 1;
        byte opCode = Opcode.SET;

        byte[] key = "Key1".getBytes(CharsetUtil.US_ASCII);
        byte[] body = "Value".getBytes(CharsetUtil.US_ASCII);
        int id = (int) System.currentTimeMillis();
        long cas = System.currentTimeMillis();

        ByteBuf buffer = Unpooled.buffer(); //2
        buffer.writeByte(magic);
        buffer.writeByte(opCode);
        buffer.writeShort(key.length);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeShort(Status.KEY_EXISTS);
        buffer.writeInt(body.length + key.length);
        buffer.writeInt(id);
        buffer.writeLong(cas);
        buffer.writeBytes(key);
        buffer.writeBytes(body);

        Assert.assertTrue(channel.writeInbound(buffer));  //3

        MemcachedResponse response = (MemcachedResponse) channel.readInbound();
        assertResponse(response, magic, opCode, Status.KEY_EXISTS, 0, 0, id, cas, key, body);//4
    }

    @Test
    public void testMemcachedResponseDecoderFragments() {
        EmbeddedChannel channel = new EmbeddedChannel(new MemcachedResponseDecoder()); //5

        byte magic = 1;
        byte opCode = Opcode.SET;

        byte[] key = "Key1".getBytes(CharsetUtil.US_ASCII);
        byte[] body = "Value".getBytes(CharsetUtil.US_ASCII);
        int id = (int) System.currentTimeMillis();
        long cas = System.currentTimeMillis();

        ByteBuf buffer = Unpooled.buffer(); //6
        buffer.writeByte(magic);
        buffer.writeByte(opCode);
        buffer.writeShort(key.length);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeShort(Status.KEY_EXISTS);
        buffer.writeInt(body.length + key.length);
        buffer.writeInt(id);
        buffer.writeLong(cas);
        buffer.writeBytes(key);
        buffer.writeBytes(body);

        ByteBuf fragment1 = buffer.readBytes(8); //7
        ByteBuf fragment2 = buffer.readBytes(24);
        ByteBuf fragment3 = buffer;

        Assert.assertFalse(channel.writeInbound(fragment1));  //8
        Assert.assertFalse(channel.writeInbound(fragment2));  //9
        Assert.assertTrue(channel.writeInbound(fragment3));  //10

        MemcachedResponse response = (MemcachedResponse) channel.readInbound();
        assertResponse(response, magic, opCode, Status.KEY_EXISTS, 0, 0, id, cas, key, body);//11
    }

    private static void assertResponse(MemcachedResponse response, byte magic, byte opCode, short status, int expires, int flags, int id, long cas, byte[] key, byte[] body) {
        Assert.assertEquals(magic, response.magic());
        Assert.assertArrayEquals(key, response.key().getBytes(CharsetUtil.US_ASCII));
        Assert.assertEquals(opCode, response.opCode());
        Assert.assertEquals(status, response.status());
        Assert.assertEquals(cas, response.cas());
        Assert.assertEquals(expires, response.expires());
        Assert.assertEquals(flags, response.flags());
        Assert.assertArrayEquals(body, response.data().getBytes(CharsetUtil.US_ASCII));
        Assert.assertEquals(id, response.id());
    }

    /**
     * 新建 EmbeddedChannel ，持有 MemcachedResponseDecoder 到测试
     创建一个新的 Buffer 并写入数据，与二进制协议的结构相匹配
     写缓冲区到 EmbeddedChannel 和检查是否一个新的MemcachedResponse 创建由声明返回值
     判断 MemcachedResponse 和预期的值
     创建一个新的 EmbeddedChannel 持有 MemcachedResponseDecoder 到测试
     创建一个新的 Buffer 和写入数据的二进制协议的结构相匹配
     缓冲分割成三个片段
     写的第一个片段 EmbeddedChannel 并检查,没有新的MemcachedResponse 创建,因为并不是所有的数据都是准备好了
     写第二个片段 EmbeddedChannel 和检查,没有新的MemcachedResponse 创建,因为并不是所有的数据都是准备好了
     写最后一段到 EmbeddedChannel 和检查新的 MemcachedResponse 是否创建，因为我们终于收到所有数据
     判断 MemcachedResponse 与预期的值


     总结

     阅读本章后,您应该能够创建自己的编解码器针对你最喜欢的协议。这包括写编码器和解码器,从字节转换为你的 POJO,反之亦然。这一章展示了如何使用一个协议规范实现和提取所需的信息。
     它还向您展示了如何编写单元测试完成你的工作的编码器和解码器,确保一切工作如预期而不需要一个完整的 Memcached 服务器运行。这允许轻松集成测试到构建系统的中。
     */
}