package com.nutch.fetcher;

import com.nutch.storage.WebPage;
import org.apache.gora.util.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FetchEntry extends Configured implements Writable {

    private String key;
    private WebPage page;

    public FetchEntry() {
        super(null);
    }

    public FetchEntry(Configuration conf, String key, WebPage page) {
        super(conf);
        this.key = key;
        this.page = page;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Text.writeString(dataOutput, key);
        IOUtils.serialize(getConf(), dataOutput, page, WebPage.class);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        key = Text.readString(dataInput);
        page = IOUtils.deserialize(getConf(), dataInput, null, WebPage.class);
    }

    public String getKey() {
        return key;
    }

    public WebPage getWebPage() {
        return page;
    }

    @Override
    public String toString() {
        return "FetchEntry{" +
                "key='" + key + '\'' +
                ", page=" + page +
                '}';
    }
}
