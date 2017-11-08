package com.nutch.crawl;

import com.nutch.util.ObjectCache;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchScheduleFactory {
    public static final Logger LOG = LoggerFactory.getLogger(FetchScheduleFactory.class);

    private FetchScheduleFactory(){}

    public static FetchSchedule getFetchSchedule(Configuration conf) {
        String clazz = conf.get("db.fetch.schedule.class", DefaultFetchSchedule.class.getName());
        ObjectCache objectCache = ObjectCache.get(conf);
        FetchSchedule impl = (FetchSchedule) objectCache.getObject(clazz);
        if (impl == null) {
            try {
                LOG.info("Using FetchSchedule impl: " + clazz);
                Class<?> implClass = Class.forName(clazz);
                impl = (FetchSchedule) implClass.newInstance();
                impl.setConf(conf);
                objectCache.setObject(clazz, impl);
            } catch (Exception e) {
                throw new RuntimeException("Couldn't create " + clazz, e);
            }
        }
        return impl;
    }
}
