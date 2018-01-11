package com.nutch.crawl;

import com.nutch.storage.WebPage;
import org.apache.avro.util.Utf8;
import org.apache.hadoop.io.MD5Hash;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;

public class MD5Signature extends Signature {

    private final static Collection<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();

    static {
        FIELDS.add(WebPage.Field.CONTENT);
    }

    @Override
    public byte[] calculate(WebPage page) {
        ByteBuffer buf = page.getContent();
        byte[] data;
        int of;
        int cb;
        if (buf == null) {
            Utf8 baseUrl = (Utf8) page.getBaseUrl();
            if (baseUrl == null) {
                data = null;
                of = 0;
                cb = 0;
            } else {
                data = baseUrl.getBytes();
                of = 0;
                cb = baseUrl.length();
            }
        }else {
            data = buf.array();
            of = buf.arrayOffset() + buf.position();
            cb = buf.remaining();
        }
        return MD5Hash.digest(data, of, cb).getDigest();
    }

    @Override
    public Collection<WebPage.Field> getFields() {
        return FIELDS;
    }
}
