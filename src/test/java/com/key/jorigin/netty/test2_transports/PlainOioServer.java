package com.key.jorigin.netty.test2_transports;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * transport: 传输，oio nio,
 * <p>
 * 为了让你想象如何运输,我会从一个简单的应用程序开始,这个应用程序什么都不做,只是接受客户端连接并发送“Hi!”字符串消息
 * 到客户端,发送完了就断开连接。我不会详细讲解这个过程的实现,它只是一个例子
 * <p>
 * Blocking networking without Netty
 * <p>
 * <p>
 * <p>
 * 下面的方式很简洁,但是这种阻塞模式在大连接数的情况就会有很严重的问题,如客户端连接超时,
 * 服务器响应严重延迟。为了解决这 种情况,我们可以使用异步网络处理所有的并发连接,
 * 但问题在于NIO和OIO的API是完全不同的,所以一个用OIO开发的网络应用程序 想要使用NIO重构代码几乎是重新开发
 */
public class PlainOioServer {
    public void server(int port) throws Exception {
        //bind server to port
        final ServerSocket socket = new ServerSocket(port);
        try {
            while (true) {
                //accept connection
                final Socket clientSocket = socket.accept();
                System.out.println("Accepted connection from " + clientSocket);
                //create new thread to handle connection
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream out;
                        try {
                            out = clientSocket.getOutputStream();
                            //write message to connected client
                            out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
                            out.flush();
                            //close connection once message written and flushed
                            clientSocket.close();
                        } catch (IOException e) {
                            try {
                                clientSocket.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }

                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            socket.close();
        }
    }
}
