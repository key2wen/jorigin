package com.key.jorigin.netty.test6_;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Netty提供了HTTP请求和响应的编码器和解码器,看下面列表:
 * HttpRequestEncoder,将HttpRequest或HttpContent编码成ByteBuf
 * HttpRequestDecoder,将ByteBuf解码成HttpRequest和HttpContent
 * HttpResponseEncoder,将HttpResponse或HttpContent编码成ByteBuf
 * HttpResponseDecoder,将ByteBuf解码成HttpResponse和HttpContent
 */
public class HttpDecoderEncoderInitializer extends ChannelInitializer<Channel> {

    private final boolean client;

    public HttpDecoderEncoderInitializer(boolean client) {
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (client) {
            //inbound
            pipeline.addLast("decoder", new HttpResponseDecoder());
            //outbound
            pipeline.addLast("encoder", new HttpRequestEncoder());
        } else {
            //inbound
            pipeline.addLast("decoder", new HttpRequestDecoder());
            //outbound
            pipeline.addLast("encoder", new HttpResponseEncoder());
        }
    }
    /**
     * 如果你需要在ChannelPipeline中有一个解码器和编码器,还分别有一个在客户端和服务器简单的编解码器:
     * HttpClientCodec和 HttpServerCodec。
     在ChannelPipelien中有解码器和编码器(或编解码器)后就可以操作不同的HttpObject消息了;
     但是HTTP请求和响应可以有很多消息数据,你需要处理不同的部分,可能也需要聚合这些消息数据,这是很麻烦的。
     为了解决这个问题,Netty提供了一个聚合器,它将消息部分合并到FullHttpRequest和FullHttpResponse,因此不需要担心接收碎片消息数据。

     8.2.2 HTTP消息聚合
     处理HTTP时可能接收HTTP消息片段,Netty需要缓冲直到接收完整个消息。要完成的处理HTTP消息,并且内存开销也不会很大,
     Netty 为此提供了HttpObjectAggregator。通过HttpObjectAggregator,Netty可以聚合HTTP消息,
     使用FullHttpResponse和FullHttpRequest到
     ChannelPipeline中的下一个ChannelHandler,这就消除了断裂消息,保证了消息的完整。下面代码显示了如何聚合
     */
}

/**
 * 添加聚合http消息的Handler
 */
class HttpAggregatorInitializer extends ChannelInitializer<Channel> {
    private final boolean client;

    public HttpAggregatorInitializer(boolean client) {
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (client) {
            pipeline.addLast("codec", new HttpClientCodec());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
        }
        pipeline.addLast("aggegator", new HttpObjectAggregator(512 * 1024));
    }
    /**
     * 如上面代码,很容使用Netty自动聚合消息。但是请注意,为了防止Dos攻击服务器,需要合理的限制消息的大小。
     * 应设置多大取决于实 际的需求,当然也得有足够的内存可用。
     *
     * 8.2.3 HTTP压缩
     使用HTTP时建议压缩数据以减少传输流量,压缩数据会增加CPU负载,现在的硬件设施都很强大,大多数时候压缩数据时一个好主意。
     Netty支持“gzip”和“deflate”,为此提供了两个ChannelHandler实现分别用于压缩和解压。看下面代码:


     HTTP Request Header

     客户端可以通过提供下面的头显示支持加密模式。然而服务器不是,所以不得不压缩它发送的数据。
     GET /encrypted-area HTTP/1.1
     Host: www.example.com
     Accept-Encoding: gzip, deflate
     */
}

class HttpZipInitializer extends ChannelInitializer<Channel> {
    private final boolean client;

    HttpZipInitializer(boolean client) {
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (client) {
            pipeline.addLast("codec", new HttpClientCodec());
            //添加解压缩 inbound Handler 用于处理来自服务器的压缩的内容
            pipeline.addLast("decompressor", new HttpContentDecompressor());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
            //用于压缩来自 client 支持的 HttpContentCompressor inbound／outbound Handler
            pipeline.addLast("compressor", new HttpContentCompressor());
        }
        //使用最大消息值是 512kb
        pipeline.addLast("aggegator", new HttpObjectAggregator(512 * 1024));
    }
}

/**
 * 8.2.4 使用HTTPS 网络中传输的重要数据需要加密来保护,使用Netty提供的SslHandler可以很容易实现,看下面代码:
 */

/**
 * 使用SSL对HTTP消息加密
 */
class HttpsCodecInitializer extends ChannelInitializer<Channel> {
    private final SSLContext context;
    private final boolean client;

    public HttpsCodecInitializer(SSLContext context, boolean client) {
        this.context = context;
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {

        SSLEngine engine = context.createSSLEngine();

        engine.setUseClientMode(client);

        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addFirst("ssl", new SslHandler(engine));
        if (client) {
            pipeline.addLast("codec", new HttpClientCodec());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
        }
    }

    /**
     * 8.2.5 WebSocket
     HTTP是不错的协议,但是如果需要实时发布信息怎么做?有个做法就是客户端一直轮询请求服务器,这种方式虽然可以达到目的,
     但是 其缺点很多,也不是优秀的解决方案,为了解决这个问题,便出现了WebSocket。
     WebSocket允许数据双向传输,而不需要请求-响应模式。早期的WebSocket只能发送文本数据,然后现在不仅可以发送文本数据,
     也 可以发送二进制数据,这使得可以使用WebSocket构建你想要的程序。下图是WebSocket的通信示例图:


     WebSocket 规范及其实现是为了一个更有效的解决方案。简单的说, 一个WebSocket 提供一个 TCP 连接两个方向的交通。
     结合 WebSocket API 它提供了一个替代 HTTP 轮询双向通信从页面到远程服务器。
     也就是说,WebSocket 提供真正的双向客户机和服务器之间的数据交换。 我们不会对内部太多的细节,但我们应该提到,虽然最早实现仅限于文本数据，
     但现在不再是这样,WebSocket可以用于任意数据,就像一个正常的套接字。
     图8.4给出了一个通用的 WebSocket 协议。在这种情况下的通信开始于普通 HTTP ，并“升级”为双向 WebSocket。
     1.Client (HTTP) 与 Server 通讯
     2.Server (HTTP) 与 Client 通讯
     3.Client 通过 HTTP(s) 来进行 WebSocket 握手,并等待确认
     4.连接协议升级至 WebSocket

     Figure 8.4 WebSocket protocol
     添加应用程序支持 WebSocket 只需要添加适当的客户端或服务器端WebSocket ChannelHandler 到管道。
     这个类将处理特殊 WebSocket 定义的消息类型,称为“帧。“如表8.3所示,这些可以归类为“数据”和“控制”帧。


     在应用程序中添加WebSocket支持很容易,Netty附带了WebSocket的支持,通过ChannelHandler来实现。使用WebSocket有不同的
     消息类型需要处理。下面列表列出了Netty中WebSocket类型:
     BinaryWebSocketFrame,包含二进制数据
     TextWebSocketFrame,包含文本数据
     ContinuationWebSocketFrame,包含二进制数据或文本数据,BinaryWebSocketFrame和TextWebSocketFrame的结合体

     CloseWebSocketFrame,WebSocketFrame代表一个关闭请求,包含关闭状态码和短语
     PingWebSocketFrame,WebSocketFrame要求PongWebSocketFrame发送数据
     PongWebSocketFrame,WebSocketFrame要求PingWebSocketFrame响应

     为了简化,我们只看看如何使用WebSocket服务器。客户端使用可以看Netty自带的WebSocket例子。
     Netty提供了许多方法来使用WebSocket,但最简单常用的方法是使用WebSocketServerProtocolHandler。看下面代码:

     该类处理协议升级握手以及三个“控制”帧 Close, Ping 和 Pong。Text 和 Binary 数据帧将被传递到下一个处理程序(由你实现)进行处理
     */
}

/**
 * 1. 添加 HttpObjectAggregator 用于提供在握手时聚合 HttpRequest
 * 2. 添加 WebSocketServerProtocolHandler 用于处理色好给你寄握手如果请求是发送到"/websocket." 端点，
 * 当升级完成后，它将会处理Ping, Pong 和 Close 帧
 * 3.TextFrameHandler 将会处理 TextWebSocketFrames
 * 4. BinaryFrameHandler 将会处理 BinaryWebSocketFrames
 * 5. ContinuationFrameHandler 将会处理ContinuationWebSocketFrames
 * 加密 WebSocket 只需插入 SslHandler 到作为 pipline 第一个 ChannelHandler
 */
class WebSocketServerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(
                //in/out bound httpData decode/encode
                new HttpServerCodec(),
                //inbound decode 聚合数据
                new HttpObjectAggregator(65536),  //1
                //inbound decode
                new WebSocketServerProtocolHandler("/websocket"),  //2
                //inbound
                new TextFrameHandler(),  //3
                //
                new BinaryFrameHandler(),  //4
                new ContinuationFrameHandler());  //5
    }

    static final class TextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
            // Handle text frame
        }
    }

    static final class BinaryFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
            // Handle binary frame
        }
    }

    static final class ContinuationFrameHandler extends SimpleChannelInboundHandler<ContinuationWebSocketFrame> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, ContinuationWebSocketFrame msg) throws Exception {
            // Handle continuation frame
        }
    }
}
/**
 * 8.2.6 SPDY
 * SPDY(读作“SPeeDY”)是Google开发的基于TCP的应用层协议,用以最小化网络延迟,提升网络速度,优化用户的网络使用体验。
 * SPDY并不是一种用于替代HTTP的协议,而是对HTTP协议的增强。新协议的功能包括数据流的多路复用、请求优先级以及HTTP报头压缩。
 * 谷歌表示,引入SPDY协议后,在实验室测试中页面加载速度比原先快64%。
 * SPDY的定位:
 * 将页面加载时间减少50%。 最大限度地减少部署的复杂性。SPDY使用TCP作为传输层,因此无需改变现有的网络设施。 避免网站开发者改动内容。
 * 支持SPDY唯一需要变化的是客户端代理和Web服务器应用程序。
 * SPDY实现技术:
 * 单个TCP连接支持并发的HTTP请求。
 * 压缩报头和去掉不必要的头部来减少当前HTTP使用的带宽。
 * 定义一个容易实现,在服务器端高效率的协议。
 * 通过减少边缘情况、定义易解析的消息格式来减少HTTP的复杂性。
 * 强制使用SSL,让SSL协议在现存的网络设施下有更好的安全性和兼容性。
 * 允许服务器在需要时发起对客户端的连接并推送数据。
 * SPDY具体的细节知识及使用可以查阅相关资料,这里不作赘述了。
 * <p>
 * SPDY 实现技术：
 * 压缩报头
 * 加密所有
 * 多路复用连接
 * 提供支持不同的传输优先级
 * SPDY 主要有5个版本：
 * 1 - 初始化版本，但没有使用
 * 2 - 新特性，包含服务器推送
 * 3 - 新特性包含流控制和更新压缩
 * 3.1 - 会话层流程控制
 * 4.0 - 流量控制，并与 HTTP 2.0 更加集成
 * SPDY 被很多浏览器支持，包括 Google Chrome, Firefox, 和 Opera
 * Netty 支持 版本 2 和 3 （包含3.1）的支持。这些版本被广泛应用，可以支持更多的用户
 * <p>
 * 8.3 处理空闲连接和超时
 * 处理空闲连接和超时是网络应用程序的核心部分。当发送一条消息后,可以检测连接是否还处于活跃状态,若很长时间没用了就可以断 开连接。
 * Netty提供了很好的解决方案,有三种不同的ChannelHandler处理闲置和超时连接:
 * IdleStateHandler,当一个通道没有进行读写或运行了一段时间后出发IdleStateEvent ReadTimeoutHandler,
 * 在指定时间内没有接收到任何数据将抛出ReadTimeoutException WriteTimeoutHandler,在指定时间内有写入数据将抛出WriteTimeoutException
 * 最常用的是IdleStateHandler,下面代码显示了如何使用IdleStateHandler,如果60秒内没有接收数据或发送数据,操作将失败,连接将关闭
 * <p>
 * 8.3 处理空闲连接和超时
 * 处理空闲连接和超时是网络应用程序的核心部分。当发送一条消息后,可以检测连接是否还处于活跃状态,若很长时间没用了就可以断 开连接。
 * Netty提供了很好的解决方案,有三种不同的ChannelHandler处理闲置和超时连接:
 * IdleStateHandler,当一个通道没有进行读写或运行了一段时间后出发IdleStateEvent ReadTimeoutHandler,
 * 在指定时间内没有接收到任何数据将抛出ReadTimeoutException WriteTimeoutHandler,在指定时间内有写入数据将抛出WriteTimeoutException
 * 最常用的是IdleStateHandler,下面代码显示了如何使用IdleStateHandler,如果60秒内没有接收数据或发送数据,操作将失败,连接将关闭
 */

/**
 * 8.3 处理空闲连接和超时
 * 处理空闲连接和超时是网络应用程序的核心部分。当发送一条消息后,可以检测连接是否还处于活跃状态,若很长时间没用了就可以断 开连接。
 * Netty提供了很好的解决方案,有三种不同的ChannelHandler处理闲置和超时连接:
 * IdleStateHandler,当一个通道没有进行读写或运行了一段时间后出发IdleStateEvent
 * ReadTimeoutHandler,在指定时间内没有接收到任何数据将抛出ReadTimeoutException
 * WriteTimeoutHandler,在指定时间内有写入数据将抛出WriteTimeoutException
 * 最常用的是 IdleStateHandler,下面代码显示了如何使用 IdleStateHandler,如果60秒内没有接收数据或发送数据,操作将失败,连接将关闭
 * <p>
 * <p>
 * <p>
 * IdleStateHandler 将通过 IdleStateEvent 调用 userEventTriggered ，如果连接没有接收或发送数据超过60秒钟
 * 心跳发送到远端
 * 发送的心跳并添加一个侦听器，如果发送操作失败将关闭连接
 * 事件不是一个 IdleStateEvent 的话，就将它传递给下一个处理程序
 * 总而言之,这个例子说明了如何使用 IdleStateHandler 测试远端是否还活着，如果不是就关闭连接释放资源。+
 */

class IdleStateHandlerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));  //1
        pipeline.addLast(new HeartbeatHandler());
    }

    static final class HeartbeatHandler extends ChannelInboundHandlerAdapter {

        private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("HEARTBEAT", CharsetUtil.UTF_8));  //2

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);  //3
            } else {
                super.userEventTriggered(ctx, evt);  //4
            }
        }
    }
}

/**
 * 8.4 解码分隔符和基于长度的协议 使用Netty时会遇到需要解码以分隔符和长度为基础的协议,本节讲解Netty如何解码这些协议。
 * 8.4.1 分隔符协议 经常需要处理分隔符协议或创建基于它们的协议,例如SMTP、POP3、IMAP、Telnet等等;Netty附带的handlers可以很容易的提取一些
 * 序列分隔:
 * DelimiterBasedFrameDecoder,解码器,接收ByteBuf由一个或多个分隔符拆分,如NUL或换行符
 * LineBasedFrameDecoder,解码器,接收ByteBuf以分割线结束,如"\n"和"\r\n"
 * <p>
 * <p>
 * 下面代码显示使用LineBasedFrameDecoder提取"\r\n"分隔帧
 * acb\r\nd\r\n (字节流) ==> acb\r\n(第一贞)   d\r\n(第二帧)
 * <p>
 * 添加一个 LineBasedFrameDecoder 用于提取帧并把数据包转发到下一个管道中的处理程序,在这种情况下就是 FrameHandler
 * 添加 FrameHandler 用于接收帧
 * 每次调用都需要传递一个单帧的内容
 */
class LineBasedHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new LineBasedFrameDecoder(65 * 1204), new FrameHandler());
    }

    public static final class FrameHandler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            // do something with the frame
        }
    }
}
/**
 * 如果框架的东西除了换行符还有别的分隔符,可以使用DelimiterBasedFrameDecoder,只需要将分隔符传递到构造方法中。
 * 如果想实现 自己的以分隔符为基础的协议,这些解码器是有用的。例如,现在有个协议,它只处理命令,这些命令由名称和参数形成,
 * 名称和参数由一 个空格分隔,实现这个需求的代码如下:
 */

/**
 * 自定义以分隔符为基础的协议
 *
 * @author c.k
 */
class CmdHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new Cmd.CmdDecoder(65 * 1024), new Cmd.CmdHandler());
    }

    public static final class Cmd {
        private final ByteBuf name;
        private final ByteBuf args;

        public Cmd(ByteBuf name, ByteBuf args) {
            this.name = name;
            this.args = args;

        }

        public ByteBuf getName() {
            return name;
        }

        public ByteBuf getArgs() {
            return args;

        }

        static final class CmdDecoder extends LineBasedFrameDecoder {
            public CmdDecoder(int maxLength) {
                super(maxLength);
            }

            @Override
            protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
                ByteBuf frame = (ByteBuf) super.decode(ctx, buffer);
                if (frame == null) {
                    return null;
                }

                int index = frame.indexOf(frame.readerIndex(), frame.writerIndex(), (byte) ' ');
                return new Cmd(frame.slice(frame.readerIndex(), index), frame.slice(index + 1, frame.writerIndex()));
            }
        }

        static final class CmdHandler extends SimpleChannelInboundHandler<Cmd> {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Cmd msg) throws Exception {
                // do something with the command
            }
        }
    }
}

/**
 * 8.4.2 长度为基础的协议 一般经常会碰到以长度为基础的协议,对于这种情况Netty有两个不同的解码器可以帮助我们来解码:
 * FixedLengthFrameDecoder LengthFieldBasedFrameDecoder
 * <p>
 * 下图显示了FixedLengthFrameDecoder的处理流程:
 * 如上图所示,FixedLengthFrameDecoder提取固定长度,例子中的是8字节。大部分时候帧的大小被编码在头部,这种情况可以使用
 * LengthFieldBasedFrameDecoder,它会读取头部长度并提取帧的长度。下图显示了它是如何工作的:
 * <p>
 * 如果长度字段是提取框架的一部分,可以在LengthFieldBasedFrameDecoder的构造方法中配置,还可以指定提供的长度。
 * FixedLengthFrameDecoder很容易使用,我们重点讲解LengthFieldBasedFrameDecoder。
 * 下面代码显示如何使用 LengthFieldBasedFrameDecoder提取8字节长度:
 */
class LengthBasedInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65 * 1024, 0, 8))
                .addLast(new FrameHandler())
//        .addLast(new FixedLengthFrameDecoder(8))
        ;
    }

    public static final class FrameHandler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            //do something with the frame
        }
    }
}

/*
* 8.5 写大数据 写大量的数据的一个有效的方法是使用异步框架,如果内存和网络都处于饱满负荷状态,你需要停止写,否则会报OutOfMemoryError。
Netty提供了写文件内容时zero-memory-copy机制,这种方法再将文件内容写到网络堆栈空间时可以获得最大的性能。使用零拷贝写文件的内
容时通过DefaultFileRegion、ChannelHandlerContext、ChannelPipeline,看下面代码:
*
* */
class MaxDataInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {

    }

    static final class MaxDataHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            File file = new File("test.txt");
            FileInputStream fis = new FileInputStream(file);
            FileRegion region = new DefaultFileRegion(fis.getChannel(), 0, file.length());
            Channel channel = ctx.channel();
            channel.writeAndFlush(region).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        Throwable cause = future.cause();
                        // do something
                    }
                }
            });
        }
    }
    /**
     * 只是看到的例子只适用于直接传输一个文件的内容,没有执行的数据应用程序的处理。在相反的情况下,
     * 将数据从文件系统复制到用户内存是必需的,您可以使用 ChunkedWriteHandler。这个类提供了支持异步写大数据流不引起高内存消耗
     *
     * 这个关键是 interface ChunkedInput，实现如下：
     名称	描述
     ChunkedFile	当你使用平台不支持 zero-copy 或者你需要转换数据，从文件中一块一块的获取数据
     ChunkedNioFile	与 ChunkedFile 类似，处理使用了NIOFileChannel
     ChunkedStream	从 InputStream 中一块一块的转移内容
     ChunkedNioStream	从 ReadableByteChannel 中一块一块的转移内容


     清单 8.12 演示了使用 ChunkedStream,实现在实践中最常用。 所示的类被实例化一个 File 和一个 SslContext。当 initChannel() 被调用来初始化显示的处理程序链的通道。
     当通道激活时，WriteStreamHandler 从文件一块一块的写入数据作为ChunkedStream。最后将数据通过 SslHandler 加密后传播。
     */
}

/*
* 如果只想发送文件中指定的数据块应该怎么做呢?Netty提供了ChunkedWriteHandler,
* 允许通过处理ChunkedInput来写大的数据块。下 面是ChunkedInput的一些实现类:
ChunkedFile ChunkedNioFile ChunkedStream ChunkedNioStream
* */
class ChunkedWriteHandlerInitializer extends ChannelInitializer<Channel> {
    private final File file;

    private final SslContext sslCtx;
    private final SSLContext sslContext;

    public ChunkedWriteHandlerInitializer(File file, SslContext sslCtx, SSLContext sslContext) {
        this.file = file;
        this.sslCtx = sslCtx;
        this.sslContext = sslContext;
    }

    /**
     * 添加 SslHandler 到 ChannelPipeline.
     * 添加 ChunkedWriteHandler 用来处理作为 ChunkedInput 传进的数据
     * 当连接建立时，WriteStreamHandler 开始写文件的内容
     * 当连接建立时，channelActive() 触发使用 ChunkedInput 来写文件的内容
     * (插图显示了 FileInputStream;也可以使用任何 InputStream )
     * ChunkedInput 所有被要求使用自己的 ChunkedInput 实现，是安装ChunkedWriteHandler 在管道中
     * 在本节中,我们讨论如何采用zero-copy（零拷贝）功能高效地传输文件
     * 如何使用 ChunkedWriteHandler 编写大型数据而避免 OutOfMemoryErrors 错误。
     * 在下一节中我们将研究几种不同方法来序列化 POJO。
     */
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast(new SslHandler(sslCtx.newEngine(ch.alloc()))) //1
//                .addLast(new SslHandler(sslContext.createSSLEngine())) //1
                .addLast(new ChunkedWriteHandler())
                .addLast(new WriteStreamHandler());
    }

    public final class WriteStreamHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            ctx.writeAndFlush(new ChunkedStream(new FileInputStream(file)));
        }
    }
}
/*
* 8.6 序列化数据 开发网络程序过程中,很多时候需要传输结构化对象数据POJO,Java中提供了ObjectInputStream和ObjectOutputStream及其他的一些对
象序列化接口。Netty中提供基于JDK序列化接口的序列化接口。 8.6.1 普通的JDK序列化
如果你使用ObjectInputStream和ObjectOutputStream,并且需要保持兼容性,不想有外部依赖,那么JDK的序列化是首选。Netty提供了
下面的一些接口,这些接口放在io.netty.handler.codec.serialization包下面:
CompatibleObjectEncoder
CompactObjectInputStream
CompactObjectOutputStream
ObjectEncoder
ObjectDecoder
ObjectEncoderOutputStream
ObjectDecoderInputStream


8.6.2 通过JBoss编组序列化
如果你想使用外部依赖的接口,JBoss编组是个好方法。JBoss Marshalling序列化的速度是JDK的3倍,并且序列化的结构更紧凑,从而
使序列化后的数据更小。Netty附带了JBoss编组序列化的实现,这些实现接口放在io.netty.handler.codec.marshalling包下面:
CompatibleMarshallingEncoder
CompatibleMarshallingDecoder
MarshallingEncoder
MarshallingDecoder



下表展示了 Netty 支持 JBoss Marshalling 的编解码器。
Table 8.9 JBoss Marshalling codecs
名称	描述
CompatibleMarshallingDecoder	为了与使用 JDK 序列化的端对端间兼容。
CompatibleMarshallingEncoder	为了与使用 JDK 序列化的端对端间兼容。
MarshallingDecoder	使用自定义序列化用于解码，必须使用
MarshallingEncoder MarshallingEncoder | 使用自定义序列化用于编码，必须使用 MarshallingDecoder
* */

/**
 * 使用JBoss Marshalling
 */
class MarshallingInitializer extends ChannelInitializer<Channel> {
    private final MarshallerProvider marshallerProvider;
    private final UnmarshallerProvider unmarshallerProvider;

    public MarshallingInitializer(MarshallerProvider marshallerProvider, UnmarshallerProvider unmarshallerProvider) {
        this.marshallerProvider = marshallerProvider;
        this.unmarshallerProvider = unmarshallerProvider;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                //inbound
                .addLast(new MarshallingDecoder(unmarshallerProvider))
                //out bound
                .addLast(new MarshallingEncoder(marshallerProvider))

                .addLast(new ObjectHandler());
    }

    public final class ObjectHandler extends SimpleChannelInboundHandler<Serializable> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Serializable msg) throws Exception {
            // do something
        }
    }
}

/**
 * 8.6.3 使用ProtoBuf序列化 最有一个序列化方案是Netty附带的ProtoBuf。protobuf是Google开源的一种编码和解码技术,它的作用是使序列化数据更高效。并且谷
 * 歌提供了protobuf的不同语言的实现,所以protobuf在跨平台项目中是非常好的选择。Netty附带的protobuf放在io.netty.handler.codec.protobuf 包下面:
 * ProtobufDecoder
 * ProtobufEncoder
 * ProtobufVarint32FrameDecoder
 * ProtobufVarint32LengthFieldPrepender
 * <p>
 * <p>
 * 下表展示了 Netty 支持 ProtoBuf 的 ChannelHandler 实现。
 * Table 8.10 ProtoBuf codec
 * 名称	描述
 * ProtobufDecoder	使用 ProtoBuf 来解码消息
 * ProtobufEncoder	使用 ProtoBuf 来编码消息
 * ProtobufVarint32FrameDecoder	在消息的整型长度域中，通过 "Base 128 Varints"将接收到的 ByteBuf 动态的分割
 * <p>
 * <p>
 * 添加 ProtobufVarint32FrameDecoder 用来分割帧
 * 添加 ProtobufEncoder 用来处理消息的编码
 * 添加 ProtobufDecoder 用来处理消息的解码
 * 添加 ObjectHandler 用来处理解码了的消息
 * 本章在这最后一节中,我们探讨了 Netty 支持的不同的序列化的专门的解码器和编码器。这些是标准 JDK 序列化 API,JBoss Marshalling 和谷歌ProtoBuf。
 */
class ProtoBufInitializer extends ChannelInitializer<Channel> {

    private final MessageLite lite;

    public ProtoBufInitializer(MessageLite lite) {
        this.lite = lite;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                //inbound
                .addLast(new ProtobufVarint32FrameDecoder())
                //outbound
                .addLast(new ProtobufEncoder())
                //inbound
                .addLast(new ProtobufDecoder(lite))

                .addLast(new ObjectHandler());
    }

    public final class ObjectHandler extends SimpleChannelInboundHandler<Serializable> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Serializable msg) throws Exception {
            // do something
        }
    }
}