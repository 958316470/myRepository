package com.nutch.api.model.response;

import org.apache.commons.lang.exception.ExceptionUtils;

public class ErrorResponse {
    private String exception;
    private String message;
    private String stackTrace;

    public ErrorResponse(Throwable throwable){
        if(throwable == null){
            message = "Unknown error!";
            return;
        }
        exception = throwable.getClass().toString();
        message = ExceptionUtils.getMessage(throwable);
        stackTrace = ExceptionUtils.getFullStackTrace(throwable);
    }

    public String getException() {
        return exception;
    }

    public String getMessage() {
        return message;
    }

    public String getStackTrace() {
        return stackTrace;
    }
}
