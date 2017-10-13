package com.nutch.plugin;


import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class PluginDescriptor {

    private String fPluginPath;

    private String fPluginClass = Plugin.class.getName();

    private String fPluginId;

    private String fVersion;

    private String fName;

    private String fProviderName;

    private HashMap<String, ResourceBundle> fMessage = new HashMap<String, ResourceBundle>();

    private ArrayList<ExtensionPoint> fExtensionPoints = new ArrayList<ExtensionPoint>();

    private ArrayList<String> fDependencies = new ArrayList<String>();

    private ArrayList<URL> fExportedLibs = new ArrayList<URL>();

    private ArrayList<URL> fNotExportedLibs = new ArrayList<URL>();

    private ArrayList<Extension> fExtensions = new ArrayList<Extension>();

    private PluginClassLoader fClassLoader;

    public static final Logger LOG = LoggerFactory.getLogger(PluginDescriptor.class);

    private Configuration fConf;

    public PluginDescriptor(String pId, String pVersion, String pName,
                            String pProviderName, String pPluginClazz, String pPath, Configuration conf) {
        setPath(pPath);
        setPluginId(pId);
        setVersion(pVersion);
        setName(pName);
        setProviderName(pProviderName);

        if (pPluginClazz != null) {
            setPluginClass(pPluginClazz);
        }

        this.fConf = conf;
    }

    private void setPath(String pPath) {
        fPluginPath = pPath;
    }

    public String getName() {
        return fName;
    }

    private void setProviderName(String providerName) {
        fProviderName = providerName;
    }

    private void setName(String name) {
        fName = name;
    }

    private void setVersion(String version) {
        fVersion = version;
    }

    public String getPluginClass() {
        return fPluginClass;
    }

    public String getPluginId() {
        return fPluginId;
    }

    public Extension[] getExtensions() {
        return fExtensions.toArray(new Extension[fExtensions.size()]);
    }

    public void addExtensions(Extension pExtension) {
        fExtensions.add(pExtension);
    }

    private void setPluginClass(String pluginClass) {
        fPluginClass = pluginClass;
    }

    private void setPluginId(String pluginId) {
        fPluginId = pluginId;
    }

    public void addExtensionPoint(ExtensionPoint extensionPoint) {
        fExtensionPoints.add(extensionPoint);
    }

    public ExtensionPoint[] getExtensionPoints() {
        return fExtensionPoints.toArray(new ExtensionPoint[fExtensionPoints.size()]);
    }

    public String[] getDependencies() {
        return fDependencies.toArray(new String[fDependencies.size()]);
    }

    public void addDependency(String pId) {
        fDependencies.add(pId);
    }

    public void addExportedLibRelative(String pLibPath) throws MalformedURLException {
        URL url = new File(getPluginPath() + File.separator + pLibPath).toURI().toURL();
        fExportedLibs.add(url);
    }

    public String getPluginPath() {
        return fPluginPath;
    }

    public URL[] getExportedLibUrls() {
        return fExportedLibs.toArray(new URL[fExportedLibs.size()]);
    }

    public void addNotExportedLibRelative(String pLibPath) throws MalformedURLException {
        URL url = new File(getPluginPath() + File.separator + pLibPath).toURI().toURL();
        fNotExportedLibs.add(url);
    }

    public URL[] getNotExportedLibUrls() {
        return fNotExportedLibs.toArray(new URL[fNotExportedLibs.size()]);
    }

    public PluginClassLoader getClassLoader(){
        if(fClassLoader != null){
            return fClassLoader;
        }

        ArrayList<URL> arrayList = new ArrayList<URL>();
        arrayList.addAll(fExportedLibs);
        arrayList.addAll(fNotExportedLibs);
        arrayList.addAll(getDependencyLibs());
        File file = new File(getPluginPath());
        try{
            for(File file1 : file.listFiles()){
                if(file1.getAbsolutePath().endsWith("properties")){
                    arrayList.add(file1.getParentFile().toURI().toURL());
                }
            }
        }catch (MalformedURLException e){
            LOG.debug(getPluginId() + " " +e.toString());
        }
        URL[] urls = arrayList.toArray(new URL[arrayList.size()]);
        fClassLoader = new PluginClassLoader(urls,PluginDescriptor.class.getClassLoader());
        return fClassLoader;
    }

    public ArrayList<URL> getDependencyLibs(){
        ArrayList<URL> list = new ArrayList<URL>();
        collectLibs(list,this);
        return list;
    }

    private void collectLibs(ArrayList<URL> pLibs,PluginDescriptor pDescriptor){

        for(String id : pDescriptor.getDependencies()){
            PluginDescriptor descriptor = PluginRepository.get(fConf).getPluginDescriptor(id);
            for(URL url : descriptor.getExportedLibUrls()){
                pLibs.add(url);
            }
            collectLibs(pLibs,descriptor);
        }
    }

    public String getResoutceString(String pKey, Locale pLocale) throws IOException{

        if(fMessage.containsKey(pLocale.toString())){
            ResourceBundle bundle = fMessage.get(pLocale.toString());
            try{
                return bundle.getString(pKey);
            }catch (MissingResourceException e){
                return '!' + pKey + '!';
            }
        }

        try{
            ResourceBundle res = ResourceBundle.getBundle("message",pLocale,getClassLoader());
            return res.getString(pKey);
        }catch (MissingResourceException e){
            return '!' + pKey + '!';
        }
    }

    public String getProviderName(){
        return fProviderName;
    }

    public String getVersion(){
        return fVersion;
    }
}
