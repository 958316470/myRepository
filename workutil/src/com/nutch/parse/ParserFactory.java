package com.nutch.parse;

import com.nutch.plugin.Extension;
import com.nutch.plugin.ExtensionPoint;
import com.nutch.plugin.PluginRepository;
import com.nutch.plugin.PluginRuntimeException;
import com.nutch.storage.WebPage;
import com.nutch.util.MimeUtil;
import com.nutch.util.ObjectCache;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ParserFactory {

    public static final Logger LOG = LoggerFactory.getLogger(ParserFactory.class);

    public static final String DEFAULT_PLUGIN = "*";

    private final List<Extension> EMPTY_EXTENSION_LIST = new ArrayList<Extension>();

    private final Configuration conf;

    private final ExtensionPoint extensionPoint;
    private ParsePluginList parsePluginList;

    public ParserFactory(Configuration conf) {
        this.conf = conf;
        ObjectCache objectCache = ObjectCache.get(conf);
        this.extensionPoint = PluginRepository.get(conf).getExtensionPoint(Parser.X_POINT_ID);
        this.parsePluginList = (ParsePluginList) objectCache.getObject(ParsePluginList.class.getName());
        if (this.parsePluginList == null) {
            this.parsePluginList = new ParsePluginsReader().parse(conf);
            objectCache.setObject(ParsePluginList.class.getName(), this.parsePluginList);
        }
        if (this.extensionPoint == null) {
            throw new RuntimeException("x point " + Parser.X_POINT_ID + "not found.");
        }
        if (this.parsePluginList == null) {
            throw new RuntimeException("Parse Plugins preferences could not be loaded.");
        }
    }

    public Parser[] getParsers(String contentType, String url) throws ParserNotFound {
        List<Parser> parsers = null;
        List<Extension> parserExts = null;
        ObjectCache objectCache = ObjectCache.get(conf);
        parserExts = getExtensions(contentType);
        if (parserExts == null) {
            throw new ParserNotFound(url, contentType);
        }
        parsers = new ArrayList<Parser>(parserExts.size());
        for (Extension ext : parserExts) {
            Parser p = null;
            try{
                p = (Parser) objectCache.getObject(ext.getId());
                if (p == null) {
                    p = (Parser) ext.getExtensionInstance();
                    objectCache.setObject(ext.getId(), p);
                }
                parsers.add(p);
            }catch (PluginRuntimeException  e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("ParserFactory:PluginRuntimeException when initializing parser plugin"
                    + ext.getDescriptor().getPluginId() + " instance in getParsers function: attempting to continue instantiating parsers: ", e );
                }
            }
        }
        return parsers.toArray(new Parser[] {});
    }

    public Parser getParserById(String id) throws ParserNotFound {
        Extension[] extensions = this.extensionPoint.getExtensions();
        Extension parserExt = null;
        ObjectCache objectCache = ObjectCache.get(conf);
        if (id != null) {
            parserExt = getExtension(extensions, id);
        }
        if (parserExt == null) {
            parserExt = getExtensionFromAlias(extensions, id);
        }

        if (parserExt == null) {
            throw new ParserNotFound("No Parser Found for id [" + id + "]");
        }

        if (objectCache.getObject(parserExt.getId()) != null) {
            return (Parser) objectCache.getObject(parserExt.getId());
        } else {
            try {
                Parser p = (Parser) parserExt.getExtensionInstance();
                objectCache.setObject(parserExt.getId(), p);
                return p;
            } catch (PluginRuntimeException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Cannot initialize parser " + parserExt.getDescriptor().getPluginId() + "(cause: " + e.toString());
                }
                throw new ParserNotFound("Cannot init parser for id [" + id + "]");
            }
        }
    }

    public Collection<WebPage.Field> getFields() {
        Set<WebPage.Field> columns = new HashSet<WebPage.Field>();
        Extension[] extensions = this.extensionPoint.getExtensions();
        for (Extension ext : extensions) {
            try {
                Parser parser = (Parser) ext.getExtensionInstance();
                Collection<WebPage.Field> plugginFields = parser.getFields();
                if (plugginFields != null) {
                    columns.addAll(plugginFields);
                }
            } catch (PluginRuntimeException e) {

            }
        }
        return columns;
    }

    protected List<Extension> getExtensions(String contentType) {
        ObjectCache objectCache = ObjectCache.get(conf);
        String type = null;
        type = MimeUtil.cleanMimeType(contentType);
        List<Extension> extensions = (List<Extension>) objectCache.getObject(type);
        if (extensions == EMPTY_EXTENSION_LIST) {
            return null;
        }
        if (extensions == null) {
            extensions = findExtensions(type);
            if(extensions != null) {
                objectCache.setObject(type, extensions);
            } else {
                objectCache.setObject(type, EMPTY_EXTENSION_LIST);
            }
        }
        return extensions;
    }

    private List<Extension> findExtensions(String contentType) {
        Extension[] extensions = this.extensionPoint.getExtensions();
        List<String> parsePluginList = this.parsePluginList.getPluginList(contentType);
        List<Extension> extensionList = matchExtensions(parsePluginList, extensions, contentType);
        if (extensionList != null) {
            return extensionList;
        }
        parsePluginList = this.parsePluginList.getPluginList(DEFAULT_PLUGIN);
        return matchExtensions(parsePluginList, extensions, DEFAULT_PLUGIN);
    }

    private List<Extension> matchExtensions(List<String> plugins, Extension[] extensions, String contentType) {
        List<Extension> extList = new ArrayList<Extension>();
        if (plugins != null) {
            for (String parsePluginId : plugins) {
                Extension ext = getExtension(extensions, parsePluginId, contentType);
                if (ext == null) {
                    ext = getExtension(extensions, parsePluginId);
                    if (LOG.isWarnEnabled()) {
                        if (ext != null) {
                            LOG.warn("ParserFactory:Plugin: " + parsePluginId + " mapped to contentType " + contentType
                            + " via parse-plugins.xml, but its plugin.xml file does not claim to support contentType: " + contentType );
                        } else {
                            LOG.warn("ParserFactory: Plugin: " + parsePluginId + " mapped to contentType " + contentType
                            + "via parse-plugins.xml, but not enabled via plugin.includes in nutch-default.xml");
                        }
                    }
                }
                if (ext != null) {
                    extList.add(ext);
                }
            }
        }else {
            for(int i = 0; i < extensions.length; i++) {
                if ("*".equals(extensions[i].getAttribute("contentType"))) {
                    extList.add(0, extensions[i]);
                } else if (extensions[i].getAttribute("contentType") != null
                        && contentType.matches(escapeContentType(extensions[i].getAttribute("contentType")))){
                    extList.add(extensions[i]);
                }
            }
            if (extList.size() > 0) {
                if(LOG.isInfoEnabled()) {
                    StringBuffer extensionsIDs = new StringBuffer("[");
                    boolean isFirst = true;
                    for (Extension ext : extList) {
                        if (!isFirst){
                            extensionsIDs.append(" - ");
                        } else {
                            isFirst = false;
                        }
                        extensionsIDs.append(ext.getId());
                    }
                    extensionsIDs.append("]");
                    LOG.info("The parsing plugins: " + extensionsIDs.toString() + " are enabled via the plugin.includes" +
                            " system property, and all claim to support the content type "
                    + contentType + ", but they are not mapped to it in the parse-plugins.xml file");
                }
            } else if (LOG.isDebugEnabled()) {
                LOG.debug("ParserFactory: No parse plugins mapped or enabled for contentType " + contentType );
            }
        }
        return (extList.size() > 0) ? extList : null;
    }

    private String escapeContentType(String contentType) {
        return contentType.replace("+", "\\+").replace(".","\\.");
    }

    private boolean match(Extension extension, String id, String type) {
        return (id.equals(extension.getId()) && (extension.getAttribute("contentType").equals("*"))
        || type.matches(escapeContentType(extension.getAttribute("contentType"))) || type.equals(DEFAULT_PLUGIN));
    }

    private Extension getExtension(Extension[] list, String id, String type) {
        for (int i = 0; i < list.length; i++) {
            if (match(list[i], id, type)) {
                return list[i];
            }
        }
        return null;
    }

    private Extension getExtension(Extension[] list, String id) {
        for (int i = 0; i < list.length; i++) {
            if (id.equals(list[i].getId())) {
                return list[i];
            }
        }
        return null;
    }

    private Extension getExtensionFromAlias(Extension[] list, String id) {
        return getExtension(list, parsePluginList.getAliases().get(id));
    }
}
