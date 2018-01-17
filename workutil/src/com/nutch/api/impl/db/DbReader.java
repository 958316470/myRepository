package com.nutch.api.impl.db;

import com.nutch.api.model.request.DbFilter;
import com.nutch.metadata.Nutch;
import com.nutch.storage.StorageUtils;
import com.nutch.storage.WebPage;
import com.nutch.util.TableUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.gora.query.Query;
import org.apache.gora.query.Result;
import org.apache.gora.store.DataStore;
import org.apache.hadoop.conf.Configuration;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DbReader {

    private DataStore<String, WebPage> store;

    public DbReader(Configuration conf, String crawlId) {
        conf = new Configuration(conf);
        if (crawlId != null) {
            conf.set(Nutch.CRAWL_ID_KEY, crawlId);
        }
        try {
            store = StorageUtils.createWebStore(conf, String.class, WebPage.class);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create webstore!", e);
        }
    }

    public Iterator<Map<String, Object>> runQuery(DbFilter filter) {
        String startKey = filter.getStartKey();
        String endKey = filter.getEndKey();
        if (!filter.isKeysReversed()) {
            startKey = reverseKey(filter.getStartKey());
            endKey = reverseKey(filter.getEndKey());
        }

        Query<String, WebPage> query = store.newQuery();
        query.setFields(prepareFields(filter.getFields()));
        if (startKey != null) {
            query.setStartKey(startKey);
            if (endKey != null) {
                query.setEndKey(endKey);
            }
        }
        Result<String, WebPage> result = store.execute(query);
        return new DbIterator(result, filter.getFields(), filter.getBatchId());
    }

    private String reverseKey(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        try {
            return TableUtil.reverseUrl(key);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Wrong url format!", e);
        }
    }

    private String[] prepareFields(Set<String> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }
        fields.remove("url");
        return fields.toArray(new String[fields.size()]);
    }
}
