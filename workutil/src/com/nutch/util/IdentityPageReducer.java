package com.nutch.util;

import com.nutch.storage.WebPage;
import org.apache.gora.mapreduce.GoraReducer;

import java.io.IOException;


public class IdentityPageReducer extends GoraReducer<String, WebPage, String, WebPage> {

    @Override
    protected void reduce(String key, Iterable<WebPage> values, Context context)
        throws IOException, InterruptedException {
        for (WebPage page : values) {
            context.write(key, page);
        }
    }

}
