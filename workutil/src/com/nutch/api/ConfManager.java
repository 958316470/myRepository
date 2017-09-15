package com.nutch.api;

import com.nutch.api.model.request.NutchConfig;
import org.apache.hadoop.conf.Configuration;

import java.util.Map;
import java.util.Set;

public interface ConfManager {

    public Set<String> list();

    public Configuration get(String confId);

    public Map<String,String> getAsMap(String confId);

    public void delete(String confId);

    public void setPropeerty(String confId,String propName,String propValue);

    public String create(NutchConfig nutchConfig);
}
