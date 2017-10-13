package com.nutch.plugin;

import java.util.ArrayList;

/**
 * 此类提供表达式
 *
 * @author 95831
 */
public class ExtensionPoint {

    private String ftId;

    private String fName;

    private String fSchema;

    private ArrayList<Extension> fExtensions;

    public ExtensionPoint(String pId, String pName, String pSchema) {
        setId(pId);
        setName(pName);
        setSchema(pSchema);
        fExtensions = new ArrayList<Extension>();
    }

    public String getId() {
        return ftId;
    }

    public void setId(String pId) {
        this.ftId = pId;
    }

    public String getName() {
        return fName;
    }

    public void setName(String pName) {
        this.fName = pName;
    }

    public String getfSchema() {
        return fSchema;
    }

    public void setSchema(String pSchema) {
        this.fSchema = pSchema;
    }

    public void addExtension(Extension extension) {
        fExtensions.add(extension);
    }

    public Extension[] getExtensions() {
        return fExtensions.toArray(new Extension[fExtensions.size()]);
    }
}
