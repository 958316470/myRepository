package com.nutch.api.impl.db;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.nutch.storage.Mark;
import com.nutch.storage.WebPage;
import com.nutch.util.NutchJob;
import com.nutch.util.TableUtil;
import org.apache.avro.util.Utf8;
import org.apache.commons.collections.CollectionUtils;
import org.apache.gora.query.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class DbIterator extends UnmodifiableIterator<Map<String, Object>>{

    private static final Logger LOG = LoggerFactory.getLogger(DbIterator.class);
    private Result<String, WebPage> result;
    private boolean hasNext;
    private String url;
    private WebPage page;
    private Utf8 batchId;
    private Set<String> commonFields;

    DbIterator(Result<String, WebPage> res, Set<String> fields, String batchId) {
        this.result = res;
        if (batchId != null) {
            this.batchId = new Utf8(batchId);
        }
        if (fields != null) {
            this.commonFields = Sets.newTreeSet(fields);
        }
        try {
            skipNonRelevant();
        } catch (Exception e) {
            LOG.error("Cannot create db iterator!", e);
        }
    }

    private void skipNonRelevant() throws Exception {
        hasNext = result.next();
        if (!hasNext) {
            return;
        }
        if (batchId == null) {
            return;
        }
        while (hasNext) {
            WebPage page = result.get();
            Utf8 mark = Mark.UPDATEDB_MARK.checkMark(page);
            if (NutchJob.shouldProcess(mark, batchId)) {
                return;
            }
            LOG.debug("Skipping {}: different batch id", result.getKey());
            hasNext = result.next();
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Map<String, Object> next() {
        url = result.getKey();
        page = WebPage.newBuilder(result.get()).build();
        try {
            skipNonRelevant();
            if (!hasNext) {
                result.close();
            }
        } catch (Exception e) {
            LOG.error("Cannot get next result!", e);
            hasNext = false;
            return null;
        }
        return pageAsMap(url, page);
    }

    private Map<String, Object> pageAsMap(String url, WebPage page) {
        Map<String, Object> result = DbPageConverter.convertPage(page, commonFields);
        if (CollectionUtils.isEmpty(commonFields) || commonFields.contains("url")) {
            result.put("url", TableUtil.unreverseUrl(url));
        }
        return result;
    }
}
