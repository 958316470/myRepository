package com.nutch.plugin;

public class MissingDependencyException extends Exception{
    private static final long serialVersionUID = 1L;

    public MissingDependencyException(Throwable cause){
        super(cause);
    }

    public MissingDependencyException(String message) {
        super(message);
    }
}
