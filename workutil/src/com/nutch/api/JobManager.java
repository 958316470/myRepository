package com.nutch.api;

import com.nutch.api.model.request.JobConfig;
import com.nutch.api.model.response.JobInfo;
import com.nutch.api.model.response.JobInfo.State;

import java.util.Collection;

/**
 * 任务管理类
 *
 * @author 95831
 */
public interface JobManager {
    public static enum JobType{
        INJECT,GENERATE,FETCH,PARSE,UPDATEDB,INDEX,READDB,CLASS
    }

    public Collection<JobInfo> list(String crawlId,State state);

    public JobInfo get(String crawlId,String id);

    public String create(JobConfig jobConfig);

    public boolean abort(String crawlId,String id);

    public boolean stop(String crawlId,String id);
}
