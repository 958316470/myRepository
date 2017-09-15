package com.nutch.api.model.request;

import java.io.Serializable;
import java.util.Collection;

public class SeedList implements Serializable {
    private Long id;
    private String name;
    private Collection<SeedUrl> seedUrls;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<SeedUrl> getSeedUrls() {
        return seedUrls;
    }

    public void setSeedUrls(Collection<SeedUrl> seedUrls) {
        this.seedUrls = seedUrls;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        SeedList other = (SeedList) obj;
        if (id == null && other.id != null) {
            return false;
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
