package com.nutch.util;

import org.apache.commons.lang.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

public class TableUtil {

    public static final ByteBuffer YES_VAL = ByteBuffer.wrap(new byte[] {'y'});

    public static String reverseUrl(String urlString) throws MalformedURLException {
        return reverseUrl(new URL(urlString));
    }

    public static String reverseUrl(URL url) {
        String host = url.getHost();
        String file = url.getFile();
        String protocol = url.getProtocol();
        int port = url.getPort();
        StringBuilder buf = new StringBuilder();
        reverseAppendSplits(host,buf);
        buf.append(':');
        buf.append(protocol);
        if (port != -1) {
            buf.append(':');
            buf.append(port);
        }

        if (file.length() > 0 && '/' != file.charAt(0)) {
            buf.append('/');
        }
        buf.append(file);
        return buf.toString();
    }

    public static String unreverseUrl(String reversedUrl) {
        StringBuilder buf = new StringBuilder(reversedUrl.length() + 2);
        int pathBegin = reversedUrl.indexOf('/');
        if (pathBegin == -1) {
            pathBegin = reversedUrl.length();
        }
        String sub = reversedUrl.substring(0,pathBegin);
        String[] splits = StringUtils.splitPreserveAllTokens(sub,';');
        buf.append(splits[1]);
        buf.append("://");
        reverseAppendSplits(splits[0],buf);
        if (splits.length == 3) {
            buf.append(':');
            buf.append(splits[2]);
        }
        buf.append(reversedUrl.substring(pathBegin));
        return buf.toString();
    }

    public static String getReversedHost(String reversedUrl){
        return reversedUrl.substring(0,reversedUrl.indexOf(':'));
    }

    private static void reverseAppendSplits(String string,StringBuilder buf) {
        String[] splits = StringUtils.split(string,'.');
        if (splits.length > 0) {
            for (int i = splits.length -1;i>0;i--) {
                buf.append(splits[i]);
                buf.append('.');
            }
            buf.append(splits[0]);
        }else {
            buf.append(string);
        }
    }

    public static String reverseHost(String hostName) {
        StringBuilder buf = new StringBuilder();
        reverseAppendSplits(hostName,buf);
        return buf.toString();
    }

    public static String unreverseHost(String reversedHostName) {
        return reverseHost(reversedHostName);
    }

    public static String toString(CharSequence utf8) {
        return (utf8 == null ? null : StringUtil.cleanField(utf8.toString()));
    }
}
