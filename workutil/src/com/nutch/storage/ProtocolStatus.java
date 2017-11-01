package com.nutch.storage;

import com.nutch.protocol.ProtocolStatusUtils;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.data.RecordBuilder;
import org.apache.avro.specific.SpecificRecordBuilderBase;
import org.apache.gora.persistency.Dirtyable;
import org.apache.gora.persistency.Persistent;
import org.apache.gora.persistency.impl.DirtyListWrapper;

import java.util.List;

public class ProtocolStatus extends org.apache.gora.persistency.impl.PersistentBase implements org.apache.avro.specific.SpecificRecord,
    org.apache.gora.persistency.Persistent{
    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser()
            .parse("{\"type\":\"record\",\"name\":\"ProtocolStatus\",\"namespace\":\"com.nutch.storage\",\"doc\":\"A nested container representing data captured from web server responses.\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"doc\":\"A protocol response code which can be one of SUCCESS - content was retrieved without errors, FAILED - Content was not retrieved. Any further errors may be indicated in args, PROTO_NOT_FOUND - This protocol was not found. Application may attempt to retry later, GONE - Resource is gone, MOVED - Resource has moved permanently. New url should be found in args, TEMP_MOVED - Resource has moved temporarily. New url should be found in args., NOTFOUND - Resource was not found, RETRY - Temporary failure. Application may retry immediately., EXCEPTION - Unspecified exception occured. Further information may be provided in args., ACCESS_DENIED - Access denied - authorization required, but missing/incorrect., ROBOTS_DENIED - Access denied by robots.txt rules., REDIR_EXCEEDED - Too many redirects., NOTFETCHING - Not fetching., NOTMODIFIED - Unchanged since the last fetch., WOULDBLOCK - Request was refused by protocol plugins, because it would block. The expected number of milliseconds to wait before retry may be provided in args., BLOCKED - Thread was blocked http.max.delays times during fetching.\",\"default\":0},{\"name\":\"args\",\"type\":{\"type\":\"array\",\"items\":\"string\"},\"doc\":\"Optional arguments supplied to compliment and/or justify the response code.\",\"default\":[]},{\"name\":\"lastModified\",\"type\":\"long\",\"doc\":\"A server reponse indicating when this page was last modified, this can be unreliable at times hence this is used as a default fall back value for the preferred 'modifiedTime' and 'preModifiedTime' obtained from the WebPage itself.\",\"default\":0}]}");

    public static enum Field {
        CODE(0,"code"),ARGS(1,"args"),LAST_MODIFIED(2,"lastModified");
        private int index;
        private String name;
        Field(int index,String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }
        public String getName(){
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    };
    public static final String[] _All_FIELDS = {"code","args","lastModified"};
    @Override
    public int getFieldsCount() {
        return ProtocolStatus._All_FIELDS.length;
    }

    private int code;
    private List<CharSequence> args;
    private long lastModified;

    @Override
    public Schema getSchema() {
        return SCHEMA$;
    }

    @Override
    public Object get(int field$) {
        switch (field$) {
            case 0:
                return code;
            case 1:
                return args;
            case 2:
                return lastModified;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }

    @Override
    public void put(int field$, Object value) {
        switch (field$) {
            case 0:
                code = (Integer) value;
                break;
            case 1:
                args = (List<CharSequence>) ((value instanceof Dirtyable) ? value : new DirtyListWrapper((List)value));
                break;
            case 2:
                lastModified = (Long) value;
                break;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
        setDirty(0);
    }

    public boolean isCodeDirty(Integer value) {
        return isDirty(0);
    }

    public List<CharSequence> getArgs() {
        return args;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setArgs(List<CharSequence> args) {
        this.args = (args instanceof Dirtyable) ? args : new DirtyListWrapper(args);
        setDirty(1);
    }

    public boolean isArgsDirty(List<CharSequence> value) {
        return isDirty(1);
    }


    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
        setDirty(2);
    }
    public boolean isLastModifiedDirty(Long lastModified){
        return isDirty(2);
    }

    public static ProtocolStatus.Builder newBuilder() {
        return new Builder();
    }

    public static ProtocolStatus.Builder newBuilder(ProtocolStatus.Builder other) {
        return new ProtocolStatus.Builder(other);
    }

    public static ProtocolStatus.Builder newBuilder(ProtocolStatus other){
        return new ProtocolStatus.Builder(other);
    }



    public static class Builder extends SpecificRecordBuilderBase<ProtocolStatus> implements RecordBuilder<ProtocolStatus> {
        private int code;
        private List<CharSequence> args;
        private long lastModified;

        private Builder(){
            super(ProtocolStatus.SCHEMA$);
        }

        private Builder(ProtocolStatus.Builder other) {
            super(other);
        }

        private Builder(ProtocolStatus other) {
            super(SCHEMA$);
            if(isValidValue(fields()[0],other.code)) {
                this.code = (Integer) data().deepCopy(fields()[0].schema(),other.code);
                fieldSetFlags()[0] = true;
            }
            if(isValidValue(fields()[1],other.args)) {
                this.args = (List<CharSequence>) data().deepCopy(fields()[1].schema(),other.args);
                fieldSetFlags()[1] = true;
            }
            if(isValidValue(fields()[2],other.lastModified)) {
                this.lastModified = (Long) data().deepCopy(fields()[2].schema(),other.lastModified);
                fieldSetFlags()[2] = true;
            }
        }
        public Integer getCode() {
            return code;
        }

        public com.nutch.storage.ProtocolStatus.Builder setCode(Integer code) {
            validate(fields()[0], code);
            this.code = code;
            fieldSetFlags()[0] = true;
            return this;
        }

        public boolean hasCode() {
            return fieldSetFlags()[0];
        }

        public com.nutch.storage.ProtocolStatus.Builder clearCode() {
            fieldSetFlags()[0] = false;
            return this;
        }


        public List<CharSequence> getArgs() {
            return args;
        }

        public com.nutch.storage.ProtocolStatus.Builder setArgs(List<CharSequence> args) {
            validate(fields()[1], args);
            this.args = args;
            fieldSetFlags()[1] = true;
            return this;
        }

        public boolean hasArgs() {
            return fieldSetFlags()[1];
        }

        public com.nutch.storage.ProtocolStatus.Builder clearArgs() {
            args = null;
            fieldSetFlags()[1] = false;
            return this;
        }


        public Long getLastModified() {
            return lastModified;
        }

        public com.nutch.storage.ProtocolStatus.Builder setLastModified(Long lastModified) {
            validate(fields()[2], lastModified);
            this.lastModified = lastModified;
            fieldSetFlags()[2] = true;
            return this;
        }

        public boolean hasLastModified() {
            return fieldSetFlags()[2];
        }

        public com.nutch.storage.ProtocolStatus.Builder clearLastModified() {
            fieldSetFlags()[2] = false;
            return this;
        }

        @Override
        public ProtocolStatus build() {
           try {
               ProtocolStatus record = new ProtocolStatus();
               record.code = fieldSetFlags()[0] ? this.code : (Integer)defaultValue(fields()[0]);
               record.args = fieldSetFlags()[1] ? this.args :(List<CharSequence>) new DirtyListWrapper((List)defaultValue(fields()[1]));
               record.lastModified = fieldSetFlags()[2] ? this.lastModified : (Long)defaultValue(fields()[2]);
               return record;
           }catch (Exception e) {
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

    public boolean isSuccess() {
        return code == ProtocolStatusUtils.SUCCESS;
    }

    private static final Tombstone TOMBSTONE = new Tombstone();

    public static final class Tombstone extends ProtocolStatus implements org.apache.gora.persistency.Tombstone {
        private Tombstone() {}
        @Override
        public Integer getCode() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setCode(Integer value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isCodeDirty(Integer value){
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public List<CharSequence> getArgs() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setArgs(List<CharSequence> value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isArgsDirty(List<CharSequence> value){
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Long getLastModified() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setLastModified(Long value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isLastModifiedDirty(Long value){
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }
    }
}
