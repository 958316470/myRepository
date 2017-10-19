package com.nutch.util;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.GenericWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.IOException;

public abstract class GenericWritableConfigurable extends GenericWritable implements Configurable {

    private Configuration conf;

    @Override
    public Configuration getConf() {
        return conf;
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }



    @Override
    public void readFields(DataInput in) throws IOException {
        byte type = in.readByte();
        Class<?> clazz = getTypes()[type];
        try {
            set((Writable)clazz.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Cannot initialize the class: " + clazz);
        }
        Writable w = get();
        if (w instanceof Configurable){
            ((Configurable)w).setConf(conf);
        }
        w.readFields(in);
    }
}
