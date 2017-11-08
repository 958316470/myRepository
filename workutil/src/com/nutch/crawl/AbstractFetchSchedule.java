package com.nutch.crawl;

import com.nutch.storage.WebPage;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFetchSchedule extends Configured implements FetchSchedule{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractFetchSchedule.class);
    protected int defaultInterval;
    protected int maxInterval;
    private static final Set<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();
    static {
        FIELDS.add(WebPage.Field.FETCH_TIME);
        FIELDS.add(WebPage.Field.RETRIES_SINCE_FETCH);
        FIELDS.add(WebPage.Field.FETCH_INTERVAL);
    }

    public AbstractFetchSchedule() {
        super(null);
    }

    public AbstractFetchSchedule(Configuration conf) {
        super(conf);
    }

    @Override
    public void setConf(Configuration conf){
        super.setConf(conf);
        if (conf == null) {
            return;
        }
        defaultInterval = conf.getInt("db.fetch.interval.default", 0);
        maxInterval = conf.getInt("db.fetch.interval.max", 0);
        LOG.info("defaultInterval=" + defaultInterval);
        LOG.info("maxInterval=" + maxInterval);
    }

    @Override
    public void initializeSchedule(String url, WebPage page) {
        page.setFetchTime(System.currentTimeMillis());
        page.setFetchInterval(defaultInterval);
        page.setRetriesSinceFetch(0);
    }

    @Override
    public void setFetchSchedule(String url, WebPage page, long prevFetchTime, long prevModifiedTime, long fetchTime, long modifiedTime, int state) {
        page.setRetriesSinceFetch(0);
    }

    @Override
    public void setPageGoneSchedule(String url, WebPage page, long prevFetchTime, long prevModifiedTime, long fetchTime) {
        if ((page.getFetchInterval() * 1.5f) < maxInterval) {
            int newFetchInterval = (int) (page.getFetchInterval() * 1.5f);
            page.setFetchInterval(newFetchInterval);
        } else {
            page.setFetchInterval((int) (maxInterval * 0.9f));
        }
        page.setFetchTime(fetchTime + page.getFetchInterval() * 1000L);
    }

    @Override
    public void setPageRetrySchedule(String url, WebPage page, long prevFetchTime, long prevModifiedTime, long fetchTime) {
        page.setFetchTime(fetchTime + SECONDS_PER_DAY * 1000L);
        page.setRetriesSinceFetch(page.getRetriesSinceFetch() + 1);
    }

    @Override
    public long calculateLastFetchTime(WebPage page) {
        return page.getFetchTime() - page.getFetchInterval() * 1000L;
    }

    @Override
    public boolean shouldFetch(String url, WebPage page, long curTime){
        long fetchTime = page.getFetchTime();
        if (fetchTime - curTime > maxInterval * 1000L) {
            if (page.getFetchInterval() > maxInterval) {
                page.setFetchInterval(Math.round(maxInterval * 0.9f));
            }
            page.setFetchTime(curTime);
        }
        return fetchTime <= curTime;
    }

    @Override
    public void forceRefetch(String url, WebPage page, boolean asap) {
        if (page.getFetchInterval() > maxInterval) {
            page.setFetchInterval(Math.round(maxInterval * 0.9f));
        }
        page.setStatus((int) CrawlStatus.STATUS_UNFETCHED);
        page.setRetriesSinceFetch(0);
        page.setModifiedTime(0L);
        if (asap) {
            page.setFetchTime(System.currentTimeMillis());
        }
    }

    @Override
    public Set<WebPage.Field> getFields() {
        return FIELDS;
    }
}
