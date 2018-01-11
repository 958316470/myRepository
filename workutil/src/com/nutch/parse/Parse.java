package com.nutch.parse;

import com.nutch.storage.ParseStatus;

public class Parse {

    private String text;
    private String title;
    private Outlink[] outlinks;
    private ParseStatus parseStatus;

    public Parse() {}

    public Parse(String text, String title, Outlink[] outlinks, ParseStatus status) {
        this.text = text;
        this.title = title;
        this.outlinks = outlinks;
        this.parseStatus = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Outlink[] getOutlinks() {
        return outlinks;
    }

    public void setOutlinks(Outlink[] outlinks) {
        this.outlinks = outlinks;
    }

    public ParseStatus getParseStatus() {
        return parseStatus;
    }

    public void setParseStatus(ParseStatus parseStatus) {
        this.parseStatus = parseStatus;
    }
}
