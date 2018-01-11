package com.nutch.parse;

import com.nutch.plugin.Extension;
import com.nutch.plugin.ExtensionPoint;
import com.nutch.plugin.PluginRepository;
import com.nutch.plugin.PluginRuntimeException;
import com.nutch.storage.WebPage;
import com.nutch.util.ObjectCache;
import org.apache.hadoop.conf.Configuration;
import org.w3c.dom.DocumentFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;


public class ParseFilters {

    private ParseFilter[] parseFilters;

    public static final String HTMLPARSEFILTER_ORDER = "htmlparsefilter.order";

    public ParseFilters(Configuration conf) {
        String order = conf.get(HTMLPARSEFILTER_ORDER);
        ObjectCache objectCache = ObjectCache.get(conf);
        this.parseFilters = (ParseFilter[]) objectCache.getObject(ParseFilter.class.getName());
        if (parseFilters == null) {
            String[] orderedFilters = null;
            if (order != null && !order.trim().equals("")) {
                orderedFilters = order.split("\\s+");
            }
            HashMap<String, ParseFilter> filterMap = new HashMap<String, ParseFilter>();
            try {
                ExtensionPoint point = PluginRepository.get(conf).getExtensionPoint(ParseFilter.X_POINT_ID);
                if (point == null) {
                    throw new RuntimeException(ParseFilter.X_POINT_ID + " not found.");
                }
                Extension[] extensions = point.getExtensions();
                for (int i = 0; i < extensions.length; i++) {
                    Extension extension = extensions[i];
                    ParseFilter parseFilter = (ParseFilter) extension.getExtensionInstance();
                    if (!filterMap.containsKey(parseFilter.getClass().getName())) {
                        filterMap.put(parseFilter.getClass().getName(), parseFilter);
                    }
                }
                ParseFilter[] htmlParseFilters = filterMap.values().toArray(new ParseFilter[filterMap.size()]);
                if (orderedFilters == null) {
                    objectCache.setObject(ParseFilter.class.getName(), htmlParseFilters);
                }else {
                    ArrayList<ParseFilter> filters = new ArrayList<ParseFilter>();
                    for (int i = 0; i < orderedFilters.length; i++) {
                        ParseFilter filter = filterMap.get(orderedFilters[i]);
                        if (filter != null) {
                            filters.add(filter);
                        }
                    }
                    objectCache.setObject(ParseFilter.class.getName(), filters.toArray(new ParseFilters[filters.size()]));
                }
            } catch (PluginRuntimeException e) {
                throw new RuntimeException(e);
            }
            this.parseFilters = (ParseFilter[]) objectCache.getObject(ParseFilter.class.getName());
        }
    }

    public Parse filter(String url, WebPage page, Parse parse, HTMLMetaTags metaTags, DocumentFragment doc) {
        for (ParseFilter parseFilter : parseFilters) {
            parse = parseFilter.filter(url, page, parse, metaTags, doc);
            if (!ParseStatusUtils.isSuccess(parse.getParseStatus())) {
                return parse;
            }
        }
        return parse;
    }

    public Collection<WebPage.Field> getFields() {
        Collection<WebPage.Field> fields = new HashSet<WebPage.Field>();
        for (ParseFilter htmlParseFilter : parseFilters) {
            Collection<WebPage.Field> pluginFields = htmlParseFilter.getFields();
            if (pluginFields != null) {
                fields.addAll(pluginFields);
            }
        }
        return fields;
    }
}
