package com.nutch.api.model.response;

import com.nutch.api.JobManager.JobType;
import com.nutch.api.model.request.JobConfig;

import java.util.Map;

public class JobInfo {

    public static enum State {
        IDLE, RUNNING, FINISHED, FAILED, KILLED, STOPPING, KILLING, ANY
    }

    private String id;
    private JobType type;
    private String confId;
    private Map<String, Object> args;
    private Map<String, Object> result;
    private State state;
    private String msg;
    private String crawlId;

    public JobInfo(String id, JobConfig config, State state, String msg) {
        this.id = id;
        this.confId = config.getConfId();
        this.type = config.getType();
        this.args = config.getArgs();
        this.state = state;
        this.msg = msg;
        this.crawlId = config.getCrawlId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCrawlId() {
        return crawlId;
    }

    public void setCrawlId(String crawlId) {
        this.crawlId = crawlId;
    }
}
