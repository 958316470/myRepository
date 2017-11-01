package com.nutch.util;

import java.nio.ByteBuffer;

public class StringUtil {

    public static String rightPad(String s, int length) {
        StringBuffer sb = new StringBuffer(s);
        for (int i = length - s.length(); i > 0; i--) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String leftPad(String s, int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = length - s.length(); i > 0; i--) {
            sb.append(" ");
        }
        sb.append(s);
        return sb.toString();
    }

    private static final char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '8', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String toHexString(ByteBuffer buf) {
        return toHexString(buf, null, Integer.MAX_VALUE);
    }

    public static String toHexString(ByteBuffer buf, String sep, int lineLen) {
        return toHexString(buf.array(), buf.arrayOffset() + buf.position(), buf.remaining(), sep, lineLen);
    }

    public static String toHexString(byte[] buf) {
        return toHexString(buf, null, Integer.MAX_VALUE);
    }

    public static String toHexString(byte[] buf, String sep, int lineLen) {
        return toHexString(buf, 0, buf.length, sep, lineLen);
    }

    public static String toHexString(byte[] buf, int of, int cb, String sep, int lineLen) {
        if (buf == null) {
            return null;
        }
        if (lineLen <= 0) {
            lineLen = Integer.MAX_VALUE;
        }
        StringBuffer res = new StringBuffer(cb * 2);
        for (int c = 0; c < cb; c++) {
            int b = buf[of++];
            res.append(HEX_DIGITS[(b >> 4) & 0xf]);
            res.append(HEX_DIGITS[b & 0xf]);
            if (c > 0 && (c % lineLen) == 0) {
                res.append('\n');
            } else if (sep != null && c < lineLen - 1) {
                res.append(sep);
            }
        }
        return res.toString();
    }

    public static byte[] fromHexString(String text) {
        text = text.trim();
        if (text.length() % 2 != 0) {
            text = "0" + text;
        }
        int resLen = text.length() / 2;
        int loNibble, hiNibble;
        byte[] res = new byte[resLen];
        for (int i = 0; i < resLen; i++) {
            int j = i << 1;
            hiNibble = charToNibble(text.charAt(j));
            loNibble = charToNibble(text.charAt(j + 1));
            if (loNibble == -1 || hiNibble == -1) {
                return null;
            }
            res[i] = (byte) (hiNibble << 4 | loNibble);
        }
        return res;
    }

    private static final int charToNibble(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }else if (c >= 'a' && c <= 'f') {
            return 0xa + (c - 'a');
        }else if (c >= 'A' && c <= 'F') {
            return 0xA + (c - 'A');
        }else {
            return -1;
        }
    }

    public static boolean isEmpty(String str) {
        return (str == null) || (str.equals(""));
    }

    public static String cleanFiled(String value) {
        return value.replaceAll("�", "");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: StringUtil <encoding name>");
        }else {
            System.out.println(args[0] + " is resolved to " + EncodingDetector.resolveEncodingAlias(args[0]));
        }
    }
}
