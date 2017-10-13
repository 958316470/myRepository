package com.nutch.plugin;


import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class PluginManifestParser {

    private static final String ATTR_NAME = "name";
    private static final String ATTR_CLASS = "class";
    private static final String ATTR_ID = "id";

    public static final Logger LOG = PluginRepository.LOG;

    private static final boolean WINDOWS = System.getProperty("os.name").startsWith("Windows");

    private Configuration conf;

    private PluginRepository pluginRepository;

    public PluginManifestParser(Configuration conf, PluginRepository pluginRepository) {
        this.conf = conf;
        this.pluginRepository = pluginRepository;
    }

    public Map<String, PluginDescriptor> parsePluginFolder(String[] pluginFolders) {
        Map<String, PluginDescriptor> map = new HashMap<String, PluginDescriptor>();
        if (pluginFolders == null) {
            throw new IllegalArgumentException("plugin.folders is not defined");
        }
        for (String name : pluginFolders) {
            File directory = getPluginFolder(name);
            if (directory == null) {
                continue;
            }
            LOG.info("Plugins: looking in:" + directory.getAbsolutePath());
            for (File oneSubFolder : directory.listFiles()) {
                if (oneSubFolder.isDirectory()) {
                    String manifestPath = oneSubFolder.getAbsolutePath() + File.separator + "plugin.xml";
                    try {
                        LOG.debug("parsing: " + manifestPath);
                        PluginDescriptor p = parseManifestFile(manifestPath);
                        map.put(p.getPluginId(), p);
                    } catch (Exception e) {
                        LOG.warn("Error while loading plugin '" + manifestPath + "'" + e.toString());
                    }
                }
            }
        }
        return map;
    }

    public File getPluginFolder(String name) {
        File directory = new File(name);
        if (!directory.isAbsolute()) {
            URL url = PluginManifestParser.class.getClassLoader().getResource(name);
            if (url == null && directory.exists() && directory.isDirectory() && directory.listFiles().length > 0) {
                return directory;
            } else if (url == null) {
                LOG.warn("Plugins: directory not found: " + name);
                return null;
            } else if (!"file".equals(url.getProtocol())) {
                LOG.warn("Plugins: not a file: url. Can't load plugins from: " + url);
                return null;
            }
            String path = url.getPath();
            if (WINDOWS && path.startsWith("/")) {
                path = path.substring(1);
            }
            try {
                path = URLDecoder.decode(path, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
        } else if (!directory.exists()) {
            LOG.warn("Plugins: directory not found: " + name);
            return null;
        }
        return directory;
    }

    private PluginDescriptor parseManifestFile(String pMainfestPath) throws MalformedURLException, SAXException, IOException, ParserConfigurationException {
        Document document = parseXml(new File(pMainfestPath).toURI().toURL());
        String pPath = new File(pMainfestPath).getParent();
        return parsePlugin(document, pPath);
    }

    private Document parseXml(URL url) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(url.openStream());
    }

    private PluginDescriptor parsePlugin(Document pDocument, String pPath) throws MalformedURLException {
        Element rootElement = pDocument.getDocumentElement();
        String id = rootElement.getAttribute(ATTR_ID);
        String name = rootElement.getAttribute(ATTR_NAME);
        String version = rootElement.getAttribute("version");
        String providerName = rootElement.getAttribute("provider-name");
        String pluginClazz = null;
        if (rootElement.getAttribute(ATTR_CLASS).trim().length() > 0) {
            pluginClazz = rootElement.getAttribute(ATTR_CLASS);
        }
        PluginDescriptor pluginDescriptor = new PluginDescriptor(id, version, name, providerName, pluginClazz, pPath, this.conf);
        LOG.debug("plugin: id=" + id + " name=" + name + " version=" + version + " name=" + name + " provider=" + providerName + " class=" + pluginClazz);
        parseExtension(rootElement, pluginDescriptor);
        parseExtensionPoints(rootElement, pluginDescriptor);
        parseLibrariies(rootElement, pluginDescriptor);
        parserRequires(rootElement, pluginDescriptor);
        return pluginDescriptor;
    }

    private void parserRequires(Element pRootElement, PluginDescriptor pDescriptor) throws MalformedURLException {
        NodeList nodeList = pRootElement.getElementsByTagName("requires");
        if (nodeList.getLength() > 0) {
            Element requires = (Element) nodeList.item(0);
            NodeList imports = requires.getElementsByTagName("import");
            for (int i = 0; i < imports.getLength(); i++) {
                Element anImport = (Element) imports.item(i);
                String plugin = anImport.getAttribute("plugin");
                if (plugin != null) {
                    pDescriptor.addDependency(plugin);
                }
            }
        }
    }

    private void parseLibrariies(Element pRootElement, PluginDescriptor pDescriptor) throws MalformedURLException {
        NodeList nodeList = pRootElement.getElementsByTagName("runtime");
        if (nodeList.getLength() > 0) {
            Element runtime = (Element) nodeList.item(0);
            NodeList libraries = runtime.getElementsByTagName("library");
            for (int i = 0; i < libraries.getLength(); i++) {
                Element library = (Element) libraries.item(i);
                String libName = library.getAttribute(ATTR_NAME);
                NodeList list = library.getElementsByTagName("export");
                Element exportElement = (Element) list.item(0);
                if (exportElement != null) {
                    pDescriptor.addExportedLibRelative(libName);
                } else {
                    pDescriptor.addNotExportedLibRelative(libName);
                }
            }
        }
    }

    private void parseExtensionPoints(Element pRootElement, PluginDescriptor pPluginDescriptor) {
        NodeList list = pRootElement.getElementsByTagName("extension-point");
        if (list != null) {
            for (int i = 0; i < list.getLength(); i++) {
                Element oneExtensionPoint = (Element) list.item(i);
                String id = oneExtensionPoint.getAttribute(ATTR_ID);
                String name = oneExtensionPoint.getAttribute(ATTR_NAME);
                String schema = oneExtensionPoint.getAttribute("schema");
                ExtensionPoint extensionPoint = new ExtensionPoint(id, name, schema);
                pPluginDescriptor.addExtensionPoint(extensionPoint);
            }
        }
    }

    private void parseExtension(Element pRootElement, PluginDescriptor pPluginDescriptor) {
        NodeList extensions = pRootElement.getElementsByTagName("extension");
        if (extensions != null) {
            for (int i = 0; i < extensions.getLength(); i++) {
                Element oneExtension = (Element) extensions.item(i);
                String pointId = oneExtension.getAttribute("point");
                NodeList extensionImplementations = oneExtension.getChildNodes();
                if (extensionImplementations != null) {
                    for (int j = 0; j < extensionImplementations.getLength(); j++) {
                        Node node = extensionImplementations.item(j);
                        if (!node.getNodeName().equals("implementation")) {
                            continue;
                        }
                        Element oneImplementation = (Element) node;
                        String id = oneImplementation.getAttribute(ATTR_ID);
                        String extensionClass = oneImplementation.getAttribute(ATTR_CLASS);
                        LOG.debug("impl: point=" + pointId + " class=" + extensionClass);
                        Extension extension = new Extension(pPluginDescriptor, pointId, id, extensionClass, this.conf, this.pluginRepository);
                        NodeList parameters = oneImplementation.getElementsByTagName("parameter");
                        if (parameters != null) {
                            for (int k = 0; k < parameters.getLength(); k++) {
                                Element param = (Element) parameters.item(k);
                                extension.addAttribute(param.getAttribute(ATTR_NAME), param.getAttribute("value"));
                            }
                        }
                        pPluginDescriptor.addExtensions(extension);
                    }
                }
            }
        }
    }
}
