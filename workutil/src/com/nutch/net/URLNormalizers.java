package com.nutch.net;

import com.nutch.plugin.Extension;
import com.nutch.plugin.ExtensionPoint;
import com.nutch.plugin.PluginRepository;
import com.nutch.plugin.PluginRuntimeException;
import com.nutch.util.ObjectCache;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.*;

public final class URLNormalizers {

    public static final String SCOPE_DEFAULT = "default";
    public static final String SCOPE_PARTITION = "partition";
    public static final String SCOPE_GENERATE_HOST_COUNT = "generate_host_count";
    public static final String SCOPE_FETCHER = "fetcher";
    public static final String SCOPE_CRAWLDB = "crawldb";
    public static final String SCOPE_LINKDB = "linkdb";
    public static final String SCOPE_INJECT = "inject";
    public static final String SCOPE_OUTLINK = "outlink";
    public static final Logger LOG = LoggerFactory.getLogger(URLNormalizers.class);
    private final List<Extension> EMPTY_EXTENSION_LIST = Collections.emptyList();
    private final URLNormalizer[] EMPTY_NORMALIZERS = new URLNormalizer[0];
    private Configuration conf;
    private ExtensionPoint extensionPoint;
    private URLNormalizer[] normalizers;
    private int loopCount;

    public URLNormalizers(Configuration conf, String scope) {
        this.conf = conf;
        this.extensionPoint = PluginRepository.get(conf).getExtensionPoint(URLNormalizer.X_POINT_ID);
        ObjectCache objectCache = ObjectCache.get(conf);
        if (this.extensionPoint == null) {
            throw new RuntimeException("x point " + URLNormalizer.X_POINT_ID + " not found.");
        }
        normalizers = (URLNormalizer[]) objectCache.getObject(URLNormalizer.X_POINT_ID + "_" + scope);
        if (normalizers == null) {
            normalizers = getURLNormalizers(scope);
        }
        if (normalizers == EMPTY_NORMALIZERS) {
            normalizers = (URLNormalizer[]) objectCache.getObject(URLNormalizer.X_POINT_ID + "_" + SCOPE_DEFAULT);
            if (normalizers == null) {
                normalizers = getURLNormalizers(SCOPE_DEFAULT);
            }
        }
        loopCount = conf.getInt("urlnormalizer.loop.count",1);
    }
    URLNormalizer[] getURLNormalizers(String scope) {
        List<Extension> extensions = getExtensions(scope);
        ObjectCache objectCache = ObjectCache.get(conf);
        if (extensions == EMPTY_EXTENSION_LIST) {
            return EMPTY_NORMALIZERS;
        }
        List<URLNormalizer> normalizers = new Vector<URLNormalizer>(extensions.size());
        Iterator<Extension> it = extensions.iterator();
        while (it.hasNext()) {
            Extension extension = it.next();
            URLNormalizer normalizer = null;
            try {
                normalizer = (URLNormalizer) objectCache.getObject(extension.getId());
                if (normalizer == null) {
                    normalizer = (URLNormalizer) extension.getExtensionInstance();
                    objectCache.setObject(extension.getId(), normalizer);
                }
                normalizers.add(normalizer);
            } catch (PluginRuntimeException e) {
                e.printStackTrace();
                LOG.warn("URLNormalizers:PluginRuntimeException when "
                        + "initializing url normalizer plugin "
                        + extension.getDescriptor().getPluginId()
                        + " instance in getURLNormalizers "
                        + "function: attempting to continue instantiating plugins");
            }
        }
        return normalizers.toArray(new URLNormalizer[normalizers.size()]);
    }

    private List<Extension> getExtensions(String scope) {
        ObjectCache objectCache = ObjectCache.get(conf);
        List<Extension> extensions = (List<Extension>) objectCache.getObject(URLNormalizer.X_POINT_ID + "_x_" + scope);
        if (extensions == EMPTY_EXTENSION_LIST) {
            return EMPTY_EXTENSION_LIST;
        }
        if (extensions == null) {
            extensions = findExtensions(scope);
            if (extensions != null) {
                objectCache.setObject(URLNormalizer.X_POINT_ID + "_x_" + scope, extensions);
            } else {
                objectCache.setObject(URLNormalizer.X_POINT_ID + "_x_" + scope, EMPTY_EXTENSION_LIST);
                extensions = EMPTY_EXTENSION_LIST;
            }
        }
        return extensions;
    }

    private List<Extension> findExtensions(String scope) {
        String[] orders = null;
        String orderList = conf.get("urlnormalizer.order." + scope);
        if (orderList == null) {
            orderList = conf.get("urlnormalizer.order");
        }
        if (orderList != null && !orderList.trim().equals("")) {
            orders = orderList.split("\\s+");
        }
        String scopeList = conf.get("urlnormalizer.scope." + scope);
        Set<String> impls = null;
        if (scopeList != null & !scopeList.trim().equals("")) {
            String[] names = scopeList.split("\\s+");
            impls = new HashSet<String>(Arrays.asList(names));
        }
        Extension[] extensions = this.extensionPoint.getExtensions();
        HashMap<String,Extension> normalizerExtensions = new HashMap<String, Extension>();
        for (int i = 0; i < extensions.length; i++) {
            Extension extension = extensions[i];
            if (impls != null && !impls.contains(extension.getClazz())) {
                continue;
            }
            normalizerExtensions.put(extension.getClazz(),extension);
        }
        List<Extension> res = new ArrayList<Extension>();
        if (orders == null) {
            res.addAll(normalizerExtensions.values());
        } else {
            for (int i = 0; i < orders.length; i++) {
                Extension extension = normalizerExtensions.get(orders[i]);
                if (extension != null) {
                    res.add(extension);
                    normalizerExtensions.remove(orders[i]);
                }
            }
            res.addAll(normalizerExtensions.values());
        }
        return res;
    }

    public String normalize(String urlString, String scope) throws MalformedURLException {
        String initialString = urlString;
        for (int k = 0; k < loopCount; k++) {
            for (int i = 0; i < this.normalizers.length; i++) {
                if (urlString == null){
                    return null;
                }
                urlString = this.normalizers[i].normalize(urlString, scope);
            }
            if (initialString.equals(urlString)) {
                break;
            }
            initialString = urlString;
        }
        return urlString;
    }
}
