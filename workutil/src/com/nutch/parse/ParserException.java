package com.nutch.parse;


public class ParserException extends Exception {

    public ParserException(){
        super();
    }

    public ParserException(String msg){
        super(msg);
    }

    public ParserException(Throwable cause) {
        super(cause);
    }

    public ParserException(String msg, Throwable cause) {
        super(msg,cause);
    }
}
