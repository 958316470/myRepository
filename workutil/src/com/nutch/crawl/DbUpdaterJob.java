package com.nutch.crawl;

import com.nutch.metadata.Nutch;
import com.nutch.scoring.ScoringFilters;
import com.nutch.storage.WebPage;
import com.nutch.util.NutchTool;
import org.apache.avro.util.Utf8;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class DbUpdaterJob extends NutchTool implements Tool{

    public static final Logger log = LoggerFactory.getLogger(DbUpdaterJob.class);

    private static final Collection<WebPage.Field> FIELDS = new HashSet<>();

    static{
        FIELDS.add(WebPage.Field.OUTLINKS);
        FIELDS.add(WebPage.Field.INLINKS);
        FIELDS.add(WebPage.Field.STATUS);
        FIELDS.add(WebPage.Field.PREV_SIGNATURE);
        FIELDS.add(WebPage.Field.SIGNATURE);
        FIELDS.add(WebPage.Field.MARKERS);
        FIELDS.add(WebPage.Field.METADATA);
        FIELDS.add(WebPage.Field.RETRIES_SINCE_FETCH);
        FIELDS.add(WebPage.Field.FETCH_TIME);
        FIELDS.add(WebPage.Field.MODIFIED_TIME);
        FIELDS.add(WebPage.Field.FETCH_INTERVAL);
        FIELDS.add(WebPage.Field.PREV_FETCH_TIME);
        FIELDS.add(WebPage.Field.PREV_MODIFIED_TIME);
        FIELDS.add(WebPage.Field.HEADERS);
    }

    public static final Utf8 DISTANCE = new Utf8("dist");

    public DbUpdaterJob() {
    }

    public DbUpdaterJob(Configuration conf){
        setConf(conf);
    }

    @Override
    public Map<String, Object> run(Map<String, Object> args) throws Exception {
        String crawlId = (String) args.get(Nutch.ARG_CARWL);
        String batchId = (String) args.get(Nutch.ARG_BATCH);
        numJobs = 1;
        currentJobNum = 0;

        if(batchId == null){
            batchId = Nutch.ALL_BATCH_ID_STR;
        }

        getConf().set(Nutch.BATCH_NAME_KEY,batchId);
        ScoringFilters scoringFilters = new ScoringFilters(getConf());
        HashSet<WebPage.Field> fields = new HashSet<>(FIELDS);
        //fields.addAll(scoringFilters.);
        return null;
    }

    @Override
    public int run(String[] strings) throws Exception {
        return 0;
    }
}
