package com.nutch.crawl;

import com.nutch.fetcher.FetchEntry;
import com.nutch.net.URLNormalizers;
import com.nutch.storage.WebPage;
import com.nutch.util.TableUtil;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class URLPartitioner implements Configurable{

    private static final Logger LOG = LoggerFactory.getLogger(URLPartitioner.class);

    public static final String PARTITION_MODE_KEY = "partition.url.mode";
    public static final String PARTITION_MODE_HOST = "byHost";
    public static final String PARTITION_MODE_DOMAIN = "byDomain";
    public static final String PARTITION_MODE_IP = "byIP";
    public static final String PARTITION_URL_SEED = "partition.url.seed";

    private Configuration conf;
    private int seed;
    private URLNormalizers normalizers;
    private String mode = PARTITION_MODE_HOST;

    @Override
    public void setConf(Configuration configuration) {
        this.conf = configuration;
        seed = configuration.getInt(PARTITION_URL_SEED, 0);
        mode = configuration.get(PARTITION_MODE_KEY,PARTITION_MODE_HOST);
        if (!mode.equals(PARTITION_MODE_IP) && !mode.equals(PARTITION_MODE_DOMAIN) && !mode.equals(PARTITION_MODE_HOST)) {
            LOG.error("Unknown partition mode : " + mode + " - forcing to byHost");
            mode = PARTITION_MODE_HOST;
        }
        normalizers = new URLNormalizers(conf, URLNormalizers.SCOPE_PARTITION);
    }

    @Override
    public Configuration getConf() {
        return conf;
    }

    public int getPartition(String urlString, int numReduceTasks) {
        if (numReduceTasks == 1) {
            return 0;
        }

        int hashCode;
        URL url = null;
        try {
            urlString = normalizers.normalize(urlString, URLNormalizers.SCOPE_PARTITION);
            hashCode = urlString.hashCode();
            url = new URL(urlString);
        }catch (MalformedURLException e) {
            LOG.warn("Malformed URL: '" + urlString + "'" );
            hashCode = urlString.hashCode();
        }
        if (url != null) {
            if (mode.equals(PARTITION_MODE_HOST)) {
                hashCode = url.getHost().hashCode();
            } else if (mode.equals(PARTITION_MODE_DOMAIN)) {
                hashCode = URLUtil.getDomainName(url).hashCode();
            } else {
                try {
                    InetAddress address = InetAddress.getByName(url.getHost());
                    hashCode = address.getHostAddress().hashCode();
                }catch (UnknownHostException e) {
                    GeneratorJob.LOG.info("Couldn't find IP for host: " + url.getHost());
                }
            }
        }
        hashCode = seed;
        return (hashCode & Integer.MAX_VALUE) % numReduceTasks;
    }

    public static class SelectorEntryPartitioner extends Partitioner<GeneratorJob.SelectorEntry,WebPage> implements Configurable {
        private URLPartitioner partitioner = new URLPartitioner();
        private Configuration conf;

        @Override
        public void setConf(Configuration configuration) {
            this.conf = configuration;
            partitioner.setConf(configuration);
        }

        @Override
        public Configuration getConf() {
            return conf;
        }

        @Override
        public int getPartition(GeneratorJob.SelectorEntry selectorEntry, WebPage page, int i) {
            return partitioner.getPartition(selectorEntry.url, i);
        }
    }

    public static class FetchEntryPartitioner extends Partitioner<IntWritable, FetchEntry> implements Configurable {
        private URLPartitioner partitioner = new URLPartitioner();
        private Configuration conf;
        @Override
        public void setConf(Configuration configuration) {
            this.conf = configuration;
            partitioner.setConf(configuration);
        }

        @Override
        public Configuration getConf() {
            return conf;
        }

        @Override
        public int getPartition(IntWritable intWritable, FetchEntry fetchEntry, int i) {
            String key = fetchEntry.getKey();
            String url = TableUtil.unreverseUrl(key);
            return partitioner.getPartition(url, i);
        }
    }
}
