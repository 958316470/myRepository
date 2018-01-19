package com.netty.aio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author 95831
 *
 * 时间服务器客户端
 */
public class AioTimeClient {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            System.out.println("args的长度：" + args.length);
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
            }
        }
        new Thread(new AsyncTimeClientHandler("127.0.0.1", port)).start();
    }
}
