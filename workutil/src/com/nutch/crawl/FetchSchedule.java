package com.nutch.crawl;

import com.nutch.storage.WebPage;
import org.apache.hadoop.conf.Configurable;

import java.util.Collection;

public interface FetchSchedule extends Configurable{

    public static final int STATUS_UNKNOWN = 0;
    public static final int STATUS_MODIFIED = 1;
    public static final int STATUS_NOTMODIFIED = 2;
    public static final int SECONDS_PER_DAY = 3600 * 24;

    public void initializeSchedule(String url, WebPage page);

    public void setFetchSchedule(String url, WebPage page, long prevFetchTime, long prevModifiedTime, long fetchTime, long modifiedTime, int state);

    public void setPageGoneSchedule(String url, WebPage page, long prevFetchTime, long prevModifiedTime, long fetchTime);

    public void setPageRetrySchedule(String url, WebPage page, long prevFetchTime, long prevModifiedTime, long fetchTime);

    public long calculateLastFetchTime(WebPage page);

    public boolean shouldFetch(String url, WebPage page, long curTime);

    public void forceRefetch(String url, WebPage page, boolean asap);

    public Collection<WebPage.Field> getFields();
}
