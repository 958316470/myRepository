package com.netty.websocket;


import com.netty.util.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;


    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handlerHttpRequest(ctx, (FullHttpRequest) msg);
        }else if (msg instanceof WebSocketFrame) {
            handlerWebsocket(ctx, (WebSocketFrame) msg);
        }
    }

    private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception{
        //如果http解码失败，返回http异常
        if (!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        //构造握手响应返回
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
        handshaker = factory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void handlerWebsocket(ChannelHandlerContext ctx, WebSocketFrame msg) {

        //判断是否连接已关闭
        if (msg instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) msg.retain());
            return;
        }
        //判断是否属于ping消息
        if (msg instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(msg.content().retain()));
            return;
        }
        //如果不输入文本消息，则抛出异常
        if (!(msg instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(msg.getClass().getName() + "frame is unsupported");
        }
        //处理文本消息
        String requestMsg = ((TextWebSocketFrame) msg).text();
        System.out.println("the receive msg is : " + requestMsg);
        //向通道响应消息
        ctx.channel().write(new TextWebSocketFrame(requestMsg + ",欢迎使用Netty服务，现在时刻是：" + new Date().toString()));
    }
    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse response) {
        if (response.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes() + "");
        }
        ChannelFuture future = ctx.channel().writeAndFlush(response);
        if (!NettyUtil.isKeepAlive(req) || response.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
