package com.nutch.parse;

public class ParserNotFound extends ParserException{

    private static final long serialVersionUID =  23993993939L;
    private String url;
    private String contentType;
    public ParserNotFound(String message) {
        super(message);
    }

    public ParserNotFound(String url, String contentType) {
        this(url,contentType, "parser not found for contentType=" + contentType + " url=" + url);
    }

    public ParserNotFound(String url, String contentType, String message) {
        super(message);
        this.url = url;
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public String getContentType() {
        return contentType;
    }
}
