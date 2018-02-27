package com.key.jorigin.netty.test10_memcached;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * 类负责创建的 MemcachedResponse 读取字节
 * 代表当前解析状态,这意味着我们需要解析的头或 body
 * 根据解析状态切换
 * 如果不是至少24个字节是可读的,它不可能读整个头部,所以返回这里,等待再通知一次数据准备阅读
 * 阅读所有头的字段
 * 检查是否足够的数据是可读用来读取完整的响应的 body。长度是从头读取
 * 检查如果有任何额外的 flag 用于读，如果是这样做
 * 检查如果响应包含一个 expire 字段，有就读它
 * 检查响应是否包含一个 key ,有就读它
 * 读实际的 body 的 payload
 * 从前面读取字段和数据构造一个新的 MemachedResponse
 * 所以在实现发生了什么事?我们知道一个 Memcached 响应有24位头;我们不知道是否所有数据,响应将被包含在输入 ByteBuf ，
 * 当解码方法调用时。这是因为底层网络堆栈可能将数据分解成块。所以确保我们只解码当我们有足够的数据,这段代码检查是否可用可读的字节的数量至少是24。
 * 一旦我们有24个字节,我们可以确定整个消息有多大,因为这个信息包含在24位头。
 * 当我们解码整个消息,我们创建一个 MemcachedResponse 并将其添加到输出列表。任何对象添加到该列表将被转发到下一个ChannelInboundHandler 在
 * ChannelPipeline,因此允许处理。
 */

public class MemcachedResponseDecoder extends ByteToMessageDecoder {  //1
    private enum State {  //2
        Header,
        Body
    }

    private State state = State.Header;
    private int totalBodySize;
    private byte magic;
    private byte opCode;
    private short keyLength;
    private byte extraLength;
    private short status;
    private int id;
    private long cas;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
                          List<Object> out) {
        switch (state) { //3
            case Header:
                if (in.readableBytes() < 24) {
                    return;//response header is 24  bytes  //4
                }
                magic = in.readByte();  //5
                opCode = in.readByte();
                keyLength = in.readShort();
                extraLength = in.readByte();
                in.skipBytes(1);
                status = in.readShort();
                totalBodySize = in.readInt();
                id = in.readInt(); //referred to in the protocol spec as opaque
                cas = in.readLong();

                state = State.Body;
            case Body:
                if (in.readableBytes() < totalBodySize) {
                    return; //until we have the entire payload return  //6
                }
                int flags = 0, expires = 0;
                int actualBodySize = totalBodySize;
                if (extraLength > 0) {  //7
                    flags = in.readInt();
                    actualBodySize -= 4;
                }
                if (extraLength > 4) {  //8
                    expires = in.readInt();
                    actualBodySize -= 4;
                }
                String key = "";
                if (keyLength > 0) {  //9
                    ByteBuf keyBytes = in.readBytes(keyLength);
                    key = keyBytes.toString(CharsetUtil.UTF_8);
                    actualBodySize -= keyLength;
                }
                ByteBuf body = in.readBytes(actualBodySize);  //10
                String data = body.toString(CharsetUtil.UTF_8);
                out.add(new MemcachedResponse(magic, opCode, status, id, cas, flags, expires, key, data));

                state = State.Header;
        }

    }
    /**
     * 类负责创建的 MemcachedResponse 读取字节
     代表当前解析状态,这意味着我们需要解析的头或 body
     根据解析状态切换
     如果不是至少24个字节是可读的,它不可能读整个头部,所以返回这里,等待再通知一次数据准备阅读
     阅读所有头的字段
     检查是否足够的数据是可读用来读取完整的响应的 body。长度是从头读取
     检查如果有任何额外的 flag 用于读，如果是这样做
     检查如果响应包含一个 expire 字段，有就读它
     检查响应是否包含一个 key ,有就读它
     读实际的 body 的 payload
     从前面读取字段和数据构造一个新的 MemachedResponse
     所以在实现发生了什么事?我们知道一个 Memcached 响应有24位头;我们不知道是否所有数据,响应将被包含在输入 ByteBuf ，
     当解码方法调用时。这是因为底层网络堆栈可能将数据分解成块。所以确保我们只解码当我们有足够的数据,这段代码检查是否可用可读的字节的数量至少是24。
     一旦我们有24个字节,我们可以确定整个消息有多大,因为这个信息包含在24位头。
     当我们解码整个消息,我们创建一个 MemcachedResponse 并将其添加到输出列表。任何对象添加到该列表将被转发到
     下一个ChannelInboundHandler 在 ChannelPipeline,因此允许处理。
     */
}