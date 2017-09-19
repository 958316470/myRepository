package com.nutch.storage;

public class WebPage {


    public static enum Field{
        BASE_URL(0,"baseurl"),STATUS(1,"status"),FETCH_TIME(2,"fetchTime"),
        PREV_FETCH_TIME(3,"prevFetchTime"),FETCH_INTERVAL(4,"fetchInterval"),
        RETRIES_SINCE_FETCH(5,"retriesSinceFetch"),MODIFIED_TIME(6,"modifiedTime"),
        PREV_MODIFIED_TIME(7,"prevModifiedTime"),PROTOCOL_STATUS(8,"protocolStatus"),
        CONTENT(9,"content"),CONTENT_TYPE(10,"contentType"),PREV_SIGNATURE(11,"prevSignature"),
        SIGNATURE(12,"signature"),TITLE(13,"title"),TEXT(14,"text"),PARSE_STATUS(15,"parseStatus"),
        SCORE(16,"score"),REPR_URL(17,"reprUrl"),HEADERS(18,"headers"),OUTLINKS(19,"outlinks"),INLINKS(20,"inlinks"),
        MARKERS(21,"markers"),METADATA(22,"metadata"),BATCH_ID(23,"batchId");

        private int index;
        private String name;

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
        public String toString(){
            return name;
        }

        Field(int index, String name) {
            this.index = index;
            this.name = name;
        }
    };

    public static final String[] _ALL_FIELDS = {};
}
