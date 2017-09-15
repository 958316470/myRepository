package com.nutch.api.model.request;

import java.util.Set;

/**
 * @author 95831
 */
public class DbFilter {
    private String batchId;
    private String startKey;
    private String endKey;
    private boolean isKeysReversed = false;
    private Set<String> fields;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getStartKey() {
        return startKey;
    }

    public void setStartKey(String startKey) {
        this.startKey = startKey;
    }

    public String getEndKey() {
        return endKey;
    }

    public void setEndKey(String endKey) {
        this.endKey = endKey;
    }

    public boolean isKeysReversed() {
        return isKeysReversed;
    }

    public void setKeysReversed(boolean keysReversed) {
        isKeysReversed = keysReversed;
    }

    public Set<String> getFields() {
        return fields;
    }

    public void setFields(Set<String> fields) {
        this.fields = fields;
    }
}
