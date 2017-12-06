package com.nutch.api.impl;

import com.google.common.collect.Maps;
import com.nutch.api.JobManager.JobType;
import com.nutch.fetcher.FetcherJob;
import com.nutch.crawl.IndexingJob;
import com.nutch.crawl.GeneratorJob;
import com.nutch.crawl.InjectorJob;
import com.nutch.crawl.ParserJob;
import com.nutch.crawl.DbUpdaterJob;
import com.nutch.crawl.WebTableReader;
import com.nutch.util.NutchTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ReflectionUtils;

import java.util.Map;

public class JobFactory {
    private static Map<JobType, Class<? extends NutchTool>> typeToClass;

    static {
        typeToClass = Maps.newHashMap();
        typeToClass.put(JobType.FETCH, FetcherJob.class);
        typeToClass.put(JobType.GENERATE, GeneratorJob.class);
        typeToClass.put(JobType.INDEX, IndexingJob.class);
        typeToClass.put(JobType.INJECT, InjectorJob.class);
        typeToClass.put(JobType.PARSE, ParserJob.class);
        typeToClass.put(JobType.UPDATEDB, DbUpdaterJob.class);
        typeToClass.put(JobType.READDB, WebTableReader.class);
    }

    public NutchTool createToolByType(JobType type, Configuration conf) {
        if (!typeToClass.containsKey(type)) {
            return null;
        }
        Class<? extends NutchTool> clz = typeToClass.get(type);
        return createTool(clz, conf);
    }

    public NutchTool createToolByClassName(String className, Configuration conf) {
        try {
            Class clz = Class.forName(className);
            return createTool(clz, conf);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private NutchTool createTool(Class<? extends NutchTool> clz, Configuration conf) {
        return ReflectionUtils.newInstance(clz, conf);
    }
}
