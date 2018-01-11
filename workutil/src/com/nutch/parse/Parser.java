package com.nutch.parse;

import com.nutch.plugin.FieldPluggable;
import com.nutch.storage.WebPage;
import org.apache.hadoop.conf.Configurable;


public interface Parser extends FieldPluggable, Configurable{

    public final static String X_POINT_ID = Parser.class.getName();

    Parse getParse(String url, WebPage page);

}
