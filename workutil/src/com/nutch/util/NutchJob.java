package com.nutch.util;

import com.nutch.metadata.Nutch;
import org.apache.avro.util.Utf8;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;

public class NutchJob extends Job {

    @Deprecated
    public NutchJob(Configuration conf) throws IOException {
        super(conf);
        super.setJarByClass(this.getClass());
    }
    @Deprecated
    public NutchJob(Configuration conf, String jobName) throws IOException{
        super(conf, jobName);
        String crawlId = conf.get("storage.crawl.id");
        if (!StringUtils.isEmpty(crawlId)) {
            jobName = "[" + crawlId + "]" + jobName;
            setJobName(jobName);
        }
        super.setJarByClass(this.getClass());
    }

    public static NutchJob getInstance(Configuration conf) throws IOException {
        NutchJobConf jobConf = new NutchJobConf(conf);
        return new NutchJob(jobConf);
    }

    public static NutchJob getInstance(Configuration conf, String jobName) throws IOException {
        NutchJob result = getInstance(conf);
        String crawlId = conf.get("storage.crawl.id");
        if (!StringUtils.isEmpty(crawlId)) {
            jobName = "[" + crawlId + "]" + jobName;
            result.setJobName(jobName);
        }
        return result;
    }

    @Override
    public boolean waitForCompletion(boolean verbose) throws
            IOException,InterruptedException,ClassNotFoundException {
        boolean succeeded = super.waitForCompletion(verbose);
        if (!succeeded) {
            if (getConfiguration().getBoolean("fail.on.job.failure", true)) {
                throw new RuntimeException("job failed: name=" + getJobName()
                + ", jobid=" + getJobID());
            }
        }
        return succeeded;
    }

    public static boolean shouldProcess(CharSequence mark, Utf8 batchId) {
        if (mark == null) {
            return false;
        }
        boolean isAll = batchId.equals(Nutch.ALL_CRAWL_ID);
        if (!isAll && !mark.equals(batchId)) {
            return false;
        }
        return true;
    }

}
