package com.nutch.util.domain;

public class DomainSuffix {

    public enum Status {
        INFRASTRUCTURE, SPONSORED, UNSPONSORED, STARTUP, PROPOSED, DELETED, PSEUDO_DOMAIN, DEPRECATED,
        IN_USE, NOT_IN_USE, REJECTED
    }

    private String domain;
    private Status status;
    private float boost;
    public static final float DEFAULT_BOOST = 1.0f;
    public static final Status DEFAULT_STATUS = Status.IN_USE;

    public DomainSuffix(String domain, Status status, float boost) {
        this.domain = domain;
        this.status = status;
        this.boost = boost;
    }

    public DomainSuffix(String domain) {
        this(domain, DEFAULT_STATUS, DEFAULT_BOOST);
    }

    public String getDomain() {
        return domain;
    }

    public Status getStatus() {
        return status;
    }

    public float getBoost() {
        return boost;
    }

    @Override
    public String toString() {
        return domain;
    }
}
