package com.key.jorigin.netty.test10_memcached;

/**
 * 实现 Memcached 解码器
 * <p>
 * 将 MemcachedRequest 对象转为字节序列，Memcached 仅需将字节转到响应对象返回即可。
 * 先见一个 POJO:
 * <p>
 * 该类,代表从 Memcached 服务器返回的响应
 * 幻数
 * opCode,这反映了创建操作的响应
 * 数据类型,这表明这个是基于二进制还是文本
 * 响应的状态,这表明如果请求是成功的
 * 惟一的 id
 * compare-and-set 值
 * 使用额外的 flag
 * 表示该值存储的一个有效期
 * 响应创建的 key
 * 实际数据
 */
public class MemcachedResponse {  //1
    private final byte magic;
    private final byte opCode;
    private byte dataType;
    private final short status;
    private final int id;
    private final long cas;
    private final int flags;
    private final int expires;
    private final String key;
    private final String data;

    public MemcachedResponse(byte magic, byte opCode,
                             short status,
                             int id, long cas,
                             int flags, int expires, String key, String data) {
        this.magic = magic;
        this.opCode = opCode;
        this.status = status;
        this.id = id;
        this.cas = cas;
        this.flags = flags;
        this.expires = expires;
        this.key = key;
        this.data = data;
    }

    public MemcachedResponse(byte magic, byte opCode,
                             byte dataType, short status,
                             int id, long cas,
                             int flags, int expires, String key, String data) {
        this.magic = magic;
        this.opCode = opCode;
        this.dataType = dataType;
        this.status = status;
        this.id = id;
        this.cas = cas;
        this.flags = flags;
        this.expires = expires;
        this.key = key;
        this.data = data;
    }

    public byte magic() { //2
        return magic;
    }

    public byte opCode() { //3
        return opCode;
    }

    public byte dataType() { //4
        return dataType;
    }

    public short status() {  //5
        return status;
    }

    public int id() {  //6
        return id;
    }

    public long cas() {  //7
        return cas;
    }

    public int flags() {  //8
        return flags;
    }

    public int expires() { //9
        return expires;
    }

    public String key() {  //10
        return key;
    }

    public String data() {  //11
        return data;
    }
}