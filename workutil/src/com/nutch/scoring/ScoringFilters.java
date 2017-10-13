package com.nutch.scoring;

import com.nutch.indexer.NutchDocument;
import com.nutch.plugin.Extension;
import com.nutch.plugin.ExtensionPoint;
import com.nutch.plugin.PluginRepository;
import com.nutch.plugin.PluginRuntimeException;
import com.nutch.storage.WebPage;
import com.nutch.util.ObjectCache;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;

import java.util.*;

public class ScoringFilters extends Configured implements ScoringFilter{

    private ScoringFilter[] filters;

    public ScoringFilters(Configuration conf) {
        super(conf);
        ObjectCache objectCache = ObjectCache.get(conf);
        String order = conf.get("scoring.filter.order");
        this.filters = (ScoringFilter[]) objectCache.getObject(ScoringFilter.class.getName());

        if (this.filters == null) {
            String[] orderedFilters = null;
            if (order != null && !order.trim().equals("")) {
                orderedFilters = order.split("\\s+");
            }
            try {
                ExtensionPoint point = PluginRepository.get(conf).getExtensionPoint(ScoringFilter.X_POINT_ID);
                if (point == null) {
                    throw new RuntimeException(ScoringFilter.X_POINT_ID + "not found.");
                }
                Extension[] extensions = point.getExtensions();
                HashMap<String, ScoringFilter> filterMap = new HashMap<String, ScoringFilter>();
                for (int i = 0; i < extensions.length; i++) {
                    Extension extension = extensions[i];
                    ScoringFilter filter = (ScoringFilter) extension.getExtensionInstance();
                    if (!filterMap.containsKey(filter.getClass().getName())) {
                        filterMap.put(filter.getClass().getName(), filter);
                    }
                }
                if (orderedFilters == null) {
                    objectCache.setObject(ScoringFilter.class.getName(),filterMap.values().toArray(new ScoringFilters[0]));
                } else {
                    ScoringFilter[] filter = new ScoringFilter[orderedFilters.length];
                    for (int i = 0; i < orderedFilters.length; i++) {
                        filter[i] = filterMap.get(orderedFilters[i]);
                    }
                    objectCache.setObject(ScoringFilter.class.getName(),filter);
                }
            } catch (PluginRuntimeException e) {
                throw new RuntimeException(e);
            }
            this.filters = (ScoringFilter[]) objectCache.getObject(ScoringFilter.class.getName());
        }
    }

    @Override
    public void injectedScore(String url, WebPage page) throws ScoringFilterException {
        for (ScoringFilter filter : filters) {
            filter.injectedScore(url,page);
        }
    }

    @Override
    public void initialScore(String url, WebPage page) throws ScoringFilterException {
        for (ScoringFilter filter : filters) {
            filter.initialScore(url,page);
        }
    }

    @Override
    public float generatorSortValue(String url, WebPage page, float initSort) throws ScoringFilterException {
        for (ScoringFilter filter : filters) {
            initSort = filter.generatorSortValue(url,page,initSort);
        }
        return initSort;
    }

    @Override
    public void distributeScoreToOutlinks(String fromUrl, WebPage page, Collection<ScoreDatum> scoreData, int allCount) throws ScoringFilterException {
        for(ScoringFilter filter : filters) {
            filter.distributeScoreToOutlinks(fromUrl, page, scoreData, allCount);
        }
    }

    @Override
    public void updateScore(String url, WebPage page, List<ScoreDatum> inLinkedScoreData) throws ScoringFilterException {
        for(ScoringFilter filter : filters) {
            filter.updateScore(url, page, inLinkedScoreData);
        }
    }

    @Override
    public float indexerScore(String url, NutchDocument doc, WebPage page, float initScore) throws ScoringFilterException {
        for (ScoringFilter filter : filters) {
            initScore = filter.indexerScore(url, doc, page, initScore);
        }
        return initScore;
    }

    @Override
    public Collection<WebPage.Field> getFields() {
        Set<WebPage.Field> fields = new HashSet<WebPage.Field>();
        for (ScoringFilter filter : filters) {
            Collection<WebPage.Field> pluginFields = filter.getFields();
            if (pluginFields != null) {
                fields.addAll(pluginFields);
            }
        }
        return fields;
    }
}
