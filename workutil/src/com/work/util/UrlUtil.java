package com.work.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlUtil {
    public static void main(String[] args){
        UrlUtil util = new UrlUtil();
        String str = "名字";
        System.out.println(util.urlEncoder(str));
    }

    private String urlEncoder(String str){
        try {
            return URLEncoder.encode(str,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
