package com.nutch.api.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.nutch.api.model.response.JobInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NutchServerPoolExecutor extends ThreadPoolExecutor {

    private Queue<JobWorker> workersHistory;
    private Queue<JobWorker> runningWorkers;

    public NutchServerPoolExecutor(int corePoolSize, int maximumPoolSize,
                                   long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        workersHistory = Queues.newArrayBlockingQueue(maximumPoolSize);
        runningWorkers = Queues.newArrayBlockingQueue(maximumPoolSize);
    }

    @Override
    protected void beforeExecute(Thread thread, Runnable runnable) {
        super.beforeExecute(thread, runnable);
        synchronized (runningWorkers) {
            runningWorkers.offer((JobWorker) runnable);
        }
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        super.afterExecute(runnable, throwable);
        synchronized (runningWorkers) {
            runningWorkers.remove((JobWorker) runnable);
        }
        JobWorker worker = (JobWorker) runnable;
        addStatusToHistory(worker);
    }

    private void addStatusToHistory(JobWorker worker) {
        synchronized (workersHistory) {
            if (!workersHistory.offer(worker)) {
                workersHistory.poll();
                workersHistory.add(worker);
            }
        }
    }

    public JobWorker findWorker(String jobId) {
        synchronized (runningWorkers) {
            for (JobWorker worker : runningWorkers) {
                if (StringUtils.equals(worker.getInfo().getId(), jobId)) {
                    return worker;
                }
            }
        }
        return null;
    }

    public Collection<JobInfo> getJobHistory() {
        return getJobsInfo(workersHistory);
    }

    public Collection<JobInfo> getJobRunning() {
        return getJobsInfo(runningWorkers);
    }

    public Collection<JobInfo> getAllJobs() {
        return CollectionUtils.union(getJobRunning(), getJobHistory());
    }

    private Collection<JobInfo> getJobsInfo(Collection<JobWorker> workers) {
        List<JobInfo> jobInfos = Lists.newLinkedList();
        for (JobWorker worker : workers) {
            jobInfos.add(worker.getInfo());
        }
        return jobInfos;
    }

    public JobInfo getInfo(String jobId) {
        for (JobInfo jobInfo : getAllJobs()) {
            if (StringUtils.equals(jobId, jobInfo.getId())) {
                return jobInfo;
            }
        }
        return null;
    }
}
