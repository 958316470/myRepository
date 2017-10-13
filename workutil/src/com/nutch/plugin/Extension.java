package com.nutch.plugin;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;

import java.util.HashMap;

/**
 * @author 95831
 */
public class Extension {

    private String fId;

    private String fTargetPoint;

    private String fClazz;

    private HashMap<String, String> fAttributes;

    private Configuration conf;

    private PluginDescriptor fDescriptor;

    public Extension(PluginDescriptor pDescriptor, String pExtensionPoint,
                     String pId, String pExtensionClass, Configuration conf, PluginRepository pluginRepository) {
        fAttributes = new HashMap<String, String>();
        setDescriptor(pDescriptor);
        setExtensionPoint(pExtensionPoint);
        setId(pId);
        setClazz(pExtensionClass);
        this.conf = conf;

    }

    private void setExtensionPoint(String point) {
        fTargetPoint = point;
    }

    public String getAttribute(String pKey) {
        return fAttributes.get(pKey);
    }

    public String getClazz() {
        return fClazz;
    }

    public String getId() {
        return fId;
    }

    public void addAttribute(String pKey, String pValue) {
        fAttributes.put(pKey, pValue);
    }

    public void setClazz(String extensionClazz) {
        fClazz = extensionClazz;
    }

    public void setId(String extensionID) {
        fId = extensionID;
    }

    public String getTargetPoint() {
        return fTargetPoint;
    }

    public Object getExtensionInstance() throws PluginRuntimeException {
        synchronized (getId()) {
            try {
                PluginRepository pluginRepository = PluginRepository.get(conf);
                Class extensionClazz = pluginRepository.getCachedClass(fDescriptor, getClazz());
                pluginRepository.getPluginInstance(getDescriptor());
                Object object = extensionClazz.newInstance();
                if (object instanceof Configurable) {
                    ((Configurable) object).setConf(this.conf);
                }
                return object;
            } catch (ClassNotFoundException e) {
                throw new PluginRuntimeException(e);
            } catch (InstantiationException e) {
                throw new PluginRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new PluginRuntimeException(e);
            }
        }
    }

    public PluginDescriptor getDescriptor() {
        return fDescriptor;
    }

    public void setDescriptor(PluginDescriptor pDescriptor) {
        fDescriptor = pDescriptor;
    }
}
