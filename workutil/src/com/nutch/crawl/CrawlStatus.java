package com.nutch.crawl;

import java.util.HashMap;
import java.util.Map;

public class CrawlStatus {

    public static final byte STATUS_UNFETCHED = 0x01;
    public static final byte STATUS_FETCHED = 0x02;
    public static final byte STATUS_GONE = 0x03;
    public static final byte STATUS_REDIR_TEMP = 0x04;
    public static final byte STATUS_REDIR_PERM = 0x05;
    public static final byte STATUS_RETRY = 0x22;
    public static final byte STATUS_NOTMODIFIED = 0x26;
    private static final Map<Byte, String> NAMES = new HashMap<Byte, String>();
    static {
        NAMES.put(STATUS_UNFETCHED,"status_unfetched");
        NAMES.put(STATUS_FETCHED,"status_fetched");
        NAMES.put(STATUS_GONE,"status_gone");
        NAMES.put(STATUS_REDIR_TEMP,"status_redir_temp");
        NAMES.put(STATUS_REDIR_PERM,"status_redir_perm");
        NAMES.put(STATUS_NOTMODIFIED,"status_notmodified");
    }

    public static String getName(byte status) {
        return NAMES.get(status);
    }
}
