package com.nutch.metadata;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Metadata implements Writable, CreativeCommons, DublinCore, HttpHeaders, Nutch, Feed {

    private Map<String, String[]> metadata = null;

    public Metadata() {
        metadata = new HashMap<String, String[]>();
    }

    public boolean isMultiValued(final String name) {
        return metadata.get(name) != null && metadata.get(name).length > 1;
    }

    public String[] names() {
        return metadata.keySet().toArray(new String[metadata.keySet().size()]);
    }

    public String get(final String name) {
        String[] values = metadata.get(name);
        if (values == null) {
            return null;
        } else {
            return values[0];
        }
    }

    public String[] getValues(final String name) {
        return _getValues(name);
    }

    private String[] _getValues(final String name) {
        String[] values = metadata.get(name);
        if (values == null) {
            values = new String[0];
        }
        return values;
    }

    public void add(final String name, final String value) {
        String[] values = metadata.get(name);
        if (values == null) {
            set(name, value);
        } else {
            String[] newValues = new String[values.length + 1];
            System.arraycopy(values, 0, newValues, 0, values.length);
            newValues[newValues.length - 1] = value;
            metadata.put(name, newValues);
        }
    }

    public void setAll(Properties properties) {
        Enumeration<?> names = properties.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            metadata.put(name, new String[]{properties.getProperty(name)});
        }
    }

    public void set(String name, String value) {
        metadata.put(name, new String[]{value});
    }

    public void remove(String name) {
        metadata.remove(name);
    }

    public int size() {
        return metadata.size();
    }

    public void clear() {
        metadata.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        Metadata other = null;
        try {
            other = (Metadata) o;
        } catch (ClassCastException e) {
            return false;
        }

        if (other.size() != size()) {
            return false;
        }
        String[] names = names();
        for (int i = 0; i < names.length; i++) {
            String[] otherValues = other._getValues(names[i]);
            String[] thisValues = _getValues(names[i]);
            if (otherValues.length != thisValues.length) {
                return false;
            }
            for (int j = 0; j < otherValues.length; j++ ){
                if (!otherValues[j].equals(thisValues[j])) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString(){
        StringBuffer buf = new StringBuffer();
        String[] names = names();
        for (int i = 0; i < names.length; i++) {
            String[] values = _getValues(names[i]);
            for (int j = 0; j < values.length; j++) {
                buf.append(names[i]).append("=").append(values[j]).append(" ");
            }
        }
        return buf.toString();
    }

    @Override
    public final void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(size());
        String [] values = null;
        String[] names = names();
        for (int i = 0;i < names.length; i++) {
            Text.writeString(dataOutput,names[i]);
            values = _getValues(names[i]);
            int cnt = 0;
            for (int j = 0; j < values.length; j++) {
                if (values[j] != null) {
                    cnt++;
                }
            }
            dataOutput.writeInt(cnt);
            for (int j = 0; j < values.length; j++) {
                if (values[j] != null) {
                    Text.writeString(dataOutput, values[j]);
                }
            }
        }
    }

    @Override
    public final void readFields(DataInput dataInput) throws IOException {
        int keySize = dataInput.readInt();
        String key;
        for (int i = 0; i < keySize; i++ ) {
            key = Text.readString(dataInput);
            int valueSize = dataInput.readInt();
            for (int j = 0; j < valueSize; j++) {
                add(key,Text.readString(dataInput));
            }
        }
    }
}
