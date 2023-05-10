package com.key.jorigin.netty.test6_;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * Created by shuguang on 18/1/16.
 * <p>
 * <p>
 * 8.1 使用SSL/TLS创建安全的Netty程序 通信数据在网络上传输一般是不安全的,因为传输的数据可以发送纯文本或二进制的数据,
 * 很容易被破解。我们很有必要对网络上的数
 * 据进行加密。SSL和TLS是众所周知的标准和分层的协议,它们可以确保数据时私有的。例如,使用HTTPS或SMTPS都使用了SSL/TLS对数
 * 据进行了加密。
 * 对于SSL/TLS,Java中提供了抽象的SslContext和SslEngine。实际上,SslContext可以用来获取SslEngine来进行加密和解密。
 * 使用指定 的加密技术是高度可配置的,但是这不在本章范围。Netty扩展了Java的SslEngine,添加了一些新功能,使其更适合基于Netty的应用程序。
 * Netty提供的这个扩展是SslHandler,是SslEngine的包装类,用来对网络数据进行加密和解密
 */
public class SslChannelInitializer extends ChannelInitializer<Channel> {

    private final SSLContext context;
    private final boolean client;
    private final boolean startTls;

    public SslChannelInitializer(SSLContext context, boolean client, boolean startTls) {
        this.context = context;
        this.client = client;
        this.startTls = startTls;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine engine = context.createSSLEngine();
        engine.setUseClientMode(client);
        ch.pipeline().addFirst("ssl", new SslHandler(engine, startTls));
    }

    /**
     * 需要注意一点,SslHandler必须要添加到ChannelPipeline的第一个位置,可能有一些例外,但是最好这样来做。回想一下之前讲解的
     * ChannelHandler,ChannelPipeline就像是一个在处理“入站”数据时先进先出,在处理“出站”数据时后进先出的队列。
     * 最先添加的SslHandler会 啊在其他Handler处理逻辑数据之前对数据进行加密,从而确保Netty服务端的所有的Handler的变化都是安全的。
     *
     * SslHandler提供了一些有用的方法,可以用来修改其行为或得到通知,一旦SSL/TLS完成握手(在握手过程中的两个对等通道互相验证对 方,然后选择一个加密密码),SSL/TLS是自动执行的。看下面方法列表:
     setHandshakeTimeout(long handshakeTimeout, TimeUnit unit),设置握手超时时间,ChannelFuture将得到通知
     setHandshakeTimeoutMillis(long handshakeTimeoutMillis),设置握手超时时间,ChannelFuture将得到通知
     getHandshakeTimeoutMillis(),获取握手超时时间值
     setCloseNotifyTimeout(long closeNotifyTimeout, TimeUnit unit),设置关闭通知超时时间,若超时,
     ChannelFuture会关闭失败 setHandshakeTimeoutMillis(long handshakeTimeoutMillis),设置关闭通知超时时间,若超时,
     ChannelFuture会关闭失败 getCloseNotifyTimeoutMillis(),获取关闭通知超时时间
     handshakeFuture(),返回完成握手后的ChannelFuture close(),发送关闭通知请求关闭和销毁
     */
}
