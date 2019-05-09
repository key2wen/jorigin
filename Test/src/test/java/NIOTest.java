import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * @link: https://mp.weixin.qq.com/s/c9tkrokcDQR375kiwCeV9w?
 */
public class NIOTest {

    public static void main(String[] args) {
//        ioRead1();
        nioRead1();
    }

    public static void ioRead1() {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream("src/nomal_io.txt"));
            byte[] buf = new byte[1024];
            int bytesRead = in.read(buf);
            while (bytesRead != -1) {
                for (int i = 0; i < bytesRead; i++)
                    System.out.print((char) buf[i]);
                bytesRead = in.read(buf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //案例是对应的NIO（这里通过RandomAccessFile进行操作，
    // 当然也可以通过FileInputStream.getChannel()进行操作）：
    // FileInputStream fis = new FileInputStream("src/nio.txt");
    //            fis.getChannel();
    public static void nioRead1() {
        RandomAccessFile aFile = null;
        try {

            aFile = new RandomAccessFile("src/nio.txt", "rw");

            //Buffer顾名思义：缓冲区，实际上是一个容器，一个连续数组。
            // Channel提供从文件、网络读取数据的渠道，但是读写的数据都必须经过Buffer。
            /**
             * 可以把Buffer简单地理解为一组基本数据类型的元素列表，
             * 它通过几个变量来保存这个数据的当前位置状态：
             * capacity：缓冲区数组的总长度
             * position：下一个要操作的数据元素的位置
             * limit：缓冲区数组中不可操作的下一个元素的位置：limit<=capacity
             * mark：用于记录当前position的前一个位置或者默认是-1
             */

            FileChannel fileChannel = aFile.getChannel();

            //分配空间; 还有一种allocateDirector再说
            ByteBuffer buf = ByteBuffer.allocate(1024);

            /**
             * 向Buffer中写数据：
             * 1。从Channel写到Buffer (fileChannel.read(buf))
             * 2。通过Buffer的put()方法 （buf.put(…)）
             */
            //写入数据到Buffer
            int bytesRead = fileChannel.read(buf);

            System.out.println(bytesRead);
            while (bytesRead != -1) {
                buf.flip();
                while (buf.hasRemaining()) {
                    /**
                     *从Buffer中读取数据：
                     *1。从Buffer读取到Channel (channel.write(buf))
                     *2。使用get()方法从Buffer中读取数据 （buf.get()）
                     */
                    System.out.print((char) buf.get());
                }
                //调用clear()方法或者compact()方法
                buf.compact();

                bytesRead = fileChannel.read(buf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (aFile != null) {
                    aFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //nio client
    public static void client() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("10.10.195.115", 8080));
            if (socketChannel.finishConnect()) {
                int i = 0;
                while (true) {
                    TimeUnit.SECONDS.sleep(1);
                    String info = "I'm " + i++ + "-th information from client";
                    buffer.clear();
                    buffer.put(info.getBytes());
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        System.out.println(buffer);
                        socketChannel.write(buffer);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socketChannel != null) {
                    socketChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //io server
    public static void server() {
        ServerSocket serverSocket = null;
        InputStream in = null;
        try {
            serverSocket = new ServerSocket(8080);
            int recvMsgSize = 0;
            byte[] recvBuf = new byte[1024];
            while (true) {
                Socket clntSocket = serverSocket.accept();
                SocketAddress clientAddress = clntSocket.getRemoteSocketAddress();
                System.out.println("Handling client at " + clientAddress);
                in = clntSocket.getInputStream();
                while ((recvMsgSize = in.read(recvBuf)) != -1) {
                    byte[] temp = new byte[recvMsgSize];
                    System.arraycopy(recvBuf, 0, temp, 0, recvMsgSize);
                    System.out.println(new String(temp));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
