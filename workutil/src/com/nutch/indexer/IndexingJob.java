package com.nutch.indexer;

import com.nutch.util.NutchTool;
import org.apache.hadoop.util.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class IndexingJob extends NutchTool implements Tool {


    @Override
    public Map<String, Object> run(Map<String, Object> args) throws Exception {
        return null;
    }

    @Override
    public int run(String[] strings) throws Exception {
        return 0;
    }
}
