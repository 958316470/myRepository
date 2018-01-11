package com.nutch.parse;

public interface ParseStatusCodes {

    public static final byte NOTPARSED = 0;

    public static final byte SUCCESS = 1;

    public static final byte FAILED = 2;

    public static final String[] majorCodes = {"notparsed", "success", "failed"};

    public static final short SUCCESS_OK = 0;

    public static final short SUCCESS_REDIRECT = 100;

    public static final short FAILED_EXCEPTION = 200;

    public static final short FAILED_TRUNCATED = 202;

    public static final short FAILED_INVALID_FORMAT = 203;

    public static final short FAILED_MISSING_PARTS = 204;

    public static final short FAILED_MISSING_CONTENT = 205;




}
