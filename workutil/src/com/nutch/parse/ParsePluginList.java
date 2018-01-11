package com.nutch.parse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsePluginList {

    private Map<String, List<String>> fMimeTypeToPluginMap = null;

    private Map<String, String> aliases = null;

    ParsePluginList() {
        fMimeTypeToPluginMap = new HashMap<String, List<String>>();
        aliases = new HashMap<String, String>();
    }

    public List<String> getPluginList(String mimeType) {
        return fMimeTypeToPluginMap.get(mimeType);
    }
    void setAliases(Map<String, String> aliases) {
        this.aliases = aliases;
    }

    public Map<String, String> getAliases() {
        return aliases;
    }
    void setPluginList(String mimeType, List<String> l){
        fMimeTypeToPluginMap.put(mimeType, l);
    }
    List<String> getSupportedMimeTypes() {
        return Arrays.asList(fMimeTypeToPluginMap.keySet().toArray(new String[] {}));
    }
}
