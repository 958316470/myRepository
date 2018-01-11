package com.nutch.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MimeTypesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MimeUtil {

    private static final String SEPARATOR = ";";

    private MimeTypes mimeTypes;

    private Tika tika;

    private boolean mimeMagic;

    private static final Logger LOG = LoggerFactory.getLogger(MimeUtil.class.getName());

    public MimeUtil(Configuration conf) {
        tika = new Tika();
        ObjectCache objectCache = ObjectCache.get(conf);
        MimeTypes mimeTypez = (MimeTypes) objectCache.getObject(MimeType.class.getName());
        if (mimeTypez == null) {
            try {
                String customMimeTypeFile = conf.get("mime.types.file");
                if (customMimeTypeFile != null && customMimeTypeFile.equals("") == false) {
                    try {
                        mimeTypez = MimeTypesFactory.create(conf.getConfResourceAsInputStream(customMimeTypeFile));
                    } catch (Exception e) {
                        LOG.error("Can't load mime.types.file : " + customMimeTypeFile + " using Tika's default");
                    }
                }
                if (mimeTypez == null) {
                    mimeTypez = MimeTypes.getDefaultMimeTypes();
                }
            }catch (Exception e) {
                LOG.error("Exception in MimeUtil " + e.getMessage());
                throw new RuntimeException(e);
            }
            objectCache.setObject(MimeTypes.class.getName(), mimeTypez);
        }
        this.mimeTypes = mimeTypez;
        this.mimeMagic = conf.getBoolean("mime.type.magic", true);
    }

    public static String cleanMimeType(String origType) {
        if (origType == null) {
            return null;
        }
        String[] tokenizedMimeType = origType.split(SEPARATOR);
        if(tokenizedMimeType.length > 1) {
            return tokenizedMimeType[0];
        } else {
            return origType;
        }
    }

    public String autoResolveContentType(String typeName, String url, byte[] data) {
        String retType = null;
        MimeType type = null;
        String cleanMimeType = null;
        cleanMimeType = MimeUtil.cleanMimeType(typeName);
        if(cleanMimeType != null) {
            try {
                type = mimeTypes.forName(cleanMimeType);
                cleanMimeType = type.getName();
            } catch (MimeTypeException mte) {
                cleanMimeType = null;
            }
        }
        if(type == null || (type != null && type.getName().equals(MimeTypes.OCTET_STREAM))) {

            try {
                retType = tika.detect(url) != null ? tika.detect(url) : null;
            } catch (Exception e) {
                String message = "Problem loading default Tika configuration";
                LOG.error(message, e);
                throw new RuntimeException(e);
            }
        } else {
            retType = type.getName();
        }

        if (this.mimeMagic) {
            String magicType = null;
            Metadata tikaMeta = new Metadata();
            tikaMeta.add(Metadata.RESOURCE_NAME_KEY, url);
            tikaMeta.add(Metadata.CONTENT_TYPE, (cleanMimeType != null ? cleanMimeType : typeName));
            try {
                InputStream stream = TikaInputStream.get(data);
                try {
                    magicType = tika.detect(stream, tikaMeta);
                }finally {
                    stream.close();
                }
            }catch (IOException e){
            }

            if(magicType != null && !magicType.equals(MimeTypes.OCTET_STREAM)
                    && !magicType.equals(MimeTypes.PLAIN_TEXT) && retType != null
                    && !retType.equals(magicType)) {
                retType = magicType;
            }
            if (retType == null) {
                try {
                    retType = MimeTypes.OCTET_STREAM;
                }catch (Exception e) {

                }
            }
        }
        return retType;
    }

    public String getMimeType(String url) {
        return tika.detect(url);
    }

    public String forName(String name) {
        try {
            return this.mimeTypes.forName(name).toString();
        } catch (MimeTypeException e) {
            LOG.error("Exception getting mime type by name: [" + name + "]: message: " + e.getMessage());
            return null;
        }
    }

    public String getMimeType(File f) {
        try {
            return tika.detect(f);
        } catch (Exception e) {
            LOG.error("Exception getting mime type for file: [" + f.getPath() + "]: message: " + e.getMessage());
            return null;
        }
    }
}
