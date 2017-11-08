package com.nutch.crawl;

import com.nutch.fetcher.FetchEntry;
import com.nutch.metadata.Nutch;
import com.nutch.storage.Mark;
import com.nutch.storage.StorageUtils;
import com.nutch.storage.WebPage;
import com.nutch.util.NutchConfiguration;
import com.nutch.util.NutchTool;
import com.nutch.util.TableUtil;
import org.apache.avro.util.Utf8;
import org.apache.gora.filter.FilterOp;
import org.apache.gora.filter.MapFieldValueFilter;
import org.apache.gora.mapreduce.GoraMapper;
import org.apache.gora.util.TimingUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class FetcherJob extends NutchTool implements Tool {

    public static final String PROTOCOL_REDIR = "protocol";
    public static final int PERM_REFRESH_TIME = 5;
    public static final Utf8 REDIRECT_DISCOVERED = new Utf8("___rdrdsc__");
    public static final String RESUME_KEY = "fetcher.job.resume";
    public static final String PARSE_KEY = "fetcher.parse";
    public static final String THREADS_KEY = "fetcher.threads.fetch";

    private static final Collection<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();

    static {
        FIELDS.add(WebPage.Field.MARKERS);
        FIELDS.add(WebPage.Field.REPR_URL);
        FIELDS.add(WebPage.Field.FETCH_TIME);
    }

    public static class FetcherMapper extends GoraMapper<String, WebPage, IntWritable, FetchEntry> {
        private boolean shouldContinue;
        private Utf8 batchId;
        private Random random = new Random();

        @Override
        protected void setup(Context context) {
            Configuration conf = context.getConfiguration();
            shouldContinue = conf.getBoolean(RESUME_KEY, false);
            batchId = new Utf8(conf.get(GeneratorJob.BATCH_ID, Nutch.ALL_BATCH_ID_STR));
        }

        @Override
        protected void map(String key, WebPage page, Context context) throws IOException,InterruptedException{
            if (Mark.GENERATE_MARK.checkMark(page) == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Skipping " + TableUtil.unreverseUrl(key) + "; not generated yet");
                }
                return;
            }
            if (shouldContinue && Mark.FETCH_MARK.checkMark(page) != null) {
                if(LOG.isDebugEnabled()) {
                    LOG.debug("Skipping " + TableUtil.unreverseUrl(key) + "; already fetched");
                }
                return;
            }
            context.write(new IntWritable(random.nextInt(65536)),new FetchEntry(context.getConfiguration(),key,page));
        }
    }
    public static final Logger LOG = LoggerFactory.getLogger(FetcherJob.class);

    public FetcherJob(){}
    public FetcherJob(Configuration conf) {
        setConf(conf);
    }

    public Collection<WebPage.Field> getFields(Job job) {
        Collection<WebPage.Field> fields = new HashSet<WebPage.Field>(FIELDS);
        if (job.getConfiguration().getBoolean(PARSE_KEY,false)) {
            ParserJob parserJob = new ParserJob();
            fields.addAll(parserJob.getFields(job));
        }
        ProtocolFactory protocolFactory = new ProtocolFactoory(job.getConfiguration());
        fields.addAll(protocolFactory.getFields());
        return fields;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> args) throws Exception {
        checkConfiguration();
        String batchId = (String) args.get(Nutch.ARG_BATCH);
        Integer threads = (Integer) args.get(Nutch.ARG_THREADS);
        Boolean shouldResume = (Boolean) args.get(Nutch.ARG_RESUME);
        Integer numTasks = (Integer) args.get(Nutch.ARG_NUMTASKS);
        if (threads != null && threads > 0) {
            getConf().setInt(THREADS_KEY, threads);
        }
        if (batchId == null) {
            batchId = Nutch.ALL_BATCH_ID_STR;
        }
        getConf().set(GeneratorJob.BATCH_ID, batchId);
        if (shouldResume != null) {
            getConf().setBoolean(RESUME_KEY, shouldResume);
        }
        LOG.info("FetcherJob: threads: " + getConf().getInt(THREADS_KEY, 10));
        LOG.info("FetcherJob: parsing: " + getConf().getBoolean(THREADS_KEY, false));
        LOG.info("FetcherJob: resuming: " + getConf().getBoolean(RESUME_KEY, false));
        long timeLimit = getConf().getLong("fetcher.timelimit.mins", -1);
        if (timeLimit != -1) {
            timeLimit = System.currentTimeMillis() + (timeLimit * 60 * 1000);
            getConf().setLong("fetcher.timelimit",timeLimit);
        }
        LOG.info("FetcherJob : timelimit set for : " + getConf().getLong("fetcher.timelimit",-1));
        numJobs = 1;
        currentJob = NutchJob.getInstance(getConf(),"fetch");
        currentJob.setReduceSpeculativeExecution(false);
        Collection<WebPage.Field> fields = getFields(currentJob);
        MapFieldValueFilter<String, WebPage> batchIdFilter = getBatchIdFilter(batchId);
        StorageUtils.initMapperJob(currentJob, fields, IntWritable.class, FetchEntry.class, FetcherMapper.class, FetchEntryPartitioner.class, batchIdFilter, false);
        StorageUtils.initMapperJob(currentJob,FetcherReducer.class);
        if (numTasks == null || numTasks < 1) {
            currentJob.setNumReduceTasks(currentJob.getConfiguration().getInt("mapred.map.tasks",currentJob.getNumReduceTasks()));
        } else {
            currentJob.setNumReduceTasks(numTasks);
        }
        currentJob.waitForCompletion(true);
        ToolUtil.recordJobStatus(null, currentJob, results);
        return results;
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

    public int fetch(String batchId, int threads, boolean shouldResume, int numTasks) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long start = System.currentTimeMillis();
        LOG.info("FetcherJob: starting at " + sdf.format(start));
        if (batchId.equals(Nutch.ALL_BATCH_ID_STR)) {
            LOG.info("FetcherJob: fetching all");
        } else {
            LOG.info("FetcherJob: batchId: " + batchId);
        }
        run(ToolUtil.toArgMap(Nutch.ARG_BATCH, batchId, Nutch.ARG_THREADS, threads, Nutch.ARG_RESUME, shouldResume, Nutch.ARG_NUMTASKS, numTasks));
        long finish = System.currentTimeMillis();
        LOG.info("FetcherJob: finished at " + sdf.format(finish) + ", time elapsed: " + TimingUtil.elapsedTime(start, finish));
        return 0;
    }

    void checkConfiguration() {
        String agentName = getConf().get("http.agent.name");
        if (agentName == null || agentName.trim().length() == 0) {
            String message = "Fetcher: No agents listed in 'http.agent.name'" + " property.";
            if (LOG.isErrorEnabled()) {
                LOG.error(message);
            }
            throw new IllegalArgumentException(message);
        }
    }



    @Override
    public int run(String[] args) throws Exception {
        int threads = -1;
        boolean shouldResume = false;
        String batchId;
        String usage = "Usage: FetcherJob (<batchId> | -all) [-crawlId <id>] "
                + "[-threads N] \n \t \t  [-resume] [-numTasks N]\n"
                + "    <batchId>     - crawl identifier returned by Generator, or -all for all \n \t \t    generated batchId-s\n"
                + "    -crawlId <id> - the id to prefix the schemas to operate on, \n \t \t    (default: storage.crawl.id)\n"
                + "    -threads N    - number of fetching threads per task\n"
                + "    -resume       - resume interrupted job\n"
                + "    -numTasks N   - if N > 0 then use this many reduce tasks for fetching \n \t \t    (default: mapred.map.tasks)";
        if (args.length == 0) {
            System.err.println(usage);
            return -1;
        }
        batchId = args[0];
        if (!batchId.equals("-all") && batchId.startsWith("-")) {
            System.err.println(usage);
            return -1;
        }
        int numTasks = -1;
        for (int i = 1; i < args.length; i++) {
            if ("-threads".equals(args[i])) {
                threads = Integer.parseInt(args[++i]);
            } else if ("-resume".equals(args[i])) {
                shouldResume = true;
            } else if ("-crawlId".equals(args[i])) {
                getConf().set(Nutch.CRAWL_ID_KEY, args[++i]);
            } else {
                throw new IllegalArgumentException("arg " + args[i] + "not recognized");
            }
        }
        int fetchCode = fetch(batchId, threads, shouldResume, numTasks);
        return fetchCode;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(NutchConfiguration.create(), new FetcherJob(),args);
        System.exit(res);
    }
}
