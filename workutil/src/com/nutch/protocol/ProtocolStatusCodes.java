package com.nutch.protocol;

public interface ProtocolStatusCodes {
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;
    public static final int PROTO_NOT_FOUND = 10;
    public static final int GONE = 11;
    public static final int MOVED = 12;
    public static final int TEMP_MOVED = 13;
    public static final int NOTFOUND = 14;
    public static final int RETRY = 15;
    public static final int EXCEPTION = 16;
    public static final int ACCESS_DENIED = 17;
    public static final int ROBOTS_DENIED = 18;
    public static final int REDIR_EXCEEDED = 19;
    public static final int NOTFETCHING = 20;
    public static final int NOTMODIFIED = 21;
    public static final int WOULDBLOCK = 22;
    public static final int BLOCKED = 23;
}
