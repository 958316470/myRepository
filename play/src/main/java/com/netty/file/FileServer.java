package com.netty.file;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author 95831
 */
public class FileServer {

    private static final String FILE_PATH = "/myRepository/";

    public void run(int port, String url) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //http请求消息解码器
                            socketChannel.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            //将多个消息转换为单一的FullHttpRequest 或者 FullHttpResponse
                            socketChannel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            //对http响应信息进行编码
                            socketChannel.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            //支持异步发送大的码流，但不占用过多内存，防止发生java内存溢出错误
                            socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            socketChannel.pipeline().addLast("fileServerHandler", new FileServerHandler(url));
                        }
                    });
            ChannelFuture f = b.bind("192.168.1.124", port).sync();
            System.out.println("HTTP 文件目录服务器启动，网址是： http://192.168.1.124:" + port + url );
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){

            }
        }
        String url = FILE_PATH;
        new FileServer().run(port,url);
    }
}
