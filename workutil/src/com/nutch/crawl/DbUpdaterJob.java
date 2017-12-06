package com.nutch.crawl;

import com.nutch.metadata.Nutch;
import com.nutch.scoring.ScoringFilters;
import com.nutch.storage.Mark;
import com.nutch.storage.StorageUtils;
import com.nutch.storage.WebPage;
import com.nutch.util.NutchConfiguration;
import com.nutch.util.NutchTool;
import com.nutch.util.ToolUtil;
import org.apache.avro.util.Utf8;
import org.apache.gora.filter.FilterOp;
import org.apache.gora.filter.MapFieldValueFilter;
import org.apache.gora.util.TimingUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
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
        fields.addAll(scoringFilters.getFields());
        currentJob = NutchJob.getInstance(getConf(),"update-table");
        if (crawlId != null) {
            currentJob.getConfiguration().set(Nutch.CRAWL_ID_KEY,crawlId);
        }
        currentJob.setPartitionerClass(UrlWithScore.UrlOnlyPartitioner.class);
        currentJob.setSortComparatorClass(UrlWithScore.UrlScoreComparator.class);
        currentJob.setGroupingComparatorClass(UrlWithScore.UrlOnlyComparator.class);
        MapFieldValueFilter<String, WebPage> batchIdFilter = getBatchIdFilter(batchId);
        StorageUtils.initReducerJob(currentJob, DbUpdateReducer.class);
        currentJob.waitForCompletion(true);
        ToolUtil.recordJobStatus(null, currentJob, results);
        return null;
    }

    private MapFieldValueFilter<String, WebPage> getBatchIdFilter(String batchId) {
        if (batchId.equals(Nutch.ALL_CRAWL_ID.toString())) {
            return null;
        }
        MapFieldValueFilter<String,WebPage> filter = new MapFieldValueFilter<String, WebPage>();
        filter.setFieldName(WebPage.Field.MARKERS.toString());
        filter.setFilterOp(FilterOp.EQUALS);
        filter.setFilterIfMissing(true);
        filter.setMapKey(Mark.GENERATE_MARK.getName());
        filter.getOperands().add(new Utf8(batchId));
        return filter;
    }

    private int updateTable(String crawlId, String batchId) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long start = System.currentTimeMillis();
        log.info("DbUpdaterJob: starting at " + sdf.format(start));
        if (batchId.equals(Nutch.ALL_BATCH_ID_STR)) {
            log.info("DbUpdaterJob: updatinging all");
        } else {
            log.info("DbUpdaterJob: batchId: " + batchId);
        }
        run(ToolUtil.toArgMap(Nutch.ARG_CARWL,crawlId,Nutch.ARG_BATCH,batchId));
        long finish = System.currentTimeMillis();
        log.info("DbUpdaterJob: finished at " + sdf.format(finish) + ", time elapsed: " + TimingUtil.elapsedTime(start,finish));
        return 0;
    }

    @Override
    public int run(String[] args) throws Exception {
        String crawlId = null;
        String batchId;
        String usage = "Usage: DbUpdaterJob (<batchId> | -all) [-crawlId <id>] "
                + "    <batchId>     - crawl identifier returned by Generator, or -all for all \n \t \t    generated batchId-s\n"
                + "    -crawlId <id> - the id to prefix the schemas to operate on, \n \t \t    (default: storage.crawl.id)\n";
        if (args.length == 0) {
            System.err.println(usage);
            return -1;
        }
        batchId = args[0];
        if (!batchId.equals("-all") && batchId.startsWith("-")) {
            System.err.println(usage);
            return -1;
        }
        for (int i = 1; i < args.length; i++) {
            if ("-crawlId".equals(args[i])){
                getConf().set(Nutch.CRAWL_ID_KEY, args[++i]);
            } else {
                throw new IllegalArgumentException("arg " +  args[i] + "not recognized");
            }
        }
        return updateTable(crawlId,batchId);
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(NutchConfiguration.create(), new DbUpdaterJob(),args);
        System.exit(res);
    }
}
