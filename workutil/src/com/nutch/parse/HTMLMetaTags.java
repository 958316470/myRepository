package com.nutch.parse;

import com.nutch.metadata.Metadata;

import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

public class HTMLMetaTags {

    private boolean noIndex = false;
    private boolean noFollow = false;
    private boolean noCache = false;
    private URL baseHref = null;
    private boolean refresh = false;
    private int refreshTime = 0;
    private URL refreshHref = null;
    private Metadata generalTags = new Metadata();
    private Properties httpEquivTags = new Properties();

    public void reset() {
        noIndex = false;
        noFollow = false;
        noCache = false;
        baseHref = null;
        refresh = false;
        refreshTime = 0;
        refreshHref = null;
        generalTags.clear();
        httpEquivTags.clear();
    }

    public void setNoFollow() {
        noFollow = true;
    }

    public void setNoIndex() {
        noIndex = true;
    }

    public void setNoCache() {
        noCache = true;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public void setBaseHref(URL baseHref) {
        this.baseHref = baseHref;
    }

    public void setRefreshHref(URL refreshHref) {
        this.refreshHref = refreshHref;
    }

    public void setRefreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
    }

    public boolean isNoIndex() {
        return noIndex;
    }

    public boolean isNoFollow() {
        return noFollow;
    }

    public boolean isNoCache() {
        return noCache;
    }

    public URL getBaseHref() {
        return baseHref;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public int getRefreshTime() {
        return refreshTime;
    }

    public URL getRefreshHref() {
        return refreshHref;
    }

    public Metadata getGeneralTags() {
        return generalTags;
    }

    public Properties getHttpEquivTags() {
        return httpEquivTags;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("base=" + baseHref + ", noCache=" + noCache + ", noFollow="
                + noFollow + ", noIndex=" + noIndex + ", refresh=" + refresh
                + ", refreshHref=" + refreshHref + "\n");
        sb.append(" * general tags:\n");
        String[] names = generalTags.names();
        for (String name : names) {
            String key = name;
            sb.append("   - " + key + "\t=\t" + generalTags.get(key) + "\n");
        }
        sb.append(" * http-equiv tags:\n");
        Iterator<Object> it = httpEquivTags.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            sb.append("   - " + key + "\t=\t" + httpEquivTags.get(key) + "\n");
        }
        return sb.toString();
    }
}
