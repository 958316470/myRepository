package com.nutch.util.domain;

import org.apache.hadoop.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;

public class DomainSuffixes {
    private static final Logger LOG = LoggerFactory.getLogger(DomainSuffixes.class);
    private HashMap<String,DomainSuffix> domains = new HashMap<String, DomainSuffix>();
    private static DomainSuffixes instance;
    private DomainSuffixes() {
        String file = "domain-suffixes.xml";
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(file);
        try {
            new DomainSuffixesReader().read(this, input);
        } catch (Exception e) {
            LOG.warn(StringUtils.stringifyException(e));
        }
    }
    public static DomainSuffixes getInstance() {
        if (instance == null) {
            instance = new DomainSuffixes();
        }
        return instance;
    }

    void addDomainSuffix(DomainSuffix tld){
        domains.put(tld.getDomain(), tld);
    }

    public boolean isDomainSuffix(String extension) {
        return domains.containsKey(extension);
    }
    public DomainSuffix get(String extension) {
        return domains.get(extension);
    }
}
