package com.nutch.crawl;

import com.nutch.metadata.Nutch;
import com.nutch.scoring.ScoreDatum;
import com.nutch.scoring.ScoringFilterException;
import com.nutch.scoring.ScoringFilters;
import com.nutch.storage.Mark;
import com.nutch.storage.WebPage;
import com.nutch.util.TableUtil;
import com.nutch.util.WebPageWritable;
import org.apache.avro.util.Utf8;
import org.apache.gora.mapreduce.GoraMapper;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbUpdateMapper extends GoraMapper<String, WebPage,
        UrlWithScore, NutchWritable>{
    public static final Logger LOG = DbUpdaterJob.log;
    private ScoringFilters scoringFilters;
    private final List<ScoreDatum> scoreData = new ArrayList<ScoreDatum>();
    private Utf8 batchId;
    private UrlWithScore urlWithScore = new UrlWithScore();
    private NutchWritable nutchWritable = new NutchWritable();
    private WebPageWritable pageWritable;

    @Override
    protected void map(String key, WebPage page, Context context) throws IOException, InterruptedException {
        if (Mark.GENERATE_MARK.checkMark(page) == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Skipping " + TableUtil.unreverseUrl(key) + "; not generated yet");
            }
            return;
        }
        String url = TableUtil.unreverseUrl(key);
        scoreData.clear();
        Map<CharSequence, CharSequence> outlinks = page.getOutlinks();
        if (outlinks != null) {
            for (Map.Entry<CharSequence, CharSequence> e : outlinks.entrySet()) {
                int depth = Integer.MAX_VALUE;
                CharSequence depthUtf8 = page.getMarkers().get(DbUpdaterJob.DISTANCE);
                if (depthUtf8 != null) {
                    depth = Integer.parseInt(depthUtf8.toString());
                    scoreData.add(new ScoreDatum(0.0f, e.getKey().toString(),e.getValue().toString(),depth));
                }
            }
        }
        try {
            scoringFilters.distributeScoreToOutlinks(url, page, scoreData, (outlinks == null ? 0 : outlinks.size()));
        } catch (ScoringFilterException e) {
            LOG.warn("Distributing score failed for URL: " + key + " exception:" + StringUtils.stringifyException(e));
        }
        urlWithScore.setUrl(key);
        urlWithScore.setScore(Float.MAX_VALUE);
        pageWritable.setWebPage(page);
        nutchWritable.set(pageWritable);
        context.write(urlWithScore, nutchWritable);
        for (ScoreDatum scoreDatum : scoreData) {
            String reversedOut = TableUtil.reverseUrl(scoreDatum.getUrl());
            scoreDatum.setUrl(url);
            urlWithScore.setUrl(reversedOut);
            urlWithScore.setScore(scoreDatum.getScore());
            nutchWritable.set(scoreDatum);
            context.write(urlWithScore, nutchWritable);
        }
    }

    @Override
    public void setup(Context context) {
        scoringFilters = new ScoringFilters(context.getConfiguration());
        pageWritable = new WebPageWritable(context.getConfiguration(), null);
        batchId = new Utf8(context.getConfiguration().get(Nutch.BATCH_NAME_KEY, Nutch.ALL_BATCH_ID_STR));
    }
}
