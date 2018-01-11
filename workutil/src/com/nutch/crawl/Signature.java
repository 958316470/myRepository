package com.nutch.crawl;

import com.nutch.storage.WebPage;
import org.apache.hadoop.conf.Configured;

import java.util.Collection;

public abstract class Signature extends Configured {

    public abstract byte[] calculate(WebPage page);

    public abstract Collection<WebPage.Field> getFields();
}
