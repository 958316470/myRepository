package com.xyd.splider;


import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

/**
 * Created by DXM_0020 on 2017/5/25.
 */
public class Test01 {
    private static   HttpClient httpClient = new HttpClient();
    private static Logger log = Logger.getLogger(Test01.class);
//    static {
//        httpClient.getHostConfiguration().setHost("192.168.1.121",8080);
//    }
    public static void main(String[] args){
        String url = "http://www.baidu.com";
        try {
            Test01.downloadPage(url,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean downloadPage(String path,Map<String,String> param) throws HttpException,IOException{
        log.info("访问地址："+path);
        //0.初始化变量
        InputStream inputStream = null;
        OutputStream outputStream = null;
        int index = 0;
        //1.获取post方法
        PostMethod postMethod = new PostMethod(path);
        //2.设置参数
        if(param != null && !param.isEmpty()){
            Set<String> keys = param.keySet();
            NameValuePair[] nameValuePairs = new NameValuePair[param.size()];
            for(String key : keys){
                nameValuePairs[index].setName(key);
                nameValuePairs[index].setValue(param.get(key));
            }
            postMethod.setRequestBody(nameValuePairs);
        }
        //3.执行请求，获取返回的状态码
        int statusCode = httpClient.executeMethod(postMethod);
        String status = String.valueOf(statusCode);
        log.info("statusCode:"+statusCode);
        if(statusCode == HttpStatus.HS_OK){
            inputStream = postMethod.getResponseBodyAsStream();
            //文件名称
            String file = path.substring(path.lastIndexOf("/")+1);
            //获取文件输出流
            outputStream = new FileOutputStream(file);
            int temp = -1;
            while ((temp=inputStream.read())>0){
                outputStream.write(temp);
            }
            log.info("连接成功结束！");
            if(inputStream != null){
                inputStream.close();
            }
            if(outputStream != null){
                outputStream.close();
            }
            return true;
        }else if(status.startsWith("30")){//重定向到分配的 URL
            //读取新URL
            Header header = postMethod.getResponseHeader("location");
            if(header != null){
                String url = header.getValue();
                log.info("新URL："+url);
                if(StringUtils.isEmpty(url)){
                    url = "/";
                }
                PostMethod redirect = new PostMethod(url);
                //有待补充代码
            }
        }
        log.info("连接失败结束！");
        return false;
    }
}
