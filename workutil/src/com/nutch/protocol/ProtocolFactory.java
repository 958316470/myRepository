package com.nutch.protocol;

import com.nutch.plugin.Extension;
import com.nutch.plugin.ExtensionPoint;
import com.nutch.plugin.PluginRepository;
import com.nutch.plugin.PluginRuntimeException;
import com.nutch.storage.WebPage;
import com.nutch.util.ObjectCache;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

public class ProtocolFactory {

    public static final Logger LOG = LoggerFactory.getLogger(ProtocolFactory.class);
    private final ExtensionPoint extensionPoint;
    private final Configuration conf;
    public ProtocolFactory(Configuration conf) {
        this.conf = conf;
        this.extensionPoint = PluginRepository.get(conf).getExtensionPoint(Protocol.X_POINT_ID);
        if (this.extensionPoint == null) {
            throw new RuntimeException("x-point " + Protocol.X_POINT_ID + " not found.");
        }
    }

    public synchronized Protocol getProtocol(String urlString) throws ProtocolNotFound {
        ObjectCache objectCache = ObjectCache.get(conf);
        try {
            URL url = new URL(urlString);
            String protocolName = url.getProtocol();
            if (protocolName == null) {
                throw new ProtocolNotFound(urlString);
            }
            String cacheId = Protocol.X_POINT_ID + protocolName;
            Protocol protocol = (Protocol) objectCache.getObject(cacheId);
            if (protocol != null) {
                return protocol;
            }
            Extension extension = findExtension(protocolName);
            if (extension == null) {
                throw new ProtocolNotFound(protocolName);
            }
            protocol = (Protocol) extension.getExtensionInstance();
            objectCache.setObject(cacheId, protocol);
            return protocol;
        } catch (MalformedURLException e) {
            throw new ProtocolNotFound(urlString,e.toString());
        } catch (PluginRuntimeException e) {
            throw new ProtocolNotFound(urlString,e.toString());
        }
    }

    private Extension findExtension(String name) throws PluginRuntimeException {
        Extension[] extensions = this.extensionPoint.getExtensions();
        for (int i = 0; i < extensions.length; i++) {
            Extension extension = extensions[i];
            if (contains(name,extension.getAttribute("protocolName"))) {
                return extension;
            }
        }
        return null;
    }
    boolean contains(String what, String where) {
        String[] parts = where.split("[, ]");
        for (int i =0; i < parts.length; i++) {
            if(parts[i].equals(what)) {
                return true;
            }
        }
        return false;
    }

    public Collection<WebPage.Field> getFields(){
        Collection<WebPage.Field>  fields = new HashSet<WebPage.Field>();
        for (Extension extension : this.extensionPoint.getExtensions()) {
            Protocol protocol;
            try {
                protocol = (Protocol) extension.getExtensionInstance();
                Collection<WebPage.Field> pluginFields = protocol.getFields();
                if (pluginFields != null) {
                    fields.addAll(pluginFields);
                }
            } catch (PluginRuntimeException e) {

            }
        }
        return fields;
    }
}
