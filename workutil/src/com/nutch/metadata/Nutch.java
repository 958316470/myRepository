package com.nutch.metadata;

import org.apache.avro.util.Utf8;
import org.apache.hadoop.io.Text;

public class Nutch {

    public static final String ORIGINAL_CHAR_ENCODING = "OriginalCharEncoding";

    public static final String CHAR_ENCODING_FOR_CONVERSION = "CharEncodingForConversion";

    public static final String SIGNATURE_KEY = "nutch.content.digest";

    public static final String BATCH_NAME_KEY = "nutch.batch.name";

    public static final String SOCRE_KEY = "nutch.crawl.score";

    public static final String GENERATE_TIME_KEY = "_ngt_";

    public static final Text WRITABLE_GENERATE_TIME_KEY = new Text(GENERATE_TIME_KEY);

    public static final String PROTO_STATUS_KEY = "_pst_";

    public static final Text WRITABLE_PROTO_STATUS_KEY = new Text(PROTO_STATUS_KEY);

    public static final String FETCH_TIME_KEY = "_ftk_";

    public static final String FETCH_STATUS_KEY = "_fst_";

    public static final String CACHING_FORBIDDEN_KEY = "caching.forbidden";

    public static final Utf8 CACHING_FORBIDDEN_KEY_UTF8 = new Utf8(CACHING_FORBIDDEN_KEY);

    public static final String CATCHING_FORBIDDEN_NONE = "none";

    public static final String CATCHING_FORBIDDEN_ALL = "ALL";

    public static final String CATCHING_FORBIDDEN_CONTENT = "content";

    public static final String REPR_URL_KEY = "_repr_";

    public static final Text WRLTABLE_REPR_URL_KEY = new Text(REPR_URL_KEY);

    public static final String ALL_BATCH_ID_STR = "-all";

    public static final Utf8 ALL_CRAWL_ID = new Utf8(ALL_BATCH_ID_STR);

    public static final String CRAWL_ID_KEY = "storage.crawl.id";

    public static final String ARG_BATCH = "batch";

    public static final String ARG_CARWL = "crawl";

    public static final String ARG_RESUME = "resume";

    public static final String ARG_FORCE = "force";

    public static final String ARG_SORT = "sort";

    public static final String ARG_SOLR = "solr";

    public static final String ARG_THREADS = "threads";

    public static final String ARG_NUMTASKS = "numTasks";

    public static final String ARG_TOPN = "topN";

    public static final String ARG_CURTIME = "curTime";

    public static final String ARG_FILTER = "filter";

    public static final String ARG_NORMALIZE = "normalize";

    public static final String ARG_SEED = "seed";

    public static final String ARG_SEEDDIR = "seedDir";

    public static final String ARG_CLASS = "class";

    public static final String ARG_DEPTH = "depth";

    public static final String STAT_MSG = "msg";

    public static final String STAT_PHASE = "phase";

    public static final String STAT_PROGRESS = "progress";

    public static final String STAT_JOBS = "jobs";

    public static final String STAT_COUNTERS = "counters";
}
