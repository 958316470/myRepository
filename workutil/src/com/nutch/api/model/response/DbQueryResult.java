package com.nutch.api.model.response;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
public class DbQueryResult {
    private List<Map<String,Object>> values = Lists.newLinkedList();

    public List<Map<String, Object>> getValues() {
        return Collections.unmodifiableList(values);
    }

    public void addValues(Map<String, Object> next) {
        values.add(next);
    }
}
