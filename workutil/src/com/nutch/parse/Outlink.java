package com.nutch.parse;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.MalformedURLException;

public class Outlink implements Writable {
    private String toUrl;
    private String anchor;

    public Outlink() {}

    public Outlink(String toUrl, String anchor) throws MalformedURLException {
        this.toUrl = toUrl;
        if (anchor == null) {
            anchor = "";
        }
        this.anchor = anchor;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Text.writeString(dataOutput, toUrl);
        Text.writeString(dataOutput, anchor);
    }

    public static void skip(DataInput in) throws IOException {
        Text.skip(in);
        Text.skip(in);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        toUrl = Text.readString(dataInput);
        anchor = Text.readString(dataInput);
    }

    public static Outlink read(DataInput in) throws IOException {
        Outlink outlink = new Outlink();
        outlink.readFields(in);
        return outlink;
    }

    public String getToUrl() {
        return toUrl;
    }

    public String getAnchor() {
        return anchor;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Outlink)) {
            return false;
        }
        Outlink other = (Outlink) o;
        return this.toUrl.equals(other.toUrl) && this.anchor.equals(other.anchor);
    }

    @Override
    public String toString() {
        return "tourl: " + toUrl + " anchor: " + anchor;
    }
}
