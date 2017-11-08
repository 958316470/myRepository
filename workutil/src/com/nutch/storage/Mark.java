package com.nutch.storage;

import org.apache.avro.util.Utf8;

public enum  Mark {
    INJECT_MARK("_injmrk_"),GENERATE_MARK("_gnmrk_"),FETCH_MARK("_ftcmrk_"),
    PARSE_MARK("__prsmrk__"),UPDATEDB_MARK("_updmrk_"),INDEX_MARK("_idxmrk_");

    private Utf8 name;
    Mark(String name) {
        this.name = new Utf8(name);
    }

    public void putMark(WebPage page, Utf8 markValue) {
        page.getMarkers().put(name, markValue);
    }

    public void mark(WebPage page, String markValue) {
        putMark(page, new Utf8(markValue));
    }

    public Utf8 removeMark(WebPage page) {
        return (Utf8) page.getMarkers().put(name,null);
    }
    public Utf8 checkMark(WebPage page) {
        return (Utf8) page.getMarkers().get(name);
    }
    public Utf8 getName() {
        return name;
    }
    public Utf8 removeMarkIfExist(WebPage page){
        if (checkMark(page) != null) {
            return removeMark(page);
        }
        return null;
    }
}
