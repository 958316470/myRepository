package com.nutch.api.impl;

import com.google.common.collect.Maps;
import com.nutch.api.ConfManager;
import com.nutch.api.model.request.NutchConfig;
import com.nutch.api.resources.ConfigResource;
import com.nutch.util.NutchConfiguration;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class RAMConfManager implements ConfManager{

    private Map<String, Configuration> configurations = Maps.newHashMap();

    private AtomicInteger newConfigId = new AtomicInteger();

    public RAMConfManager() {
        configurations.put(ConfigResource.DEFAULT, NutchConfiguration.create());
    }

    @Override
    public Set<String> list() {
        return configurations.keySet();
    }

    @Override
    public Configuration get(String confId) {
        if (confId == null) {
            return configurations.get(ConfigResource.DEFAULT);
        }
        return configurations.get(confId);
    }

    @Override
    public Map<String, String> getAsMap(String confId) {
        Configuration configuration = configurations.get(confId);
        if (configuration == null) {
            return Collections.emptyMap();
        }
        Iterator<Map.Entry<String, String>> iterator = configuration.iterator();
        Map<String, String> configMap = Maps.newTreeMap();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            configMap.put(entry.getKey(), entry.getValue());
        }
        return configMap;
    }

    @Override
    public void setProperty(String confId, String propName, String propValue) {
        if (!configurations.containsKey(confId)) {
            throw new IllegalArgumentException("Unknown configId '" + confId + "'");
        }
        Configuration conf = configurations.get(confId);
        conf.set(propName, propValue);
    }

    @Override
    public void delete(String confId) {
        configurations.remove(confId);
    }

    public String create(NutchConfig nutchConfig) {
        if (StringUtils.isBlank(nutchConfig.getConfigId())) {
            nutchConfig.setConfigId(String.valueOf(newConfigId.incrementAndGet()));
        }
        if (!canCreate(nutchConfig)) {
            throw new IllegalArgumentException("Config already exists");
        }
        createHadoopConfig(nutchConfig);
        return nutchConfig.getConfigId();
    }

    private boolean canCreate(NutchConfig nutchConfig) {
        if (nutchConfig.isForce()) {
            return true;
        }
        if (!configurations.containsKey(nutchConfig.getConfigId())) {
            return true;
        }
        return false;
    }

    private void createHadoopConfig(NutchConfig nutchConfig) {
        Configuration conf = NutchConfiguration.create();
        configurations.put(nutchConfig.getConfigId(), conf);
        if (MapUtils.isEmpty(nutchConfig.getParams())) {
            return;
        }
        for (Map.Entry<String, String> e : nutchConfig.getParams().entrySet()) {
            conf.set(e.getKey(), e.getValue());
        }
    }
}
