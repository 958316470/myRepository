package com.netty.serializable;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqServerHandler extends ChannelHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReq req = (SubscribeReq) msg;
        if ("xyd".equals(req.getUserName())) {
            System.out.println("server receive data is " + req.toString());
            ctx.writeAndFlush(getResp(req.getSubReqID()));
        }
    }

    private SubscribeResp getResp(int id){
        SubscribeResp subscribeResp = new SubscribeResp();
        subscribeResp.setSubReqID(id);
        subscribeResp.setRespCode(0);
        subscribeResp.setDesc("apply is success, please wait 3 or 5 days!");
        return subscribeResp;
    }
}
