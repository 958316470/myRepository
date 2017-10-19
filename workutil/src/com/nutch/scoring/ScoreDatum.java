package com.nutch.scoring;

import com.nutch.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ScoreDatum implements Writable{

    private float score;
    private String url;
    private String anchor;
    private int distance;
    private Map<String,byte[]> metaData = new HashMap<String,byte[]>();

    public ScoreDatum() {
    }
    public ScoreDatum(float score,String url,String anchor,int depth) {
        this.score = score;
        this.url = url;
        this.anchor = anchor;
        this.distance = depth;
    }
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeFloat(score);
        Text.writeString(dataOutput,url);
        Text.writeString(dataOutput,anchor);
        WritableUtils.writeVInt(dataOutput,distance);
        WritableUtils.writeVInt(dataOutput,metaData.size());
        for(Entry<String,byte[]> e : metaData.entrySet()) {
            Text.writeString(dataOutput,e.getKey());
            Bytes.writeByteArray(dataOutput,e.getValue());
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        score = dataInput.readFloat();
        url = Text.readString(dataInput);
        anchor = Text.readString(dataInput);
        distance = WritableUtils.readVInt(dataInput);
        metaData.clear();

        int size = WritableUtils.readVInt(dataInput);
        for (int i = 0; i < size; i++) {
            String key = Text.readString(dataInput);
            byte[] value = Bytes.readByteArray(dataInput);
            metaData.put(key,null);
        }
    }

    @Override
    public String toString() {
        return "ScoreDatum{" +
                "score=" + score +
                ", url='" + url + '\'' +
                ", anchor='" + anchor + '\'' +
                ", distance=" + distance +
                ", metaData=" + metaData +
                '}';
    }
}
