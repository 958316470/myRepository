package com.netty.serializable;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqClientHandler extends ChannelHandlerAdapter {

    public SubReqClientHandler(){}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      for (int i = 0; i < 10; i++) {
          ctx.writeAndFlush(req(i));
      }
    }

    private SubscribeReq req(int id) {
        SubscribeReq req = new SubscribeReq();
        req.setSubReqID(id);
        req.setUserName("xyd");
        req.setAddress("beijing shi");
        req.setPhoneNumber("15654567890");
        req.setProductName("books");
        return req;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeResp resp = (SubscribeResp) msg;
        System.out.println("the client receive data is " + resp.toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
