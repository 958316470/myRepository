package com.nutch.api.impl;

import com.nutch.api.ConfManager;
import com.nutch.api.JobManager;
import com.nutch.api.model.request.JobConfig;
import com.nutch.api.model.response.JobInfo;
import com.nutch.util.NutchTool;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;

import java.util.Collection;

public class RAMJobManager implements JobManager {

    private JobFactory jobFactory;
    private NutchServerPoolExecutor executor;
    private ConfManager configManager;

    public RAMJobManager(JobFactory jobFactory, NutchServerPoolExecutor executor, ConfManager configManager) {
        this.jobFactory = jobFactory;
        this.executor = executor;
        this.configManager = configManager;
    }

    @Override
    public Collection<JobInfo> list(String crawlId, JobInfo.State state) {
        if (state == null || state == JobInfo.State.ANY) {
            return executor.getAllJobs();
        }
        if (state == JobInfo.State.RUNNING || state == JobInfo.State.IDLE) {
            return executor.getJobRunning();
        }
        return executor.getJobHistory();
    }

    @Override
    public JobInfo get(String crawlId, String id) {
        return executor.getInfo(id);
    }

    @Override
    public String create(JobConfig jobConfig) {
        if (jobConfig.getArgs() == null) {
            throw new IllegalArgumentException("Arguments cannot be null!");
        }
        Configuration conf = cloneConfiguration(jobConfig.getConfId());
        NutchTool tool = createTool(jobConfig, conf);
        JobWorker worker = new JobWorker(jobConfig, conf, tool);
        executor.execute(worker);
        executor.purge();
        return worker.getInfo().getId();
    }

    private Configuration cloneConfiguration(String confId) {
        Configuration conf = configManager.get(confId);
        if (conf == null) {
            throw new IllegalArgumentException("Unknown confId " + confId);
        }
        return new Configuration(conf);
    }

    private NutchTool createTool(JobConfig jobConfig, Configuration conf){
        if (StringUtils.isNotBlank(jobConfig.getJobClassName())) {
            return jobFactory.createToolByClassName(jobConfig.getJobClassName(), conf);
        }
        return jobFactory.createToolByType(jobConfig.getType(), conf);
    }
    @Override
    public boolean abort(String crawlId, String id) {
        return executor.findWorker(id).killJob();
    }


    @Override
    public boolean stop(String crawlId, String id) {
        return executor.findWorker(id).stopJob();
    }
}
