package com.netty.aio;

import java.io.IOException;

/**
 * @author 95831
 * 事件驱动I/O
 */
public class AioTimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args != null && args.length > 0) {
            System.out.println("args的长度：" + args.length);
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
            }
        }
        AsyncTimeServerHandler handler = new AsyncTimeServerHandler(port);
        new Thread(handler, "aio_time_server-0001").start();
    }
}
