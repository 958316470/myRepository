package com.xyd;

import org.apache.commons.lang3.StringUtils;

public class Demo {

    public static void main(String[] args) {
        Integer value = 4;
        out(value);
    }

    public static String formatStr(String name,int value) {
        return "public static final ProtocolStatus STATUS_" + name.toUpperCase() + " = makeStatus(" + name + ");";
    }

    public static void out(Integer language) {
        if((language==1)||(language==3)||(language==4)){
           System.out.println(language);
        }
    }

    private AuthBean parseToAuthBean(String userInfo) {
        AuthBean authBean = new AuthBean();
        for(String user : userInfo.split("\\|")){
            System.out.println(user);
        }

        authBean.setCustomID(userInfo.split("\\|")[0]);
        authBean.setUserNick(userInfo.split("\\|")[1]);
        authBean.setUserId(userInfo.split("\\|")[2]);
        authBean.setOrigin("01");
        return authBean;
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
