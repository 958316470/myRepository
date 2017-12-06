package com.nutch.net;

import com.nutch.plugin.Pluggable;
import org.apache.hadoop.conf.Configurable;

public interface URLFilter extends Pluggable,Configurable{
    public final static String X_POINT_ID = URLFilter.class.getName();
    public String filter(String urlString);
}
