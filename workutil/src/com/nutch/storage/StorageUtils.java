package com.nutch.storage;

import com.nutch.metadata.Nutch;
import org.apache.gora.filter.Filter;
import org.apache.gora.mapreduce.GoraMapper;
import org.apache.gora.mapreduce.GoraOutputFormat;
import org.apache.gora.mapreduce.GoraReducer;
import org.apache.gora.persistency.Persistent;
import org.apache.gora.query.Query;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.util.GoraException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Partitioner;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

public class StorageUtils {

    public static ByteBuffer deepCopyToReadOnlyBuffer(
            ByteBuffer input) {
       ByteBuffer copy = ByteBuffer.allocate(input.capacity());
        int position = input.position();
        input.reset();
        int mark = input.position();
        int limit = input.limit();
        input.rewind();
        input.limit(input.capacity());
        copy.put(input);
        input.rewind();
        copy.rewind();
        input.position(mark);
        input.mark();
        copy.position(mark);
        copy.mark();
        input.position(position);
        copy.position(position);
        input.limit(limit);
        copy.limit(limit);
        return copy.asReadOnlyBuffer();
    }

    public static <K, V extends Persistent> DataStore<K, V> createWebStore(
            Configuration conf, Class<K> keyClass, Class<V> persistentClass
    ) throws ClassNotFoundException, GoraException {
        String carwlId = conf.get(Nutch.CRAWL_ID_KEY, "");
        String schemaPrefix = "";
        if (!carwlId.isEmpty()) {
            schemaPrefix = carwlId + "_";
        }
        String schema;
        if (WebPage.class.equals(persistentClass)) {
            schema = conf.get("storage.schema.webpage", "webpage");
            conf.set("preferred.schema.name", schemaPrefix + "webpage");
        } else if (Host.class.equals(persistentClass)) {
            schema = conf.get("storage.schema.host", "host");
            conf.set("preferred.schema.name", schemaPrefix + "host");
        } else {
            throw new UnsupportedOperationException("Unable to create store for class " + persistentClass);
        }
        Class<? extends DataStore<K, V>> dataStoreClass = (Class<? extends DataStore<K, V>>) getDataStoreClass(conf);
        return DataStoreFactory.createDataStore(dataStoreClass, keyClass, persistentClass, conf, schema);
    }

    public static <K, V extends Persistent> Class<? extends DataStore<K, V>> getDataStoreClass(
            Configuration conf) throws ClassNotFoundException {
        return (Class<? extends DataStore<K, V>>) Class.forName(conf.get("storage.data.store.class", "org.apache.gora.memory.store.MemStore"));
    }

    public static <K, V> void initMapperJob(Job job, Collection<WebPage.Field> fields,
                                            Class<V> outValueClass, Class<K> outKeyClass,
                                            Class<? extends GoraMapper<String, WebPage, K, V>> mapperClass
    ) throws ClassNotFoundException, IOException {
        initMapperJob(job, fields, outKeyClass, outValueClass, mapperClass, null, true);
    }

    public static <K, V> void initMapperJob(Job job, Collection<WebPage.Field> fields, Class<K> outKeyClass,
                                            Class<V> outValueClass, Class<? extends GoraMapper<String, WebPage, K, V>> mapperClass,
                                            Class<? extends Partitioner<K, V>> partitionerClass, boolean reuseObjects) throws ClassNotFoundException, IOException {
        initMapperJob(job, fields, outKeyClass, outValueClass, mapperClass, partitionerClass, null, reuseObjects);
    }

    public static <K, V> void initMapperJob(Job job, Collection<WebPage.Field> fields, Class<K> outKeyClass, Class<V> outValueClass,
                                            Class<? extends GoraMapper<String, WebPage, K, V>> mapperClass,
                                            Class<? extends Partitioner<K, V>> partitionerClass,
                                            Filter<String, WebPage> filter, boolean reuseObjects) throws ClassNotFoundException, IOException {
        DataStore<String, WebPage> store = createWebStore(job.getConfiguration(), String.class, WebPage.class);
        if (store == null) {
            throw new RuntimeException("Could not create dataStore");
        }
        Query<String, WebPage> query = store.newQuery();
        query.setFields(toStringArray(fields));
        if (filter != null) {
            query.setFilter(filter);
        }
        GoraMapper.initMapperJob(job, query, store, outKeyClass, outValueClass, mapperClass, partitionerClass, reuseObjects);
        GoraOutputFormat.setOutput(job, store, true);
    }

    public static <K,V> void initMapperJob(Job job,Collection<WebPage.Field> fields,
                                           Class<K> outKeyClass, Class<V> outValueClass,
                                           Class<? extends GoraMapper<String,WebPage,K,V>> mapperClass,
                                           Filter<String, WebPage> filter) throws ClassNotFoundException,IOException{
        initMapperJob(job, fields, outKeyClass, outValueClass, mapperClass, null,filter,true);
    }

    public static <K,V> void initReducerJob(Job job, Class<? extends GoraReducer<K,V,String,WebPage>> reducerClass)
        throws ClassNotFoundException,GoraException {
        Configuration conf = job.getConfiguration();
        DataStore<String, WebPage> store = StorageUtils.createWebStore(conf,String.class,WebPage.class);
        GoraReducer.initReducerJob(job,store,reducerClass);
        GoraOutputFormat.setOutput(job,store,true);
    }

    public static String[] toStringArray(Collection<WebPage.Field> fields) {
        String[] arr = new String[fields.size()];
        Iterator<WebPage.Field> iter = fields.iterator();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = iter.next().getName();
        }
        return arr;
    }

}
