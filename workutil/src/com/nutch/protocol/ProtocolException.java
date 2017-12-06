package com.nutch.protocol;

public class ProtocolException extends Exception{
    public ProtocolException() {
        super();
    }
    public ProtocolException(String message) {
        super(message);
    }
    public ProtocolException(String message,Throwable e) {
        super(message,e);
    }
    public ProtocolException(Throwable e) {
        super(e);
    }
}
