package com.nutch.crawl;

import com.nutch.storage.WebPage;

public class DefaultFetchSchedule extends AbstractFetchSchedule {

    @Override
    public void setFetchSchedule(String url, WebPage page, long pervFetchTime, long prevModifiedTime, long fetchTime, long modifiedTime, int state) {
        super.setFetchSchedule(url, page, pervFetchTime, prevModifiedTime, fetchTime, modifiedTime, state);
        page.setFetchTime(fetchTime + page.getFetchInterval() * 1000L);
        page.setModifiedTime(modifiedTime);
        page.setPrevModifiedTime(prevModifiedTime);
    }
}
