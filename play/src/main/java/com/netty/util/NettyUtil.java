package com.netty.util;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

public class NettyUtil {

    private NettyUtil() {}

    public static boolean isKeepAlive(FullHttpRequest request) {
        String connection = request.headers().get(HttpHeaderNames.CONNECTION).toString();
        if (HttpHeaderValues.KEEP_ALIVE.equals(connection)) {
            return true;
        }
        return false;
    }
}
