package com.nutch.net;

import com.nutch.plugin.Extension;
import com.nutch.plugin.ExtensionPoint;
import com.nutch.plugin.PluginRepository;
import com.nutch.plugin.PluginRuntimeException;
import com.nutch.util.ObjectCache;
import org.apache.hadoop.conf.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class URLFilters {

    public static final String URLFILTER_ORDER = "urlfilter.order";
    private URLFilter[] filters;

    public URLFilters(Configuration conf) {
        String order = conf.get(URLFILTER_ORDER);
        ObjectCache objectCache = ObjectCache.get(conf);
        this.filters = (URLFilter[]) objectCache.getObject(URLFilter.class.getName());
        if (this.filters == null) {
            String[] orderedFilters = null;
            if (order != null && !order.trim().equals("")) {
                orderedFilters = order.split("\\s+");
            }
            try {
                ExtensionPoint point = PluginRepository.get(conf).getExtensionPoint(URLFilter.X_POINT_ID);
                if (point == null) {
                    throw new RuntimeException(URLFilter.X_POINT_ID + " not found.");
                }
                Extension[] extensions = point.getExtensions();
                Map<String,URLFilter> filterMap = new HashMap<String,URLFilter>();
                for (int i = 0; i < extensions.length; i++) {
                    Extension extension = extensions[i];
                    URLFilter filter = (URLFilter) extension.getExtensionInstance();
                    if (!filterMap.containsKey(filter.getClass().getName())) {
                        filterMap.put(filter.getClass().getName(),filter);
                    }
                }
                if (orderedFilters == null) {
                    objectCache.setObject(URLFilter.class.getName(), filterMap.values().toArray(new URLFilter[0]));
                }else {
                    ArrayList<URLFilter> filters = new ArrayList<URLFilter>();
                    for (int i = 0; i < orderedFilters.length; i++) {
                        URLFilter filter = filterMap.get(orderedFilters[i]);
                        if (filter != null) {
                            filters.add(filter);
                        }
                    }
                    objectCache.setObject(URLFilter.class.getName(),filters.toArray(new URLFilter[filters.size()]));
                }
            }catch (PluginRuntimeException e) {
                throw new RuntimeException(e);
            }
            this.filters = (URLFilter[]) objectCache.getObject(URLFilter.class.getName());
        }
    }

    public String filter(String urlString) throws URLFilterException {
        for (int i = 0; i < this.filters.length; i++) {
            if (urlString == null) {
                return null;
            }
            urlString = this.filters[i].filter(urlString);
        }
        return urlString;
    }
}
