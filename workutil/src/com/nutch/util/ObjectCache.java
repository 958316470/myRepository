package com.nutch.util;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * 不同的配置单实例化相对应的类，并放入Map中
 *
 * @author 95831
 */
public class ObjectCache {

    private static final Logger log = LoggerFactory.getLogger(ObjectCache.class);

    private static final WeakHashMap<Configuration,ObjectCache> CACHE = new WeakHashMap<>();

    private final HashMap<String,Object> objectMap;

    private ObjectCache(){
        objectMap = new HashMap<>();
    }

    public static ObjectCache get(Configuration conf){
        ObjectCache objectCache = CACHE.get(conf);
        if(objectCache == null){
            log.debug("该配置对应的对象没有创建，"+ conf +", 正在初始化一个新的实例。");
            objectCache = new ObjectCache();
            CACHE.put(conf,objectCache);
        }
        return objectCache;
    }

    public Object getObject(String key){
        return objectMap.get(key);
    }

    public void setObject(String key,Object value){
        objectMap.put(key, value);
    }
}
