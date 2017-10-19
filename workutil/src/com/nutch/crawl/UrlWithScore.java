package com.nutch.crawl;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Partitioner;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Comparator;

public class UrlWithScore implements WritableComparable<UrlWithScore> {
    private static final Comparator<UrlWithScore> comp = new UrlScoreComparator();

    private Text url;
    private FloatWritable score;

    public UrlWithScore() {
        url = new Text();
        score = new FloatWritable();
    }

    public UrlWithScore(Text url,FloatWritable score) {
        this.url = url;
        this.score = score;
    }

    public UrlWithScore(String url,float score) {
        this.url = new Text(url);
        this.score = new FloatWritable(score);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        url.write(dataOutput);
        score.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        url.readFields(dataInput);
        score.readFields(dataInput);
    }

    public Text getUrl() {
        return url;
    }

    public void setUrl(Text url) {
        this.url = url;
    }

    public FloatWritable getScore() {
        return score;
    }

    public void setScore(FloatWritable score) {
        this.score = score;
    }
    public void setScore(float score) {
        this.score.set(score);
    }
    public void setUrl(String url){
        this.url.set(url);
    }

    @Override
    public int compareTo(UrlWithScore other) {
        return comp.compare(this,other);
    }

    @Override
    public String toString() {
        return "UrlWithScore{" +
                "url=" + url +
                ", score=" + score +
                '}';
    }


    public static final class UrlOnlyComparator implements RawComparator<UrlWithScore> {

        private final WritableComparator textComp = new Text.Comparator();

        @Override
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            try {
                int deptLen1 = WritableUtils.decodeVIntSize(b1[s1]) + WritableComparator.readVInt(b1,s1);
                int deptLen2 = WritableUtils.decodeVIntSize(b2[s2]) + WritableComparator.readVInt(b2,s2);
                return textComp.compare(b1, s1, deptLen1, b2, s2, deptLen2);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public int compare(UrlWithScore o1, UrlWithScore o2) {
            return o1.getUrl().compareTo(o2.getUrl());
        }
    }

    public static class UrlOnlyPartitioner extends Partitioner<UrlWithScore,NutchWritable> {
        @Override
        public int getPartition(UrlWithScore key, NutchWritable val, int reduces) {
            return (key.url.hashCode() & Integer.MAX_VALUE) % reduces;
        }
    }

    public static final class UrlScoreComparator implements RawComparator<UrlWithScore> {
        private final WritableComparator textComp = new Text.Comparator();
        private final WritableComparator floatComp = new FloatWritable.Comparator();
        @Override
        public int compare(byte[] bytes, int i, int i1, byte[] bytes1, int i2, int i3) {
            try {
                int deptLen1 = WritableUtils.decodeVIntSize(bytes[i]) + WritableComparator.readVInt(bytes,i);
                int deptLen2 = WritableUtils.decodeVIntSize(bytes1[i2]) + WritableComparator.readVInt(bytes1,i2);
                int cmp = textComp.compare(bytes1,i,deptLen1,bytes1,i2,deptLen2);
                if(cmp != 0) {
                    return cmp;
                }
                return -floatComp.compare(bytes, i + deptLen1, i1 - deptLen1, bytes1, i2 + deptLen2, i3 - deptLen2);
            } catch (IOException e){
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public int compare(UrlWithScore o1, UrlWithScore o2) {
            int cmp = o1.getUrl().compareTo(o2.getUrl());
            if(cmp != 0) {
                return cmp;
            }
            return -o1.getScore().compareTo(o2.getScore());
        }
    }
}
