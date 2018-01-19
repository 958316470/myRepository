package com.netty.nio;


/**
 * @author 95831
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            System.out.println("args的长度：" + args.length);
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
            }
        }

        new Thread(new TimeClientHandle("127.0.0.1", port),"timeClent-001").start();

    }
}
