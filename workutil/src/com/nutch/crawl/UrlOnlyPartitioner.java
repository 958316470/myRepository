package com.nutch.crawl;

import org.apache.hadoop.mapreduce.Partitioner;

public class UrlOnlyPartitioner extends Partitioner {
    @Override
    public int getPartition(Object o, Object o2, int i) {
        return 0;
    }
}
