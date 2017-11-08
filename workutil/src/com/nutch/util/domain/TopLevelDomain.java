package com.nutch.util.domain;

public class TopLevelDomain extends DomainSuffix {

    public enum Type {
        INFRASTRUCTURE,GENERIC,COUNTRY
    }

    private Type type;
    private String countryName = null;

    public TopLevelDomain(String domain, Type type, Status status, float boost) {
        super(domain, status, boost);
        this.type = type;
    }
    public TopLevelDomain(String domain, Status status, float boost, String countryName) {
        super(domain, status, boost);
        this.type = Type.COUNTRY;
        this.countryName = countryName;
    }

    public Type getType() {
        return type;
    }

    public String getCountryName() {
        return countryName;
    }
}
