package com.nutch.util;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import com.nutch.metadata.Nutch;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class NutchTool extends Configured {

    protected HashMap<String, Object> results = new HashMap<>();
    protected Map<String, Object> status = Collections.synchronizedMap(new HashMap<String, Object>());
    protected Job currentJob;
    protected int numJobs;
    protected int currentJobNum;

    public abstract Map<String, Object> run(Map<String, Object> args) throws Exception;

    public float getProgress() {
        float res = 0;
        if (currentJob != null) {
            try {
                res = (currentJob.mapProgress() + currentJob.reduceProgress()) / 2.0f;
            } catch (IOException e) {
                e.printStackTrace();
                res = 0;
            } catch (IllegalStateException ile) {
                ile.printStackTrace();
                res = 0;
            }
        }
        if (numJobs > 1) {
            res = (currentJobNum + res) / (float) numJobs;
        }
        status.put(Nutch.STAT_PROGRESS, res);
        return res;
    }

    public Map<String,Object> getStatus(){
        return status;
    }

    public boolean stopJob() throws Exception{
        return killJob();
    }

    public boolean killJob() throws Exception{
        if(currentJob != null && !currentJob.isComplete()){
            try {
                currentJob.killJob();
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
