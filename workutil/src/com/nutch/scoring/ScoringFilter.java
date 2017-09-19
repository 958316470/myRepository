package com.nutch.scoring;

import com.nutch.plugin.FieldPluggable;
import com.nutch.indexer.NutchDocument;
import com.nutch.storage.WebPage;
import org.apache.hadoop.conf.Configurable;

import java.util.Collection;
import java.util.List;

public interface ScoringFilter extends Configurable, FieldPluggable {

    public final static String X_POINT_ID = ScoringFilter.class.getName();

    public void injectedScore(String url, WebPage page) throws ScoringFilterException;

    public void initialScore(String url, WebPage page) throws ScoringFilterException;

    public float generatorSortValue(String url, WebPage page, float initSort) throws ScoringFilterException;

    public void distributeScoreToOutlinks(String fromUrl, WebPage page, Collection<ScoreDatum> scoreData, int allCount) throws ScoringFilterException;

    public void updateScore(String url, WebPage page, List<ScoreDatum> inLinkedScoreData) throws ScoringFilterException;

    public float indexerScore(String url, NutchDocument doc, WebPage page, float initScore) throws ScoringFilterException;


}
