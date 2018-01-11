package com.nutch.parse;

import com.nutch.storage.WebPage;

import java.util.concurrent.Callable;

public class ParseCallable implements Callable<Parse>{

    private Parser p;
    private WebPage content;
    private String url;

    public ParseCallable(Parser p, WebPage content, String url) {
        this.p = p;
        this.content = content;
        this.url = url;
    }

    @Override
    public Parse call() throws Exception {
        return p.getParse(url, content);
    }
}
