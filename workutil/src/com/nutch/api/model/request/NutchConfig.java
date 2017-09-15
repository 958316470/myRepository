package com.nutch.api.model.request;

import java.util.Collections;
import java.util.Map;

public class NutchConfig {
    private String configId;
    private boolean force = false;
    private Map<String, String> params = Collections.emptyMap();

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
