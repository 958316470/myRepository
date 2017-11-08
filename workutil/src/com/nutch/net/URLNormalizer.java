package com.nutch.net;

import org.apache.hadoop.conf.Configurable;

import java.net.MalformedURLException;

public interface URLNormalizer extends Configurable{
    public static final String X_POINT_ID = URLNormalizer.class.getName();
    public String normalize(String urlString, String scope) throws MalformedURLException;
}
