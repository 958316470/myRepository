package com.nutch.crawl;

import com.nutch.metadata.Nutch;
import com.nutch.storage.StorageUtils;
import com.nutch.storage.WebPage;
import com.nutch.util.NutchConfiguration;
import com.nutch.util.NutchTool;
import org.apache.gora.util.TimingUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GeneratorJob extends NutchTool implements Tool {

    public static final String GENERATE_UPDATE_CRAWLDB = "generate.update.crawldb";
    public static final String GENERATE_MIN_SCORE = "generate.min.score";
    public static final String GENERATOR_FILTER = "generate.filter";
    public static final String GENERATOR_NORMALISE = "generate.normalise";
    public static final String GENERATOR_MAX_COUNT = "generate.max.count";
    public static final String GENERATOR_COUNT_MODE = "generate.count.mode";
    public static final String GENERATOR_COUNT_VALUE_DOMAIN = "domain";
    public static final String GENERATOR_COUNT_VALUE_HOST = "host";
    public static final String GENERATOR_COUNT_VALUE_IP = "ip";
    public static final String GENERATOR_TOP_N = "generate.topN";
    public static final String GENERATOR_CUR_TIME = "generate.curTime";
    public static final String GENERATOR_DELAY = "crawl.gen.delay";
    public static final String GENERATOR_RANDOM_SEED = "generate.partition.seed";
    public static final String BATCH_ID = "generate.batch.id";
    public static final String GENERATE_COUNT = "generate.count";

    private static final Set<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();

    static {
        FIELDS.add(WebPage.Field.FETCH_TIME);
        FIELDS.add(WebPage.Field.SCORE);
        FIELDS.add(WebPage.Field.STATUS);
        FIELDS.add(WebPage.Field.MARKERS);
    }

    public static final Logger LOG = LoggerFactory.getLogger(GeneratorJob.class);

    public static class SelectorEntry implements WritableComparable<SelectorEntry> {
        String url;
        float score;

        public SelectorEntry() {

        }

        public SelectorEntry(String url, float score) {
            this.url = url;
            this.score = score;
        }

        @Override
        public int compareTo(SelectorEntry o) {
            if (o.score > score) {
                return 1;
            } else if (o.score == score) {
                return url.compareTo(o.url);
            }
            return -1;
        }

        @Override
        public void write(DataOutput dataOutput) throws IOException {
            Text.writeString(dataOutput, url);
            dataOutput.writeFloat(score);
        }

        @Override
        public void readFields(DataInput dataInput) throws IOException {
            url = Text.readString(dataInput);
            score = dataInput.readFloat();
        }


        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + url.hashCode();
            result = prime * result + Float.floatToRawIntBits(score);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            SelectorEntry other = (SelectorEntry) obj;
            if (!url.equals(other.url)) {
                return false;
            }
            if (Float.floatToRawIntBits(score) != Float.floatToRawIntBits(other.score)) {
                return false;
            }
            return true;
        }

        public void eet(String url, float score) {
            this.url = url;
            this.score = score;
        }
    }

    public static class SelectorEntryComparator extends WritableComparator {
        public SelectorEntryComparator() {
            super(SelectorEntry.class, true);
        }
    }

    static {
        WritableComparator.define(SelectorEntry.class, new SelectorEntryComparator());
    }

    public GeneratorJob() {
    }

    public GeneratorJob(Configuration conf) {
        setConf(conf);
    }

    public Collection<WebPage.Field> getFields(Job job) {
        Collection<WebPage.Field> fields = new HashSet<WebPage.Field>();
        fields.addAll(FetchScheduleFactory.getFetchSchedule(job.getConfiguration()).getFields());
        return fields;
    }

    public static String randomBathcId() {
        long curTime = System.currentTimeMillis();
        int randomSeed = Math.abs(new Random().nextInt());
        String batchId = (curTime / 1000) + "-" + randomSeed;
        return batchId;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> args) throws Exception {
        String batchId = (String) args.get(Nutch.ARG_BATCH);
        if (batchId == null) {
            batchId = randomBathcId();
        }
        getConf().set(BATCH_ID, batchId);
        Long topN = null;
        try {
            topN = (Long) args.get(Nutch.ARG_TOPN);
        } catch (Exception e) {
            topN = Long.parseLong(args.get(Nutch.ARG_TOPN).toString());
        }
        Long curTime = (Long) args.get(Nutch.ARG_CURTIME);
        if (curTime == null) {
            curTime = System.currentTimeMillis();
        }
        Boolean filter = (Boolean) args.get(Nutch.ARG_FILTER);
        Boolean norm = (Boolean) args.get(Nutch.ARG_NORMALIZE);
        getConf().setLong(GENERATOR_CUR_TIME, curTime);
        if (topN != null) {
            getConf().setLong(GENERATOR_TOP_N, topN);
        }
        if (filter != null) {
            getConf().setBoolean(GENERATOR_FILTER, filter);
        }
        getConf().setLong(Nutch.GENERATE_TIME_KEY, System.currentTimeMillis());
        if (norm != null) {
            getConf().setBoolean(GENERATOR_NORMALISE, norm);
        }
        String mode = getConf().get(GENERATOR_COUNT_MODE, GENERATOR_COUNT_VALUE_HOST);
        if (GENERATOR_COUNT_VALUE_HOST.equalsIgnoreCase(mode)) {
            getConf().set(URLPartitioner.PARTITION_MODE_KEY, URLPartitioner.PARTITION_MODE_HOST);
        } else if (GENERATOR_COUNT_VALUE_DOMAIN.equalsIgnoreCase(mode)) {
            getConf().set(URLPartitioner.PARTITION_MODE_KEY, URLPartitioner.PARTITION_MODE_DOMAIN);
        } else {
            LOG.warn("Unknown generator.max.count mode '" + mode + "' using mode=" + GENERATOR_COUNT_VALUE_HOST);
            getConf().set(GENERATOR_COUNT_MODE, GENERATOR_COUNT_VALUE_HOST);
            getConf().set(URLPartitioner.PARTITION_MODE_KEY, URLPartitioner.PARTITION_MODE_HOST);
        }
        numJobs = 1;
        currentJobNum = 0;
        currentJob = NutchJob.getInstance(getConf(), "generate: " + getConf().get(BATCH_ID));
        Collection<WebPage.Field> fields = getFields(currentJob);
        StorageUtils.initMapperJob(currentJob, fields, SelectorEntry.class, WebPage.class, GeneratorMapper.class, URLPartitioner.SelectorEntryPartitioner.class, true);
        StorageUtils.initReducerJob(currentJob, GeneratorReducer.class);
        currentJob.waitForCompletion(true);
        ToolUtil.recordJobStatus(null, currentJob, results);
        results.put(BATCH_ID, getConf().get(BATCH_ID));
        long generateCount = currentJob.getCounters().findCounter("Generator", "GENERATE_MARK").getValue();
        results.put(GENERATE_COUNT, generateCount);
        return results;
    }

    public String generate(long topN, long curTime, boolean filter, boolean norm) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long start = System.currentTimeMillis();
        LOG.info("GeneratorJob: starting at " + sdf.format(start));
        LOG.info("GeneratorJob: Selecting best-scoring urls due for fetch.");
        LOG.info("GeneratorJob: starting");
        LOG.info("GeneratorJob: filtering: " + filter);
        LOG.info("GeneratorJob: normalizing: " + norm);
        if (topN != Long.MAX_VALUE) {
            LOG.info("GeneratorJob: topN: " + topN);
        }
        String batchId = getConf().get(BATCH_ID);
        Map<String, Object> results = run(ToolUtil.toArgMap(Nutch.ARG_TOPN, topN, Nutch.ARG_CURTIME, curTime, Nutch.ARG_FILTER, filter,
                Nutch.ARG_NORMALIZE, norm, Nutch.ARG_BATCH, batchId));
        if (batchId == null) {
            batchId = (String) results.get(BATCH_ID);
        }
        long finish = System.currentTimeMillis();
        long generateCount = (Long) results.get(GENERATE_COUNT);
        LOG.info("GeneratorJob: finished at " + sdf.format(finish) + ", time elapsed: " + TimingUtil.elapsedTime(start, finish));
        LOG.info("GeneratorJob: generated batch id: " + batchId + " containing " + generateCount + " URLs");
        if (generateCount == 0) {
            return null;
        }
        return batchId;
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length <= 0) {
            System.out
                    .println("Usage: GeneratorJob [-topN N] [-crawlId id] [-noFilter] [-noNorm] [-adddays numDays]");
            System.out
                    .println("    -topN <N>      - number of top URLs to be selected, default is Long.MAX_VALUE ");
            System.out
                    .println("    -crawlId <id>  - the id to prefix the schemas to operate on, \n \t \t    (default: storage.crawl.id)\");");
            System.out
                    .println("    -noFilter      - do not activate the filter plugin to filter the url, default is true ");
            System.out
                    .println("    -noNorm        - do not activate the normalizer plugin to normalize the url, default is true ");
            System.out
                    .println("    -adddays       - Adds numDays to the current time to facilitate crawling urls already");
            System.out
                    .println("                     fetched sooner then db.fetch.interval.default. Default value is 0.");
            System.out.println("    -batchId       - the batch id ");
            System.out.println("----------------------");
            System.out.println("Please set the params.");
            return -1;
        }
        long curTime = System.currentTimeMillis();
        long topN = Long.MAX_VALUE;
        boolean filter = true;
        boolean norm = true;
        for (int i = 0; i < args.length; i++) {
            if ("-topN".equals(args[i])) {
                topN = Long.parseLong(args[++i]);
            } else if ("-noFilter".equals(args[i])) {
                filter = false;
            } else if ("-noNorm".equals(args[i])) {
                norm = false;
            } else if ("-crawlId".equals(args[i])) {
                getConf().set(Nutch.CRAWL_ID_KEY, args[++i]);
            } else if ("-addays".equals(args[i])) {
                long numDays = Integer.parseInt(args[++i]);
                curTime += numDays * 1000L * 60 * 60 * 24;
            } else if ("-batchId".equals(args[i])) {
                getConf().set(BATCH_ID, args[++i]);
            } else {
                System.err.println("Unrecognized arg " + args[i]);
                return -1;
            }
        }
        try {
            return (generate(topN, curTime, filter, norm) != null) ? 0 : 1;
        } catch (Exception e) {
            LOG.error("GeneratorJob: " + StringUtils.stringifyException(e));
            return -1;
        }
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(NutchConfiguration.create(),new GeneratorJob(),args);
        System.exit(res);
    }
}
