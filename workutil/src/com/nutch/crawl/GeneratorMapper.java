package com.nutch.crawl;

import com.nutch.net.URLFilterException;
import com.nutch.net.URLFilters;
import com.nutch.net.URLNormalizers;
import com.nutch.scoring.ScoringFilterException;
import com.nutch.scoring.ScoringFilters;
import com.nutch.storage.Mark;
import com.nutch.storage.WebPage;
import com.nutch.util.TableUtil;
import org.apache.gora.mapreduce.GoraMapper;
import com.nutch.crawl.GeneratorJob.SelectorEntry;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import java.net.MalformedURLException;

public class GeneratorMapper extends GoraMapper<String, WebPage, SelectorEntry, WebPage> {
    private URLFilters filters;
    private URLNormalizers normalizers;
    private boolean filter;
    private boolean normalise;
    private FetchSchedule schedule;
    private ScoringFilters scoringFilters;
    private long curTime;
    private SelectorEntry entry = new SelectorEntry();
    private int maxDistance;

    @Override
    protected void map(String key, WebPage value, Context context) throws IOException, InterruptedException {
        String url = TableUtil.unreverseUrl(key);
        if (Mark.GENERATE_MARK.checkMark(value) != null) {
            GeneratorJob.LOG.debug("Skipping {}: already generated", url);
            return;
        }
        if (maxDistance > -1) {
            CharSequence distanceUtf8 = value.getMarkers().get(DbUpdaterJob.DISTANCE);
            if (distanceUtf8 != null) {
                int distance = Integer.parseInt(distanceUtf8.toString());
                if (distance > maxDistance) {
                    return;
                }
            }
        }
        try {
            if (normalise) {
                url = normalizers.normalize(url, URLNormalizers.SCOPE_GENERATE_HOST_COUNT);
            }
            if (filter && filters.filter(url) == null) {
                return;
            }
        } catch (URLFilterException e) {
            GeneratorJob.LOG.warn("Couldn't filter url: {} ({})", url, e.getMessage());
            return;
        }catch (MalformedURLException e) {
            GeneratorJob.LOG.warn("Couldn't filter url: {} ({})", url, e.getMessage());
            return;
        }

        if (!schedule.shouldFetch(url,value,curTime)) {
            if (GeneratorJob.LOG.isDebugEnabled()) {
                GeneratorJob.LOG.debug("-shouldFetch rejected '" + url + "', fetchTime=" + value.getFetchTime() +
                        ", curTime=" + curTime);
            }
            return;
        }
        float score = value.getScore();
        try {
            score = scoringFilters.generatorSortValue(url,value,score);
        } catch (ScoringFilterException e) {

        }
        entry.set(url, score);
        context.write(entry,value);
    }

    @Override
    protected void setup(Context context){
        Configuration conf = context.getConfiguration();
        filter = conf.getBoolean(GeneratorJob.GENERATOR_FILTER, true);
        normalise = conf.getBoolean(GeneratorJob.GENERATOR_NORMALISE,true);
        if (filter) {
            filters = new URLFilters(conf);
        }
        if (normalise) {
            normalizers = new URLNormalizers(conf,URLNormalizers.SCOPE_GENERATE_HOST_COUNT);
        }
        maxDistance = conf.getInt("generate.max.distance", -1);
        curTime = conf.getLong(GeneratorJob.GENERATOR_CUR_TIME, System.currentTimeMillis());
        schedule = FetchScheduleFactory.getFetchSchedule(conf);
        scoringFilters = new ScoringFilters(conf);

    }
}
