package com.nutch.crawl;

import com.nutch.storage.WebPage;
import org.apache.hadoop.conf.Configuration;

public class AdaptiveFetchSchedule extends AbstractFetchSchedule {

    private float INC_RATE;
    private float DEC_RATE;
    private int MAX_INTERVAL;
    private int MIN_INTERVAL;
    private boolean SYNC_DELTA;
    private double SYNC_DELTA_RATE;

    @Override
    public void setConf(Configuration conf) {
        super.setConf(conf);
        if (conf == null) {
            return;
        }
        INC_RATE = conf.getFloat("db.fetch.schedule.adaptive.inc_rate", 0.2f);
        DEC_RATE = conf.getFloat("db.fetch.schedule.adaptive.dec_rate", 0.2f);
        MIN_INTERVAL = conf.getInt("db.fetch.schedule.adaptive.min_interval", 60);
        MAX_INTERVAL = conf.getInt("db.fetch.schedule.adaptive.max_interval",
                SECONDS_PER_DAY * 365); // 1 year
        SYNC_DELTA = conf.getBoolean("db.fetch.schedule.adaptive.sync_delta", true);
        SYNC_DELTA_RATE = conf.getFloat(
                "db.fetch.schedule.adaptive.sync_delta_rate", 0.2f);
    }

    @Override
    public void setFetchSchedule(String url, WebPage page, long prevFetchTime, long prevModifiedTime, long fetchTime, long modifiedTime, int state) {
        super.setFetchSchedule(url, page, prevFetchTime, prevModifiedTime, fetchTime, modifiedTime, state);
        long refTime = fetchTime;
        if (modifiedTime <= 0) {
            modifiedTime = fetchTime;
        }
        int interval = page.getFetchInterval();
        switch (state) {
            case FetchSchedule.STATUS_MODIFIED:
                interval *= (1.0f - DEC_RATE);
                break;
            case  FetchSchedule.STATUS_NOTMODIFIED:
                interval *= (1.0f + INC_RATE);
                break;
            case FetchSchedule.STATUS_UNKNOWN:
                break;
        }

        if (SYNC_DELTA) {
            int delta = (int) ((fetchTime - modifiedTime) / 1000L);
            if (delta > interval) {
                interval = delta;
            }
            refTime = fetchTime - Math.round(delta * SYNC_DELTA_RATE);
        }
        if (interval < MIN_INTERVAL) {
            interval = MIN_INTERVAL;
        }
        if (interval > MAX_INTERVAL) {
            interval = MAX_INTERVAL;
        }
        page.setFetchInterval(interval);
        page.setFetchTime(refTime + interval * 1000L);
        page.setModifiedTime(modifiedTime);
        page.setPrevModifiedTime(prevModifiedTime);
    }

}
