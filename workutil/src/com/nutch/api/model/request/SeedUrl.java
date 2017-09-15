package com.nutch.api.model.request;

import java.io.Serializable;

public class SeedUrl implements Serializable {
    private Long id;
    private SeedList seedList;
    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SeedList getSeedList() {
        return seedList;
    }

    public void setSeedList(SeedList seedList) {
        this.seedList = seedList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SeedUrl other = (SeedUrl) obj;
        if (id == null && other.id != null) {
            return false;
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
