package com.netty.protocol;

public final  class NettyMessage {
    
    private Header header;
    private Object message;

    public final Header getHeader() {
        return header;
    }

    public final void setHeader(Header header) {
        this.header = header;
    }

    public final Object getMessage() {
        return message;
    }

    public final void setMessage(Object message) {
        this.message = message;
    }

    @Override
    public final String toString() {
        return "NettyMessage{" +
                "header=" + header +
                ", message=" + message +
                '}';
    }
}
