package com.nutch.crawl;

import com.nutch.storage.Mark;
import com.nutch.storage.WebPage;
import com.nutch.util.TableUtil;
import com.nutch.util.URLUtil;
import org.apache.avro.util.Utf8;
import org.apache.gora.mapreduce.GoraReducer;
import com.nutch.crawl.GeneratorJob.SelectorEntry;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class GeneratorReducer extends GoraReducer<SelectorEntry, WebPage, String, WebPage> {
    private long limit;
    private long maxCount;
    protected static long count = 0;
    private boolean byDomain = false;
    private Map<String, Integer> hostCountMap = new HashMap<String, Integer>();
    private Utf8 batchId;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        long totalLimit = conf.getLong(GeneratorJob.GENERATOR_TOP_N, Long.MAX_VALUE);
        if (totalLimit == Long.MAX_VALUE) {
            limit = Long.MAX_VALUE;
        } else {
            limit = totalLimit / context.getNumReduceTasks();
        }
        maxCount = conf.getLong(GeneratorJob.GENERATOR_MAX_COUNT, -2);
        batchId = new Utf8(conf.get(GeneratorJob.BATCH_ID));
        String countMode = conf.get(GeneratorJob.GENERATOR_COUNT_MODE,GeneratorJob.GENERATOR_COUNT_VALUE_HOST);
        if (countMode.equals(GeneratorJob.GENERATOR_COUNT_VALUE_DOMAIN)) {
            byDomain = true;
        }
    }

    @Override
    protected void reduce(SelectorEntry key, Iterable<WebPage> values, Context context) throws IOException, InterruptedException {
        for (WebPage page : values) {
            if (count >= limit) {
                return;
            }
            if (maxCount > 0) {
                String hostordomain;
                if (byDomain) {
                    hostordomain = URLUtil.getDomainName(key.url);
                } else {
                    hostordomain = URLUtil.getHost(key.url);
                }
                Integer hostCount = hostCountMap.get(hostordomain);
                if (hostCount == null) {
                    hostCountMap.put(hostordomain, 0);
                    hostCount = 0;
                }
                if (hostCount >= maxCount) {
                    return;
                }
                hostCountMap.put(hostordomain, hostCount + 1);
            }
            Mark.GENERATE_MARK.putMark(page, batchId);
            page.setBatchId(batchId);
            try {
                context.write(TableUtil.reverseUrl(key.url), page);
            } catch (MalformedURLException e) {
                context.getCounter("Generator", "MALFORMED_URL").increment(1);
                continue;
            }
            context.getCounter("Generator", "GENERATE_MARK").increment(1);
            count++;
        }
    }
}
