package com.nutch.scoring;

import com.nutch.indexer.NutchDocument;
import com.nutch.plugin.ExtensionPoint;
import com.nutch.storage.WebPage;
import com.nutch.util.ObjectCache;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;

import java.util.Collection;
import java.util.List;

public class ScoringFilters extends Configured implements ScoringFilter{

    private ScoringFilter[] filters;

    public ScoringFilters(Configuration conf){
        super(conf);
        ObjectCache objectCache = ObjectCache.get(conf);
        String order = conf.get("scoring.filter.order");
        this.filters = (ScoringFilter[]) objectCache.getObject(ScoringFilter.class.getName());

        if(this.filters == null){
            String[] orderedFilters = null;
            if(order != null && !order.trim().equals("")){
                orderedFilters = order.split("\\s+");
            }
        }

        try{
            //TODO 代码暂定
            //ExtensionPoint
        }catch (Exception e){

        }
    }

    @Override
    public void injectedScore(String url, WebPage page) throws ScoringFilterException {

    }

    @Override
    public void initialScore(String url, WebPage page) throws ScoringFilterException {

    }

    @Override
    public float generatorSortValue(String url, WebPage page, float initSort) throws ScoringFilterException {
        return 0;
    }

    @Override
    public void distributeScoreToOutlinks(String fromUrl, WebPage page, Collection<ScoreDatum> scoreData, int allCount) throws ScoringFilterException {

    }

    @Override
    public void updateScore(String url, WebPage page, List<ScoreDatum> inLinkedScoreData) throws ScoringFilterException {

    }

    @Override
    public float indexerScore(String url, NutchDocument doc, WebPage page, float initScore) throws ScoringFilterException {
        return 0;
    }
}
