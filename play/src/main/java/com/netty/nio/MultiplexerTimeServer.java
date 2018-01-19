package com.netty.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author 95831
 * NIO 异步I/O阻塞模式处理
 *
 *
 */
public class MultiplexerTimeServer implements Runnable{

    private Selector selector;
    private ServerSocketChannel socketChannel;
    private volatile boolean stop;

    /**
     * 初始化多路复用器，绑定端口
     *
     * @param port 端口号
     */
    public MultiplexerTimeServer(int port) {
        try {
            //1.打开多路复用器（选择器）
            selector = Selector.open();
            //2.打开通道
            socketChannel = ServerSocketChannel.open();
            //3.启用异步阻塞模式
            socketChannel.configureBlocking(false);
            //4.绑定端口号
            socketChannel.socket().bind(new InetSocketAddress(port), 1024);
            //5.通道注册到多路复用器，相当于启动服务器
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop () {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                //设置一秒轮询一次
                selector.select(1000);
                //获取查询到所有活动线程的SelectionKey
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                SelectionKey selectionKey = null;
                while (iterator.hasNext()) {
                    //取出key,并从集合中移除key
                    selectionKey = iterator.next();
                    iterator.remove();
                    try {
                        //对key进行处理
                        handleInput(selectionKey);
                    } catch (Exception e) {
                        //发送异常后释放资源
                        if (selectionKey != null) {
                            selectionKey.cancel();
                            if (selectionKey.channel() != null) {
                                selectionKey.channel().close();
                            }
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }//外层 while 循环结束

        //关闭多路复用器
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 对客户端的请求进行处理
     *
     * @param key 输入的事件
     * @throws IOException
     */
    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            //如果是可获取连接状态
            if (key.isAcceptable()) {
                //获取连接
                ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = channel.accept();
                socketChannel.configureBlocking(false);
                //注册到多路复用器，为读操作
                socketChannel.register(selector, SelectionKey.OP_READ);
            }
            //如果是可读取数据状态
            if (key.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int readSize = socketChannel.read(buffer);
                if (readSize > 0) {
                    //缓冲区limit设置为当前位置，position设置为0。对这段数据进行读取操作
                    buffer.flip();
                    //初始化字节数组
                    byte[] bytes = new byte[buffer.remaining()];
                    //将数据读取到数组中
                    buffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("The time server receive order is " + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                    //将时间写到通道里面
                    doWrite(socketChannel, currentTime);
                }else  if (readSize < 0){
                    //此时链路已经关闭,关闭连接释放资源
                    key.cancel();
                    socketChannel.close();
                } else {
                    //等于0正常情况，1说明读取到数据，不作处理
                }
            }
        }
    }

    private void doWrite(SocketChannel channel, String value) throws IOException{
        if (value != null && value.trim().length() > 0) {
            byte[] bytes = value.getBytes();
            ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.flip();
            //此处是异步阻塞，可能出现写半包的情况
            channel.write(byteBuffer);
        }
    }
}
