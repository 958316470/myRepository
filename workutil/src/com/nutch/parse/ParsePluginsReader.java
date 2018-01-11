package com.nutch.parse;

import com.nutch.util.NutchConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsePluginsReader {

    public static final Logger LOG = LoggerFactory.getLogger(ParsePluginsReader.class);
    private static final String PP_FILE_PROP = "parse.plugin.file";
    private String fParsePluginsFile = null;
    public ParsePluginsReader() {}

    public ParsePluginList parse(Configuration conf) {
        ParsePluginList pList = new ParsePluginList();
        DocumentBuilderFactory factory = null;
        DocumentBuilder parser = null;
        Document document = null;
        InputSource inputSource = null;
        InputStream ppInputStream = null;
        if (fParsePluginsFile != null) {
            URL parsePluginUrl = null;
            try {
                parsePluginUrl = new URL(fParsePluginsFile);
                ppInputStream = parsePluginUrl.openStream();
            } catch (Exception e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Unable to load parse plugins file from URL [" + fParsePluginsFile + "]. Reason is [" + e + "]");
                }
                return pList;
            }
        } else {
            ppInputStream = conf.getConfResourceAsInputStream(conf.get(PP_FILE_PROP));
        }
        inputSource = new InputSource(ppInputStream);
        try {
            factory = DocumentBuilderFactory.newInstance();
            parser = factory.newDocumentBuilder();
            document = parser.parse(inputSource);
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Unable to parse [" + fParsePluginsFile + "]. Reason is [" + e + "]");
            }
            return null;
        }

        Element parsePlugins = document.getDocumentElement();
        Map<String, String> aliases = getAliases(parsePlugins);
        pList.setAliases(aliases);
        NodeList mimeTypes = parsePlugins.getElementsByTagName("mimeType");
        for (int i = 0; i < mimeTypes.getLength(); i++) {
            Element mimeType = (Element) mimeTypes.item(i);
            String mimeTypeStr = mimeType.getAttribute("name");
            NodeList pluginList = mimeType.getElementsByTagName("plugin");
            if(pluginList != null && pluginList.getLength() > 0) {
                List<String> plugList = new ArrayList<String>(pluginList.getLength());
                for (int j = 0;j < pluginList.getLength(); j++) {
                    Element plugin = (Element) pluginList.item(j);
                    String pluginId = plugin.getAttribute("id");
                    String extId = aliases.get(pluginId);
                    if (extId == null) {
                        extId = pluginId;
                    }
                    String orderStr = plugin.getAttribute("order");
                    int order = -1;
                    try {
                         order = Integer.parseInt(orderStr);
                    } catch (NumberFormatException e) {
                    }
                    if (order != -1) {
                        plugList.add(order - 1, extId);
                    } else {
                        plugList.add(extId);
                    }
                }
                pList.setPluginList(mimeTypeStr, plugList);
            } else if (LOG.isWarnEnabled()) {
                LOG.warn("ParsePluginsReader:ERROR:no plugins defined for mime type: " + mimeTypeStr + ", continuing parse");
            }
        }
        return pList;
    }

    public static void main(String[] args) throws Exception {
        String parsePluginFile = null;
        String usage = "ParsePluginsReader [--file <parse plugin file location>]";
        if ((args.length != 0 && args.length != 2) || (args.length == 2 && !"--file".equals(args[0]))) {
            System.err.println(usage);
            System.exit(1);
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--file")) {
                parsePluginFile = args[++i];
            }
        }
        ParsePluginsReader reader = new ParsePluginsReader();
        if (parsePluginFile != null) {
            reader.setFParsePluginsFile(parsePluginFile);
        }
        ParsePluginList prefs = reader.parse(NutchConfiguration.create());
        for (String mimeType : prefs.getSupportedMimeTypes()) {
            System.out.println("MIMETYPE: " + mimeType);
            List<String> plugList = prefs.getPluginList(mimeType);
            System.out.println("Extension IDs:");
            for (String j : plugList) {
                System.out.println(j);
            }
        }
    }

    public String getFParsePluginsFile() {
        return fParsePluginsFile;
    }

    public void setFParsePluginsFile(String parsePluginsFile) {
        fParsePluginsFile = parsePluginsFile;
    }

    private Map<String, String> getAliases(Element parsePluginsRoot) {
        Map<String, String> aliases = new HashMap<String, String>();
        NodeList aliasRoot = parsePluginsRoot.getElementsByTagName("aliases");
        if (aliasRoot == null || (aliasRoot != null && aliasRoot.getLength() == 0)) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("No aliases defined in parse-plugins.xml");
            }
            return aliases;
        }
        if (aliasRoot.getLength() > 1) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("There should only be one \"aliases\" tag in parse-plugins.xml");
            }
        }
        Element aliasRootElem = (Element) aliasRoot.item(0);
        NodeList aliasElements = aliasRootElem.getElementsByTagName("alias");
        if (aliasElements != null && aliasElements.getLength() > 0) {
            for (int i = 0; i < aliasElements.getLength(); i++) {
                Element aliasElem = (Element) aliasElements.item(i);
                String parsePluginId = aliasElem.getAttribute("name");
                String extensionId = aliasElem.getAttribute("extension-id");
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Found alias: plugin-id: " + parsePluginId + ", extension-id: " + extensionId);
                }
                if (parsePluginId != null && extensionId != null) {
                    aliases.put(parsePluginId, extensionId);
                }
            }
        }
        return aliases;
    }

}
