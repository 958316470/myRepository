package com.nutch.api.model.response;

import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class NutchStatus {
    private Date startDate;
    private Set<String> configuration;
    private Collection<JobInfo> jobs;
    private Collection<JobInfo> runningJobs;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Set<String> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Set<String> configuration) {
        this.configuration = configuration;
    }

    public Collection<JobInfo> getJobs() {
        return jobs;
    }

    public void setJobs(Collection<JobInfo> jobs) {
        this.jobs = jobs;
    }

    public Collection<JobInfo> getRunningJobs() {
        return purgeFinishedFailedJobs(runningJobs);
    }

    public void setRunningJobs(Collection<JobInfo> runningJobs) {
        this.runningJobs = runningJobs;
    }

    private Collection<JobInfo> purgeFinishedFailedJobs(Collection<JobInfo> runningJobColl){
        if(CollectionUtils.isNotEmpty(runningJobColl)){
            Iterator<JobInfo> jobInfoIterable = runningJobColl.iterator();
            while (jobInfoIterable.hasNext()){
                JobInfo jobInfo = jobInfoIterable.next();
                if(jobInfo.getState().equals(JobInfo.State.FINISHED)){
                    jobInfoIterable.remove();
                }else if(jobInfo.getState().equals(JobInfo.State.FAILED)){
                    jobInfoIterable.remove();
                }
            }
        }
        return runningJobColl;
    }
}
