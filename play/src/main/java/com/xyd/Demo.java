package com.xyd;

import org.apache.commons.lang3.StringUtils;

public class Demo {

    public static void main(String[] args) {
        String[] nameArr = {"SUCCESS","FAILED","PROTO_NOT_FOUND",
                "GONE","MOVED","TEMP_MOVED","NOTFOUND",
        "RETRY","EXCEPTION","ACCESS_DENIED","ROBOTS_DENIED",
        "REDIR_EXCEEDED","NOTFETCHING","NOTMODIFIED","WOULDBLOCK","BLOCKED"};
        int[] valueArr = {1,2,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
        if(nameArr.length == valueArr.length) {
            for(int i=0;i<nameArr.length;i++) {
                System.out.println(formatStr(nameArr[i],valueArr[i]));
            }
        }
    }

    public static String formatStr(String name,int value) {
        return "public static final ProtocolStatus STATUS_" + name.toUpperCase() + " = makeStatus(" + name + ");";
    }











    public static String formatStat(String name) {
        return "public static final String STAT_" + name.toUpperCase() + " = \"" + name + "\";";
    }

    public static String replaceAllDataId(String src, String content) {
        if (StringUtils.isEmpty(src) || StringUtils.isEmpty(content) || !src.contains(content)) {
            return src;
        } else {
            while (src.contains(content)) {
                src = replaceDataId(src, content);
            }
        }
        return src;
    }

    public static String replaceDataId(String src, String content) {
        int index = src.indexOf(content);
        String temp = src.substring(index);
        int second = temp.indexOf(">");
        String dataId = temp.substring(0, second);
        src = src.replace(dataId, "");
        return src;
    }
}
