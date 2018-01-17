package com.nutch.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;

public class NutchJobConf extends JobConf{
    public NutchJobConf(Configuration conf) {
        super(conf, NutchJobConf.class);
    }
}
