package com.nutch.util;

import org.apache.hadoop.conf.Configuration;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

public class NutchConfiguration {
    public static final String UUID_KEY = "nutch.conf.uuid";

    private NutchConfiguration(){}

    private static void setUUID(Configuration conf) {
        UUID uuid = UUID.randomUUID();
        conf.set(UUID_KEY,uuid.toString());
    }

    public static String getUUID(Configuration conf) {
        return conf.get(UUID_KEY);
    }

    public static Configuration create(){
        Configuration conf = new Configuration();
        setUUID(conf);
        addNutchResources(conf);
        return conf;
    }

    public static Configuration create(boolean addNutchResources, Properties nutchProperties) {
        Configuration conf = new Configuration();
        setUUID(conf);
        if(addNutchResources){
            addNutchResources(conf);
        }
        for (Entry<Object,Object> e : nutchProperties.entrySet()) {
            conf.set(e.getKey().toString(),e.getValue().toString());
        }
        return conf;
    }

    private static Configuration addNutchResources(Configuration conf) {
        conf.addResource("nutch-default.xml");
        conf.addResource("nutch-site.xml");
        return conf;
    }
}
