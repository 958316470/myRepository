package com.netty.nettyfile;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.RandomAccessFile;

public class NettyFileServerHandler extends SimpleChannelInboundHandler<String> {

    private static final String CR = System.getProperty("line.separator");

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg);
        File file = new File(msg);
        if (file.exists()) {
            if (!file.isFile()) {
                ctx.writeAndFlush("Not a file:" + file + CR);
                return;
            }
            ctx.write("File length" + file.length() + CR);
            RandomAccessFile accessFile = new RandomAccessFile(msg, "r");
            FileRegion region = new DefaultFileRegion(accessFile.getChannel(), 0, accessFile.length());
            ctx.write(region);
            ctx.writeAndFlush(CR);
            accessFile.close();
        } else {
            ctx.writeAndFlush("File not found" + file + CR);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
