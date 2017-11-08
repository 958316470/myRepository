package com.nutch.storage;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.data.RecordBuilder;
import org.apache.avro.specific.SpecificRecordBuilderBase;
import org.apache.gora.persistency.Dirtyable;
import org.apache.gora.persistency.Persistent;
import org.apache.gora.persistency.impl.DirtyMapWrapper;

import java.nio.ByteBuffer;
import java.util.Map;

public class Host extends org.apache.gora.persistency.impl.PersistentBase implements org.apache.avro.specific.SpecificRecord,
        org.apache.gora.persistency.Persistent {
    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser()
            .parse("{\"type\":\"record\",\"name\":\"Host\",\"namespace\":\"org.apache.nutch.storage\",\"doc\":\"Host represents a store of webpages or other data which resides on a server or other computer so that it can be accessed over the Internet\",\"fields\":[{\"name\":\"metadata\",\"type\":{\"type\":\"map\",\"values\":[\"null\",\"bytes\"]},\"doc\":\"A multivalued metadata container used for storing a wide variety of host metadata such as structured web server characterists etc\",\"default\":{}},{\"name\":\"outlinks\",\"type\":{\"type\":\"map\",\"values\":[\"null\",\"string\"]},\"doc\":\"Hyperlinks which direct outside of the current host domain these can used in a histogram style manner to generate host statistics\",\"default\":{}},{\"name\":\"inlinks\",\"type\":{\"type\":\"map\",\"values\":[\"null\",\"string\"]},\"doc\":\"Hyperlinks which link to pages within the current host domain these can used in a histogram style manner to generate host statistics\",\"default\":{}}]}");


    public static enum Field {
        METADATA(0, "metadata"), OUTLINKS(1, "outlinks"), INLINKS(2, "inlinks");

        private int index;
        private String name;

        Field(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    ;
    public static final String[] _All_FIELDS = {"metadata", "outlinks", "inlinks"};

    @Override
    public int getFieldsCount() {
        return Host._All_FIELDS.length;
    }

    private Map<CharSequence, ByteBuffer> metadata;
    private Map<CharSequence, CharSequence> outlinks;
    private Map<CharSequence, CharSequence> inlinks;

    @Override
    public Schema getSchema() {
        return SCHEMA$;
    }

    @Override
    public Object get(int field$) {
        switch (field$) {
            case 0:
                return metadata;
            case 1:
                return outlinks;
            case 2:
                return inlinks;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }

    @Override
    public void put(int field$, Object value) {
        switch (field$) {
            case 0:
                metadata = (Map<CharSequence, ByteBuffer>) ((value instanceof Dirtyable) ? value : new DirtyMapWrapper((Map) value));
                break;
            case 1:
                outlinks = (Map<CharSequence, CharSequence>) ((value instanceof Dirtyable) ? value : new DirtyMapWrapper((Map) value));
                break;
            case 2:
                inlinks = (Map<CharSequence, CharSequence>) ((value instanceof Dirtyable) ? value : new DirtyMapWrapper((Map) value));
                break;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }

    public Map<CharSequence, ByteBuffer> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<CharSequence, ByteBuffer> metadata) {
        this.metadata = (metadata instanceof org.apache.gora.persistency.Dirtyable) ? metadata
                : new org.apache.gora.persistency.impl.DirtyMapWrapper(metadata);
        setDirty(0);
    }

    public boolean isMetadataDirty(Map<CharSequence, ByteBuffer> metadata) {
        return isDirty(0);
    }

    public Map<CharSequence, CharSequence> getOutlinks() {
        return outlinks;
    }

    public void setOutlinks(Map<CharSequence, CharSequence> outlinks) {
        this.outlinks = (outlinks instanceof org.apache.gora.persistency.Dirtyable) ? outlinks
                : new org.apache.gora.persistency.impl.DirtyMapWrapper(outlinks);
        setDirty(1);
    }

    public boolean isOutlinksDirty(Map<CharSequence, CharSequence> outlinks) {
        return isDirty(1);
    }

    public Map<CharSequence, CharSequence> getInlinks() {
        return inlinks;
    }

    public void setInlinks(Map<CharSequence, CharSequence> inlinks) {
        this.inlinks = (inlinks instanceof org.apache.gora.persistency.Dirtyable) ? inlinks
                : new org.apache.gora.persistency.impl.DirtyMapWrapper(inlinks);
        setDirty(2);
    }

    public boolean isInlinksDirty(Map<CharSequence, CharSequence> inlinks) {
        return isDirty(2);
    }

    public static Host.Builder newBuilder() {
        return new Builder();
    }

    public static Host.Builder newBuilder(Host.Builder other) {
        return new Host.Builder(other);
    }

    public static Host.Builder newBuilder(Host other) {
        return new Host.Builder(other);
    }

    public static class Builder extends SpecificRecordBuilderBase<Host> implements RecordBuilder<Host> {
        private Map<CharSequence, ByteBuffer> metadata;
        private Map<CharSequence, CharSequence> outlinks;
        private Map<CharSequence, CharSequence> inlinks;

        private Builder() {
            super(Host.SCHEMA$);
        }

        private Builder(Host.Builder other) {
            super(other);
        }

        private Builder(Host other) {
            super(SCHEMA$);
            if (isValidValue(fields()[0], other.metadata)) {
                this.metadata = (Map<CharSequence, ByteBuffer>) data().deepCopy(fields()[0].schema(), other.metadata);
                fieldSetFlags()[0] = true;
            }

            if (isValidValue(fields()[1], other.outlinks)) {
                this.outlinks = (Map<CharSequence, CharSequence>) data().deepCopy(fields()[1].schema(), other.outlinks);
                fieldSetFlags()[1] = true;
            }

            if (isValidValue(fields()[2], other.inlinks)) {
                this.inlinks = (Map<CharSequence, CharSequence>) data().deepCopy(fields()[2].schema(), other.inlinks);
                fieldSetFlags()[2] = true;
            }

        }

        public Map<CharSequence, ByteBuffer> getMetadata() {
            return metadata;
        }

        public com.nutch.storage.Host.Builder setMetadata(Map<CharSequence, ByteBuffer> metadata) {
            validate(fields()[0], metadata);
            this.metadata = metadata;
            fieldSetFlags()[0] = true;
            return this;
        }

        public boolean hasMetadata() {
            return fieldSetFlags()[0];
        }

        public com.nutch.storage.Host.Builder clearMetadata() {
            metadata = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        public Map<CharSequence, CharSequence> getOutlinks() {
            return outlinks;
        }

        public com.nutch.storage.Host.Builder setOutlinks(Map<CharSequence, CharSequence> outlinks) {
            validate(fields()[1], outlinks);
            this.outlinks = outlinks;
            fieldSetFlags()[1] = true;
            return this;
        }

        public boolean hasOutlinks() {
            return fieldSetFlags()[1];
        }

        public com.nutch.storage.Host.Builder clearOutlinks() {
            outlinks = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        public Map<CharSequence, CharSequence> getInlinks() {
            return inlinks;
        }

        public com.nutch.storage.Host.Builder setInlinks(Map<CharSequence, CharSequence> inlinks) {
            validate(fields()[2], inlinks);
            this.inlinks = inlinks;
            fieldSetFlags()[2] = true;
            return this;
        }

        public boolean hasInlinks() {
            return fieldSetFlags()[2];
        }

        public com.nutch.storage.Host.Builder clearInlinks() {
            inlinks = null;
            fieldSetFlags()[2] = false;
            return this;
        }

        @Override
        public Host build() {
            try {
                Host record = new Host();
                record.metadata = fieldSetFlags()[0] ? this.metadata : (Map<CharSequence, ByteBuffer>) new DirtyMapWrapper((Map) defaultValue(fields()[0]));
                record.outlinks = fieldSetFlags()[1] ? this.outlinks : (Map<CharSequence, CharSequence>) new DirtyMapWrapper((Map) defaultValue(fields()[1]));
                record.inlinks = fieldSetFlags()[2] ? this.inlinks : (Map<CharSequence, CharSequence>) new DirtyMapWrapper((Map) defaultValue(fields()[2]));
                return record;
            } catch (Exception e) {
                throw new AvroRuntimeException(e);
            }
        }
    }

    @Override
    public Tombstone getTombstone() {
        return TOMBSTONE;
    }

    @Override
    public Persistent newInstance() {
        return newBuilder().build();
    }

    private static final Tombstone TOMBSTONE = new Tombstone();

    public static final class Tombstone extends Host implements org.apache.gora.persistency.Tombstone {
        private Tombstone() {
        }

        @Override
        public Map<CharSequence, ByteBuffer> getMetadata() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setMetadata(Map<CharSequence, ByteBuffer> value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isMetadataDirty(Map<CharSequence, ByteBuffer> value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }

        @Override
        public Map<CharSequence, CharSequence> getOutlinks() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setOutlinks(Map<CharSequence, CharSequence> value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isOutlinksDirty(Map<CharSequence, CharSequence> value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }

        @Override
        public Map<CharSequence, CharSequence> getInlinks() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setInlinks(Map<CharSequence, CharSequence> value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isInlinksDirty(Map<CharSequence, CharSequence> value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }

    }
}