package com.xyd.splider;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * 下载文件
 * Created by DXM_0020 on 2017/5/25.
 */
public class DownloadFile {

    private static Logger log = Logger.getLogger(DownloadFile.class);
    /**
     *获取文件的名字
     *
     * @param url 访问的URL
     * @param contentType 响应类型 text/···
     * @return
     */
    public String getFileNameByUrl(String url,String contentType){
        //1.移除http://
        if(url.startsWith("https")){
            url = url.substring(8);
        }else {
            url = url.substring(7);
        }
        //2.判断响应类型，并对其处理
        if(contentType.indexOf("html")!=-1){
            //text/html
            url = url.replaceAll("[\\?/:*|<>\"]","_")+".html";
        }else{
            //application/pdf
            url = url.replaceAll("[\\?/:*|<>\"]","_")+contentType.substring(contentType.lastIndexOf("/")+1);
        }
        return url;
    }

    /**
     * 保存网页中的字节数据到本地
     *
     * @param br 网页中字节数据输入流
     * @param filePath 文件的相对路径
     */
    public void saveToLocal(BufferedReader br,String filePath){
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath)));
            String result="";
           while ((result = br.readLine())!=null){
                out.writeUTF(result);
            }
            out.flush();
            out.close();
            log.info("保存文件成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String downLoadFile(String url){
        log.info("下载文件访问的："+url);
        //0.初始化变量
        String filePath = null;
        HttpClient httpClient = new HttpClient();
        //1.设置连接超时5S
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        //2.生成getMethod并设置参数
        GetMethod getMethod = new GetMethod(url);
        // 设置 get 请求超时 5s
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,5000);
        // 设置请求重试处理
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
        //3.执行get请求
        BufferedReader br = null;
        InputStream inputStream = null;
        try {
            int status = httpClient.executeMethod(getMethod);
            //4.处理响应
            if(status == HttpStatus.HS_OK){
                filePath=SpliederConfiger.filePath+getFileNameByUrl(url,getMethod.getResponseHeader("Content-Type").getValue());
                inputStream = getMethod.getResponseBodyAsStream();
                br = new BufferedReader(new InputStreamReader(inputStream));
                saveToLocal(br,filePath);
            }else {
                log.info("下载文件失败："+getMethod.getStatusLine());
            }

        } catch (IOException e) {
            log.info("下载文件失败发生异常："+e.getMessage());
        }finally {
            getMethod.releaseConnection();
            try {
                if(br!=null){
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }
}
