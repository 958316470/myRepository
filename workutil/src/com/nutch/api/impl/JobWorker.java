package com.nutch.api.impl;

import com.nutch.api.model.request.JobConfig;
import com.nutch.api.model.response.JobInfo.State;
import com.nutch.api.model.response.JobInfo;
import com.nutch.api.resources.ConfigResource;
import com.nutch.metadata.Nutch;
import com.nutch.util.NutchTool;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class JobWorker implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(JobWorker.class);
    private NutchTool tool;
    private JobInfo jobInfo;
    private JobConfig jobConfig;

    public JobWorker(JobConfig jobConfig, Configuration conf, NutchTool tool) {
        this.tool = tool;
        this.jobConfig = jobConfig;
        if (jobConfig.getConfId() == null) {
            jobConfig.setConfId(ConfigResource.DEFAULT);
        }
        jobInfo = new JobInfo(generateId(), jobConfig, State.IDLE, "idle");
        if (jobConfig.getCrawlId() != null) {
            conf.set(Nutch.CRAWL_ID_KEY, jobConfig.getConfId());
        }
    }

    private String generateId() {
        if (jobConfig.getCrawlId() == null) {
            return MessageFormat.format("{0}-{1}-{2}", jobConfig.getConfId(), jobConfig.getType(), String.valueOf(hashCode()));
        }
        return MessageFormat.format("{0}-{1}-{2}-{3}", jobConfig.getCrawlId(), jobConfig.getConfId(), jobConfig.getType(), String.valueOf(hashCode()));
    }

    @Override
    public void run() {
        try {
            getInfo().setState(State.RUNNING);
            getInfo().setMsg("OK");
            getInfo().setResult(tool.run(getInfo().getArgs()));
            getInfo().setState(State.FINISHED);
        } catch (Exception e) {
            LOG.error("Cannot run job worker!", e);
            getInfo().setMsg("ERROR: " + e.toString());
            getInfo().setState(State.FAILED);
        }
    }

    public boolean stopJob() {
        getInfo().setState(State.STOPPING);
        try {
            return tool.stopJob();
        } catch (Exception e) {
            throw new RuntimeException("Cannot stop job with id " + getInfo().getId(), e);
        }
    }

    public boolean killJob() {
        getInfo().setState(State.KILLING);
        try {
            boolean result = tool.killJob();
            getInfo().setState(State.KILLED);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Cannot kill job with id " + getInfo().getId(), e);
        }
    }

    public JobInfo getInfo() {
        return jobInfo;
    }

    public void setInfo(JobInfo jobInfo) {
        this.jobInfo = jobInfo;
    }
}
