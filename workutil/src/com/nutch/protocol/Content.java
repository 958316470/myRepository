package com.nutch.protocol;

import com.nutch.metadata.Metadata;
import com.nutch.util.MimeUtil;
import com.nutch.util.NutchConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VersionMismatchException;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.*;
import java.util.Arrays;
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

    public String getContentType(){
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof  Content)) {
            return false;
        }
        Content that = (Content) o;
        return this.url.equals(that.url) && this.base.equals(that.base)
                && Arrays.equals(this.getContent(), that.getContent())
                && this.contentType.equals(that.contentType)
                && this.metadata.equals(that.metadata);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Version: " + version + "\n");
        buffer.append("url: " + url + "\n");
        buffer.append("base: " + base + "\n");
        buffer.append("contentType: " + contentType + "\n");
        buffer.append("metadate: " + metadata + "\n");
        buffer.append("Content:\n");
        buffer.append(new String(content));
        return buffer.toString();

    }

    public static void main(String[] args) throws Exception{
        String usage = "Content (-local | -dfs <namenode:port>) recno batchId";
        if(args.length < 3) {
            System.out.println("usage: " + usage);
            return;
        }
        GenericOptionsParser optionsParser = new GenericOptionsParser(NutchConfiguration.create(), args);
        String[] argv = optionsParser.getRemainingArgs();
        Configuration conf = optionsParser.getConfiguration();
        FileSystem fs = FileSystem.get(conf);
        try {
            int recno = Integer.parseInt(argv[0]);
            String batchId = argv[1];
            Path file = new Path(batchId, DIR_NAME);
            System.out.println("Reading from file: " + file);
            ArrayFile.Reader contents = new ArrayFile.Reader(fs, file.toString(), conf);
            Content content = new Content();
            contents.get(recno, content);
            System.out.println("Retrieved " + recno + " from file " + file);
            System.out.println(content);
            contents.close();
        } finally {
            fs.close();
        }
    }

    private String getContentType(String typeName, String url, byte[] data) {
        return this.mineTypes.autoResolveContentType(typeName, url, data);
    }
}
