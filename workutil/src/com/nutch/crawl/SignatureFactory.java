package com.nutch.crawl;

import com.nutch.storage.WebPage;
import com.nutch.util.ObjectCache;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class SignatureFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SignatureFactory.class);

    private SignatureFactory() {}

    public static Signature getSignature(Configuration conf) {
        String clazz = conf.get("db.signature.class", MD5Signature.class.getName());
        ObjectCache objectCache = ObjectCache.get(conf);
        Signature impl = (Signature) objectCache.getObject(clazz);
        if (impl == null) {
            try {
                LOG.info("Using Signature impl: " + clazz);
                Class<?> implClass = Class.forName(clazz);
                impl = (Signature) implClass.newInstance();
                impl.setConf(conf);
                objectCache.setObject(clazz, impl);
            } catch (Exception e) {
                throw new RuntimeException("Couldn't create " + clazz, e);
            }
        }
        return impl;
    }
    public static Collection<WebPage.Field> getFields(Configuration conf) {
        Signature impl = getSignature(conf);
        return impl.getFields();
    }
}
