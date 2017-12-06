package com.nutch.storage;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.data.RecordBuilder;
import org.apache.avro.specific.SpecificRecordBuilderBase;
import org.apache.gora.persistency.Dirtyable;
import org.apache.gora.persistency.Persistent;
import org.apache.gora.persistency.impl.DirtyListWrapper;

import java.util.List;

public class ParseStatus extends org.apache.gora.persistency.impl.PersistentBase implements org.apache.avro.specific.SpecificRecord,
        org.apache.gora.persistency.Persistent {
    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser()
            .parse("{\"type\":\"record\",\"name\":\"ParseStatus\",\"namespace\":\"com.nutch.storage\",\"doc\":\"A nested container representing parse status data captured from invocation of parsers on fetch of a WebPage\",\"fields\":[{\"name\":\"majorCode\",\"type\":\"int\",\"doc\":\"Major parsing status' including NOTPARSED (Parsing was not performed), SUCCESS (Parsing succeeded), FAILED (General failure. There may be a more specific error message in arguments.)\",\"default\":0},{\"name\":\"minorCode\",\"type\":\"int\",\"doc\":\"Minor parsing status' including SUCCESS_OK - Successful parse devoid of anomalies or issues, SUCCESS_REDIRECT - Parsed content contains a directive to redirect to another URL. The target URL can be retrieved from the arguments., FAILED_EXCEPTION - Parsing failed. An Exception occured which may be retrieved from the arguments., FAILED_TRUNCATED - Parsing failed. Content was truncated, but the parser cannot handle incomplete content., FAILED_INVALID_FORMAT - Parsing failed. Invalid format e.g. the content may be corrupted or of wrong type., FAILED_MISSING_PARTS - Parsing failed. Other related parts of the content are needed to complete parsing. The list of URLs to missing parts may be provided in arguments. The Fetcher may decide to fetch these parts at once, then put them into Content.metadata, and supply them for re-parsing., FAILED_MISING_CONTENT - Parsing failed. There was no content to be parsed - probably caused by errors at protocol stage.\",\"default\":0},{\"name\":\"args\",\"type\":{\"type\":\"array\",\"items\":\"string\"},\"doc\":\"Optional arguments supplied to compliment and/or justify the parse status code.\",\"default\":[]}]}");


    public static enum Field {
        MAJORCODE(0, "majorCode"),
        MINORCODE(1, "minorCode"), ARGS(2, "args");
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
    public static final String[] _All_FIELDS = {"majorCode", "minorCode", "args"};

    @Override
    public int getFieldsCount() {
        return ParseStatus._All_FIELDS.length;
    }

    private int majorCode;
    private int minorCode;
    private List<CharSequence> args;

    @Override
    public Schema getSchema() {
        return SCHEMA$;
    }

    @Override
    public Object get(int field$) {
        switch (field$) {
            case 0:
                return majorCode;
            case 1:
                return minorCode;
            case 2:
                return args;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }

    @Override
    public void put(int field$, Object value) {
        switch (field$) {
            case 0:
                majorCode = (Integer) value;
                break;
            case 1:
                minorCode = (Integer) value;
                break;
            case 2:
                args = (List<CharSequence>) ((value instanceof Dirtyable) ? value : new DirtyListWrapper((List) value));
                break;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }

    public Integer getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(Integer majorCode) {
        this.majorCode = majorCode;
        setDirty(0);
    }

    public boolean isMajorCodeDirty(Integer majorCode) {
        return isDirty(0);
    }

    public Integer getMinorCode() {
        return minorCode;
    }

    public void setMinorCode(Integer minorCode) {
        this.minorCode = minorCode;
        setDirty(1);
    }

    public boolean isMinorCodeDirty(Integer minorCode) {
        return isDirty(1);
    }

    public List<CharSequence> getArgs() {
        return args;
    }

    public void setArgs(List<CharSequence> args) {
        this.args = (args instanceof org.apache.gora.persistency.Dirtyable) ? args
                : new org.apache.gora.persistency.impl.DirtyListWrapper(args);
        setDirty(2);
    }

    public boolean isArgsDirty(List<CharSequence> args) {
        return isDirty(2);
    }

    public static ParseStatus.Builder newBuilder() {
        return new Builder();
    }

    public static ParseStatus.Builder newBuilder(ParseStatus.Builder other) {
        return new ParseStatus.Builder(other);
    }

    public static ParseStatus.Builder newBuilder(ParseStatus other) {
        return new ParseStatus.Builder(other);
    }

    public static class Builder extends SpecificRecordBuilderBase<ParseStatus> implements RecordBuilder<ParseStatus> {
        private int majorCode;
        private int minorCode;
        private List<CharSequence> args;

        private Builder() {
            super(ParseStatus.SCHEMA$);
        }

        private Builder(ParseStatus.Builder other) {
            super(other);
        }

        private Builder(ParseStatus other) {
            super(SCHEMA$);
            if (isValidValue(fields()[0], other.majorCode)) {
                this.majorCode = (Integer) data().deepCopy(fields()[0].schema(), other.majorCode);
                fieldSetFlags()[0] = true;
            }

            if (isValidValue(fields()[1], other.minorCode)) {
                this.minorCode = (Integer) data().deepCopy(fields()[1].schema(), other.minorCode);
                fieldSetFlags()[1] = true;
            }

            if (isValidValue(fields()[2], other.args)) {
                this.args = (List<CharSequence>) data().deepCopy(fields()[2].schema(), other.args);
                fieldSetFlags()[2] = true;
            }

        }

        public Integer getMajorCode() {
            return majorCode;
        }

        public com.nutch.storage.ParseStatus.Builder setMajorCode(Integer majorCode) {
            validate(fields()[0], majorCode);
            this.majorCode = majorCode;
            fieldSetFlags()[0] = true;
            return this;
        }

        public boolean hasMajorCode() {
            return fieldSetFlags()[0];
        }

        public com.nutch.storage.ParseStatus.Builder clearMajorCode() {
            fieldSetFlags()[0] = false;
            return this;
        }

        public Integer getMinorCode() {
            return minorCode;
        }

        public com.nutch.storage.ParseStatus.Builder setMinorCode(Integer minorCode) {
            validate(fields()[1], minorCode);
            this.minorCode = minorCode;
            fieldSetFlags()[1] = true;
            return this;
        }

        public boolean hasMinorCode() {
            return fieldSetFlags()[1];
        }

        public com.nutch.storage.ParseStatus.Builder clearMinorCode() {
            fieldSetFlags()[1] = false;
            return this;
        }

        public List<CharSequence> getArgs() {
            return args;
        }

        public com.nutch.storage.ParseStatus.Builder setArgs(List<CharSequence> args) {
            validate(fields()[2], args);
            this.args = args;
            fieldSetFlags()[2] = true;
            return this;
        }

        public boolean hasArgs() {
            return fieldSetFlags()[2];
        }

        public com.nutch.storage.ParseStatus.Builder clearArgs() {
            args = null;
            fieldSetFlags()[2] = false;
            return this;
        }

        @Override
        public ParseStatus build() {
            try {
                ParseStatus record = new ParseStatus();
                record.majorCode = fieldSetFlags()[0] ? this.majorCode : (Integer) defaultValue(fields()[0]);
                record.minorCode = fieldSetFlags()[1] ? this.minorCode : (Integer) defaultValue(fields()[1]);
                record.args = fieldSetFlags()[2] ? this.args : (List<CharSequence>) new DirtyListWrapper((List) defaultValue(fields()[2]));
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

    public static final class Tombstone extends ParseStatus implements org.apache.gora.persistency.Tombstone {
        private Tombstone() {
        }

        @Override
        public Integer getMajorCode() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setMajorCode(Integer value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isMajorCodeDirty(Integer value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }

        @Override
        public Integer getMinorCode() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setMinorCode(Integer value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isMinorCodeDirty(Integer value) {
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
        public boolean isArgsDirty(List<CharSequence> value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }

    }
}