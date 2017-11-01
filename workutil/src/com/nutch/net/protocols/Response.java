package com.nutch.net.protocols;


import com.nutch.metadata.HttpHeaders;
import com.nutch.metadata.Metadata;

import java.net.URL;

public interface Response extends HttpHeaders {

    public URL getUrl();

    public int getCode();

    public String getHeader(String name);

    public Metadata getHeaders();

    public byte[] getContent();
}
