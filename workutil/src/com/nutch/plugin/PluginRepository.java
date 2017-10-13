package com.nutch.plugin;

import com.nutch.util.NutchConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

public class PluginRepository {

    private static final WeakHashMap<String, PluginRepository> CACHE = new WeakHashMap<String, PluginRepository>();

    private boolean auto;

    private List<PluginDescriptor> fRegisteredPlugins;

    private HashMap<String,ExtensionPoint> fExtensionPoints;

    private HashMap<String,Plugin> fActivatedPlugins;

    private static final Map<String,Map<PluginClassLoader,Class>> CLASS_CACHE = new HashMap<String, Map<PluginClassLoader, Class>>();

    private Configuration conf;

    public static final Logger LOG = LoggerFactory.getLogger(PluginRepository.class);

    public PluginRepository(Configuration conf) throws RuntimeException{
        fActivatedPlugins = new HashMap<String, Plugin>();
        fExtensionPoints = new HashMap<String, ExtensionPoint>();
        this.conf = new Configuration(conf);
        this.auto = conf.getBoolean("plugin.auto-activation",true);
        String[] pluginFolders = conf.getStrings("plugin.folders");
        PluginManifestParser manifestParser = new PluginManifestParser(this.conf,this);
        Map<String,PluginDescriptor> allPlugins = manifestParser.parsePluginFolder(pluginFolders);
        if(allPlugins.isEmpty()){
            LOG.warn("No plugins found on paths of property plugin.folders=\"{}\"",conf.get("plugin.folders"));
        }
        Pattern excludes = Pattern.compile(conf.get("plugin.excludes",""));
        Pattern includes = Pattern.compile(conf.get("plugin.includes",""));
        Map<String,PluginDescriptor> filteredPlugins = filter(excludes,includes,allPlugins);
        fRegisteredPlugins = getDependencyCheckedPlugins(filteredPlugins,this.auto ? allPlugins : filteredPlugins);
        installExtensionPoints(fRegisteredPlugins);
        try{
            installExtensions(fRegisteredPlugins);
        }catch (PluginRuntimeException e){
            LOG.error(e.toString());
            throw new RuntimeException(e.getMessage());
        }
        displayStatus();
    }

    public static synchronized PluginRepository get(Configuration conf){
        String uuid = NutchConfiguration.getUUID(conf);
        if(uuid == null){
            uuid = "nonXuYuanDongConf@"+ conf.hashCode();
        }
        PluginRepository result = CACHE.get(uuid);
        if(result == null){
            result = new PluginRepository(conf);
            CACHE.put(uuid,result);
        }
        return result;
    }

    private void installExtensionPoints(List<PluginDescriptor> plugins){
        if(plugins == null){
            return;
        }
        for(PluginDescriptor plugin : plugins){
            for(ExtensionPoint point : plugin.getExtensionPoints()){
                String xpId = point.getId();
                LOG.debug("Adding extension point " + xpId);
                fExtensionPoints.put(xpId,point);
            }
        }
    }

    private void installExtensions(List<PluginDescriptor> pRegisteredPlugins) throws PluginRuntimeException {
        for (PluginDescriptor descriptor : pRegisteredPlugins){
            for (Extension extension : descriptor.getExtensions()){
                String xpId = extension.getTargetPoint();
                ExtensionPoint point = getExtensionPoint(xpId);
                if (point == null) {
                    throw new PluginRuntimeException("Plugin (" + descriptor.getPluginId() + "), " + "extension point: " + xpId + " does not exist.");
                }
                point.addExtension(extension);
            }
        }
    }

    private void getPluginCheckedDependencies(PluginDescriptor plugin,Map<String,PluginDescriptor> plugins,Map<String,PluginDescriptor> dependencies,
                                              Map<String,PluginDescriptor> branch) throws MissingDependencyException,CircularDependencyException {
        if (dependencies == null) {
            dependencies = new HashMap<String, PluginDescriptor>();
        }
        if (branch == null) {
            branch = new HashMap<String, PluginDescriptor>();
        }
        branch.put(plugin.getPluginId(),plugin);

        for (String id : plugin.getDependencies()) {
            PluginDescriptor dependency = plugins.get(id);
            if (dependency == null) {
                throw new MissingDependencyException("Missing dependency " + id + " for plugin " + plugin.getPluginId());
            }
            if (branch.containsKey(id)) {
                throw new CircularDependencyException("Circular dependency detected " + id + " for plugin " + plugin.getPluginId());
            }
            dependencies.put(id,dependency);
            getPluginCheckedDependencies(plugins.get(id), plugins, dependencies, branch);
        }
        branch.remove(plugin.getPluginId());
    }

    private Map<String,PluginDescriptor> getPluginCheckedDependencies(PluginDescriptor plugin, Map<String,PluginDescriptor> plugins) throws MissingDependencyException,CircularDependencyException {

        Map<String, PluginDescriptor> dependencies = new HashMap<String, PluginDescriptor>();
        Map<String,PluginDescriptor> branch = new HashMap<String, PluginDescriptor>();
        getPluginCheckedDependencies(plugin, plugins,dependencies,branch);
        return dependencies;
    }

    private List<PluginDescriptor> getDependencyCheckedPlugins(Map<String,PluginDescriptor> filtered,Map<String,PluginDescriptor> all) {
        if (filtered == null) {
            return null;
        }
        Map<String, PluginDescriptor> checked = new HashMap<String, PluginDescriptor>();
        for (PluginDescriptor plugin : filtered.values()) {
            try {
                checked.putAll(getPluginCheckedDependencies(plugin,all));
                checked.put(plugin.getPluginId(),plugin);
            } catch (MissingDependencyException mde) {
                LOG.warn(mde.getMessage());
            } catch (CircularDependencyException cde) {
                LOG.warn(cde.getMessage());
            }
        }
        return new ArrayList<PluginDescriptor>(checked.values());
    }

    public PluginDescriptor[] getPluginDescriptors() {
        return fRegisteredPlugins.toArray(new PluginDescriptor[fRegisteredPlugins.size()]);
    }

    public PluginDescriptor getPluginDescriptor(String pPluginId) {
        for (PluginDescriptor descriptor : fRegisteredPlugins) {
            if (descriptor.getPluginId().equals(pPluginId)) {
                return descriptor;
            }
        }
        return null;
    }

    public ExtensionPoint getExtensionPoint(String pXpId) {
        return this.fExtensionPoints.get(pXpId);
    }

    public Plugin getPluginInstance(PluginDescriptor pDescriptor) throws PluginRuntimeException {
        if(fActivatedPlugins.containsKey(pDescriptor.getPluginId())) {
            return fActivatedPlugins.get(pDescriptor.getPluginId());
        }
        try {
            synchronized (pDescriptor) {
                Class<?> pluginClass = getCachedClass(pDescriptor,pDescriptor.getPluginClass());
                Constructor<?> constructor = pluginClass.getConstructor(new Class<?>[] {PluginDescriptor.class,Configuration.class});
                Plugin plugin = (Plugin) constructor.newInstance(new Object[] {pDescriptor,this.conf});
                plugin.startUp();
                fActivatedPlugins.put(pDescriptor.getPluginId(),plugin);
                return plugin;
            }
        } catch (ClassNotFoundException e){
            throw new PluginRuntimeException(e);
        }catch (InstantiationException e){
            throw new PluginRuntimeException(e);
        }catch (IllegalAccessException e){
            throw new PluginRuntimeException(e);
        } catch (NoSuchMethodException e){
            throw new PluginRuntimeException(e);
        } catch (InvocationTargetException e){
            throw new PluginRuntimeException(e);
        }
    }

    public void finalize() throws Throwable {
        shutDownActivatedPlugins();
    }

    private void shutDownActivatedPlugins() throws PluginRuntimeException {
        for (Plugin plugin : fActivatedPlugins.values()) {
            plugin.shutDown();
        }
    }

    public Class getCachedClass(PluginDescriptor pDescriptor,String className) throws ClassNotFoundException {
        Map<PluginClassLoader,Class> descMap = CLASS_CACHE.get(className);
        if(descMap == null) {
            descMap = new HashMap<PluginClassLoader,Class>();
            CLASS_CACHE.put(className,descMap);
        }
        PluginClassLoader loader = pDescriptor.getClassLoader();
        Class clazz = descMap.get(loader);
        if(clazz == null){
            clazz = loader.loadClass(className);
            descMap.put(loader,clazz);
        }
        return clazz;
    }

    private void displayStatus() {
        LOG.info("Plugin Auto-activation mode: [" + this.auto + "]");
        LOG.info("Registered Plugins:");
        if ((fRegisteredPlugins == null) || (fRegisteredPlugins.size() == 0)) {
            LOG.info("\tNONE");
        } else {
            for (PluginDescriptor plugin : fRegisteredPlugins) {
                LOG.info("\t" + plugin.getName() + " (" + plugin.getPluginId() + ")");
            }
        }
        LOG.info("Registered Extension-Points:");
        if((fExtensionPoints == null) || (fExtensionPoints.size() == 0)) {
            LOG.info("\tNONE");
        } else {
            for (ExtensionPoint ep : fExtensionPoints.values()) {
                LOG.info("\t" + ep.getName() + " (" + ep.getId() + ")");
            }
        }
    }

    private Map<String,PluginDescriptor> filter(Pattern excludes,Pattern includes,Map<String,PluginDescriptor> plugins) {
        Map<String,PluginDescriptor> map = new HashMap<String, PluginDescriptor>();
        if(plugins == null){
            return map;
        }
        for (PluginDescriptor plugin : plugins.values()){
            if(plugin == null){
                continue;
            }
            String id = plugin.getPluginId();
            if(id == null){
                continue;
            }
            if(!includes.matcher(id).matches()){
                LOG.debug("not including: " + id);
                continue;
            }
            if(excludes.matcher(id).matches()){
                LOG.debug("excluding: " + id);
                continue;
            }
            map.put(plugin.getPluginId(),plugin);
        }
       return map;
    }

    public static void main(String[] args) throws Exception {
        if(args.length < 2) {
            System.err.println("Usage: PluginRepository pluginId className [arg1 arg2 ...]");
            return;
        }
        Configuration conf = NutchConfiguration.create();
        PluginRepository repo = new PluginRepository(conf);
        PluginDescriptor d = repo.getPluginDescriptor(args[0]);
        if(d == null){
            System.err.println("Plugin '" + args[0] + "' not present or inactive.");
            return;
        }
        ClassLoader cl = d.getClassLoader();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(args[1],true,cl);
        } catch (Exception e) {
            System.err.println("Could not load the class '" + args[1] + ": " + e.getMessage());
            return;
        }
        Method m = null;
        try {
            m = clazz.getMethod("main",new Class<?>[]{args.getClass()});
        } catch (Exception e) {
            System.err.println("Could not find the 'main[String[]]' method in class " + args[1] + ": " + e.getMessage());
            return;
        }
        String[] subargs = new String[args.length - 2];
        System.arraycopy(args,2,subargs,0,subargs.length);
        m.invoke(null,new Object[]{subargs});
    }
}