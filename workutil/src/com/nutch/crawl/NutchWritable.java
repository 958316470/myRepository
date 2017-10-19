package com.nutch.crawl;

import com.nutch.util.GenericWritableConfigurable;
import org.apache.hadoop.io.Writable;

public class NutchWritable extends GenericWritableConfigurable {

    private static Class<? extends Writable>[] CLASSES = null;

    static {
        CLASSES = (Class<? extends Writable>[]) new Class<?>[] {

        };
    }

    @Override
    protected Class<? extends Writable>[] getTypes() {
        return new Class[0];
    }
}
