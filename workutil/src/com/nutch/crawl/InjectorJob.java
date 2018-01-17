package com.nutch.crawl;

import com.nutch.metadata.Nutch;
import com.nutch.net.URLFilters;
import com.nutch.net.URLNormalizers;
import com.nutch.scoring.ScoringFilterException;
import com.nutch.scoring.ScoringFilters;
import com.nutch.storage.Mark;
import com.nutch.storage.StorageUtils;
import com.nutch.storage.WebPage;
import com.nutch.util.*;
import org.apache.avro.util.Utf8;
import org.apache.gora.mapreduce.GoraOutputFormat;
import org.apache.gora.persistency.Persistent;
import org.apache.gora.store.DataStore;
import org.apache.gora.util.TimingUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;

public class InjectorJob extends NutchTool implements Tool{

    public static final Logger LOG = LoggerFactory.getLogger(InjectorJob.class);
    private static final Set<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();
    private static final Utf8 YES_STRING = new Utf8("y");

    static {
        FIELDS.add(WebPage.Field.MARKERS);
        FIELDS.add(WebPage.Field.STATUS);
    }

    public static String nutchScoreMDName = "nutch.score";
    public static String nutchFetchIntervalMDName = "nutch.fetchInterval";

    public static class UrlMapper extends Mapper<LongWritable, Text, String, WebPage> {
        private URLNormalizers urlNormalizers;
        private int interval;
        private float scoreInjected;
        private URLFilters filters;
        private ScoringFilters scfilters;
        private long curTime;

        @Override
        protected void setup(Context context) throws IOException,InterruptedException {
            urlNormalizers = new URLNormalizers(context.getConfiguration(), URLNormalizers.SCOPE_INJECT);
            interval = context.getConfiguration().getInt("db.fetch.interval.default", 2592000);
            filters = new URLFilters(context.getConfiguration());
            scfilters = new ScoringFilters(context.getConfiguration());
            scoreInjected = context.getConfiguration().getFloat("db.score.injected", 1.0f);
            curTime = context.getConfiguration().getLong("injector.current.time", System.currentTimeMillis());
        }
        @Override
        protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException{
            String url = value.toString().trim();
            if (url != null && (url.length() == 0 || url.startsWith("#"))) {
                return;
            }
            float customScore = -1f;
            int customInterval = interval;
            Map<String, String> metadata = new TreeMap<String, String>();
            if (url.indexOf("\t") != -1) {
                String[] splits = url.split("\t");
                url = splits[0];
                for (int s = 1; s < splits.length; s++) {
                    int indexEquals = splits[s].indexOf("=");
                    if (indexEquals == -1) {
                        continue;
                    }
                    String metaName = splits[s].substring(0, indexEquals);
                    String metaValue = splits[s].substring(indexEquals + 1);
                    if (metaName.equals(nutchScoreMDName)) {
                        try {
                            customScore = Float.parseFloat(metaValue);
                        } catch (NumberFormatException e){

                        }
                    }else if (metaName.equals(nutchFetchIntervalMDName)) {
                        try {
                            customScore = Integer.parseInt(metaValue);
                        } catch (NumberFormatException e){
                        }
                    }else {
                        metadata.put(metaName, metaValue);
                    }
                }
            }
                try {
                    url = urlNormalizers.normalize(url, URLNormalizers.SCOPE_INJECT);
                    url = filters.filter(url);
                } catch (Exception e) {
                    LOG.warn("Skipping " + url + ":" + e);
                    url = null;
                }
                if (url == null) {
                    context.getCounter("injector", "urls_filtered").increment(1);
                    return;
                } else {
                    String reversedUrl = TableUtil.reverseUrl(url);
                    WebPage row = WebPage.newBuilder().build();
                    row.setFetchTime(curTime);
                    row.setFetchInterval(customInterval);
                    Iterator<String> keysIter = metadata.keySet().iterator();
                    while (keysIter.hasNext()) {
                        String keymd = keysIter.next();
                        String valuemd = metadata.get(keymd);
                        row.getMetadata().put(new Utf8(keymd), ByteBuffer.wrap(valuemd.getBytes()));
                    }
                    if (customScore != -1) {
                        row.setScore(customScore);
                    } else {
                        row.setScore(scoreInjected);
                    }
                    try {
                        scfilters.injectedScore(url, row);
                    } catch (ScoringFilterException e) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("Cannot filter injected score for url " + url + ", using default (" + e.getMessage() + ")");
                        }
                    }
                    context.getCounter("injector", "urls_injected").increment(1);
                    row.getMarkers().put(DbUpdaterJob.DISTANCE, new Utf8(String.valueOf(0)));
                    Mark.INJECT_MARK.putMark(row, YES_STRING);
                    context.write(reversedUrl, row);
                }
            }
        }

    public InjectorJob() {}

    public InjectorJob(Configuration conf) {
        setConf(conf);
    }


    @Override
    public Map<String, Object> run(Map<String, Object> args) throws Exception {
        getConf().setLong("injector.current.time", System.currentTimeMillis());
        Path input;
        Object path = args.get(Nutch.ARG_SEEDDIR);
        if (path instanceof Path) {
            input = (Path) path;
        } else {
            input = new Path(path.toString());
        }
        numJobs = 1;
        currentJobNum = 0;
        currentJob = NutchJob.getInstance(getConf(), "inject" + input);
        FileInputFormat.addInputPath(currentJob, input);
        currentJob.setMapperClass(UrlMapper.class);
        currentJob.setMapOutputKeyClass(String.class);
        currentJob.setMapOutputValueClass(WebPage.class);
        currentJob.setOutputFormatClass(GoraOutputFormat.class);
        DataStore<String, WebPage> store = StorageUtils.createWebStore(currentJob.getConfiguration(), String.class, WebPage.class);
        GoraOutputFormat.setOutput(currentJob, store, true);
        Class<? extends DataStore<Object, Persistent>> dataStoreClass = StorageUtils.getDataStoreClass(currentJob.getConfiguration());
        LOG.info("InjectorJob: Using " + dataStoreClass + " as the Gora storage class");
        currentJob.setReducerClass(Reducer.class);
        currentJob.setNumReduceTasks(0);
        currentJob.waitForCompletion(true);
        ToolUtil.recordJobStatus(null, currentJob, results);
        long urlsInjected = currentJob.getCounters().findCounter("injector", "urls_injected").getValue();
        long urlsFiltered = currentJob.getCounters().findCounter("injector", "urls_filtered").getValue();
        LOG.info("InjectorJob: total number of urls rejected by filters: " + urlsFiltered);
        LOG.info("InjectorJob: total nunber ofr urls injected after normalization and filtering: " + urlsInjected);
        return results;
    }

    public void inject(Path urlDir) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long start = System.currentTimeMillis();
        LOG.info("InjectorJob: starting at " + sdf.format(start));
        LOG.info("InjectorJob: Injecting urlDir: " + urlDir);
        run(ToolUtil.toArgMap(Nutch.ARG_SEEDDIR, urlDir));
        long end = System.currentTimeMillis();
        LOG.info("Injector: finished at " + sdf.format(end) + ", elapsed: " + TimingUtil.elapsedTime(start,end));
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: InjectorJob <url_dir> [-crawlId <id>]");
            return -1;
        }
        for (int i = 1; i < args.length; i++) {
            if ("-crawlId".equals(args[i])) {
                getConf().set(Nutch.CRAWL_ID_KEY, args[i + 1]);
                i++;
            } else {
                System.err.println("Unrecognized arg " + args[i]);
                return -1;
            }
        }
        try {
            inject(new Path(args[0]));
            return -0;
        } catch (Exception e) {
            LOG.error("InjectorJob: " + StringUtils.stringifyException(e));
            return -1;
        }
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(NutchConfiguration.create(), new InjectorJob(), args);
        System.exit(res);
    }
}
