package com.nutch.net;

public class URLFilterException extends Exception{
    public URLFilterException() {super();}
    public URLFilterException(String message){super(message);}
    public URLFilterException(String meeage, Throwable cause) {
        super(meeage,cause);
    }
    public URLFilterException(Throwable cause) {
        super(cause);
    }
}
