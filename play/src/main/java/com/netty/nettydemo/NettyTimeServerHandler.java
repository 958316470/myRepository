package com.netty.nettydemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
/**
 * @author 95831
 * netty 时间服务器处理
 */
public class NettyTimeServerHandler extends ChannelHandlerAdapter{

    /**
     * 用来计数发送的次数，测试故障
     */
    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       // ByteBuf buf = (ByteBuf) msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        //String body = new String(req, "UTF-8");
//        //用来测试故障
//        String body = new String(req, "UTF-8").substring(0,req.length - System.getProperty("line.separator").length());
        String body = (String) msg;
        System.out.println("The time server receive order : " + body + " ; the counter is :" +  ++counter);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        //用来测试故障
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
