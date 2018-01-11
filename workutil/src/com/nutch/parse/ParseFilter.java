package com.nutch.parse;

import com.nutch.plugin.FieldPluggable;
import com.nutch.storage.WebPage;
import org.apache.hadoop.conf.Configurable;
import org.w3c.dom.DocumentFragment;

public interface ParseFilter extends FieldPluggable,Configurable {
    final  static String X_POINT_ID = ParseFilter.class.getName();
    Parse filter(String url, WebPage page, Parse parse, HTMLMetaTags metaTags, DocumentFragment doc);
}
