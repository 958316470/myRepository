package com.nutch.protocol;

import com.nutch.metadata.Metadata;
import com.nutch.util.MimeUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VersionMismatchException;
import org.apache.hadoop.io.Writable;

import java.io.*;
import java.util.zip.InflaterInputStream;

public class Content implements Writable {
    public static final String DIR_NAME = "content";
    private final static int VERSION = -1;
    private int version;
    private String url;
    private String base;
    private byte[] content;
    private String contentType;
    private Metadata metadata;
    private MimeUtil mineTypes;

    public Content() {
        this.metadata = new Metadata();
    }
    public Content(String url, String base, byte[] content, String contentType, Metadata metadata, Configuration conf) {
        if (url == null) {
            throw new IllegalArgumentException("null url");
        }
        if (base == null) {
            throw new IllegalArgumentException("null base");
        }
        if (content == null) {
            throw new IllegalArgumentException("null content");
        }
        if (metadata == null) {
            throw new IllegalArgumentException("null metadata");
        }
        this.url = url;
        this.base = base;
        this.content = content;
        this.metadata = metadata;
        this.mineTypes = new MimeUtil(conf);
        this.contentType = getContentType(contentType, url, content);

    }

    public Content(String url, String base, byte[] content, String contentType, Metadata metadata, MimeUtil mineTypes) {
        if (url == null) {
            throw new IllegalArgumentException("null url");
        }
        if (base == null) {
            throw new IllegalArgumentException("null base");
        }
        if (content == null) {
            throw new IllegalArgumentException("null content");
        }
        if (metadata == null) {
            throw new IllegalArgumentException("null metadata");
        }
        this.url = url;
        this.base = base;
        this.content = content;
        this.metadata = metadata;
        this.mineTypes = mineTypes;
        this.contentType = getContentType(contentType, url, content);

    }
    private final void readFieldsCompressed(DataInput in) throws IOException {
        byte oldVersion = in.readByte();
        switch (oldVersion) {
            case 0:
            case 1:
                url = Text.readString(in);
                base = Text.readString(in);
                content = new byte[in.readInt()];
                in.readFully(content);
                contentType = Text.readString(in);
                int keySize = in.readInt();
                String key;
                for (int i = 0; i < keySize; i++) {
                    key = Text.readString(in);
                    int valueSize = in.readInt();
                    for (int j = 0; j < valueSize; j++) {
                        metadata.add(key, Text.readString(in));
                    }
                }
                break;
            case 2:
                url = Text.readString(in);
                base = Text.readString(in);
                content = new byte[in.readInt()];
                in.readFully(content);
                contentType = Text.readString(in);
                metadata.readFields(in);
                break;
                default:
                    throw new VersionMismatchException((byte) 2, oldVersion);
        }
    }


    @Override
    public final void write(DataOutput out) throws IOException {
        out.writeInt(VERSION);
        Text.writeString(out, url);
        Text.writeString(out,base);
        out.writeInt(content.length);
        out.write(content);
        Text.writeString(out, contentType);
        metadata.write(out);
    }

    @Override
    public final void readFields(DataInput in) throws IOException {
        metadata.clear();
        int sizeOrVersion = in.readInt();
        if (sizeOrVersion < 0) {
            version = sizeOrVersion;
            switch (version) {
                case VERSION:
                    url = Text.readString(in);
                    base = Text.readString(in);
                    content = new byte[in.readInt()];
                    in.readFully(content);
                    contentType = Text.readString(in);
                    metadata.readFields(in);
                    break;
                    default:
                        throw new VersionMismatchException((byte) VERSION, (byte) version);
            }
        } else {
            byte[] compressed = new byte[sizeOrVersion];
            in.readFully(compressed, 0, compressed.length);
            ByteArrayInputStream deflated = new ByteArrayInputStream(compressed);
            DataInput inflater = new DataInputStream(new InflaterInputStream(deflated));
            readFieldsCompressed(inflater);
        }
    }

    public static Content read(DataInput in) throws IOException {
        Content content = new Content();
        content.readFields(in);
        return content;
    }

    public String getUrl() {
        return url;
    }

    public String getBaseUrl() {
        return base;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }


}
