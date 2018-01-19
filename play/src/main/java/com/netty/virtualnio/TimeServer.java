package com.netty.virtualnio;

import com.netty.bio.TimeServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author 95831
 *
 * 伪异步阻塞式时间服务器
 *
 * 进步：优化了线程模型，通过线程池实现对socket的处理
 * 线程池可以控制线程的数量，防止由于海量并发
 * 接入导致线程耗尽
 *
 * 问题：底层仍是采用了同步阻塞模式
 * 1）.当对socket的输入流进行读取操作的时候，它会一直
 * 阻塞下去，直到有数据可读 或 可用数据已经读取完毕 或
 * 发生空指针或I/O异常
 * 2）.输出流也是同步阻塞
 *
 * 3）.通信时间过长引起的级联故障
 * 1.服务端处理缓慢，返回应答消息耗费60s,平时10ms.
 * 2.采用伪异步I/O的线程正在读取故障服务节点的响应，
 * 由于输入流是阻塞的，因此会被同步阻塞60s.
 * 3.如果 所有的可用线程都被故障服务器阻塞，那么后续所有的I/O
 * 消息都将在队列中排队
 * 4.线程池采用的是阻塞队列实现，当队列积满后，后续入队操作将被阻塞
 * 5.由于服务器只有一个accptor接受客户端接入，它将被阻塞在线程池的同步阻塞队列后
 * 新的客户端请求消息将被拒绝，客户端会发生大量的连接超时
 * 6.由于几乎所有的连接都超时，调用者会认为系统已经崩溃，无法接受新的请求消息
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args != null && args.length > 0) {
            System.out.println("args的长度：" + args.length);
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
            }
        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port: " + port);
            Socket socket = null;
            //创建I/O任务线程池
            TimeServerHandlerExecutePool pool = new TimeServerHandlerExecutePool(50, 10000);
            while (true) {
                socket = server.accept();
                //通过线程池处理线程
                pool.execute(new TimeServerHandler(socket));
            }
        }finally {
            if (server != null) {
                System.out.println("The time server close");
                server.close();
                server = null;
            }
        }
    }
}
