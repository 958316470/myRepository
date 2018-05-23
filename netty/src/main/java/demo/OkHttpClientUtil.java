package demo;

import com.alibaba.fastjson.JSON;
import okhttp3.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by 57254 on 2017/12/15.
 *
 * @author wl
 */
public class OkHttpClientUtil<T> {
    /**
     * 返回一个Call对象该对象是post请求方式，json串UTF-8的数据格式,超市时间默认为秒
     *
     * @param requestParamBean
     * @param targetAddr
     * @param timeout
     * @return
     * @author wl
     */
    public static <T> Call toJsonStringPostSend(T requestParamBean, String targetAddr, long timeout) {
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON.toJSONString(requestParamBean));
        Request request = new Request.Builder()
                .url(targetAddr)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
        return client.newCall(request);
    }

    public static Call toJsonStringGetSend(String targetAddr) {
        Request request = new Request.Builder()
                .url(targetAddr)
                .get()
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.SECONDS)
                .build();
        return client.newCall(request);
    }

    /**
     * 默认创建Call对象，超时时间默认为15秒
     *
     * @param requestParamBean 请求参数
     * @param targetAddr       目标主机/接口地址
     * @return Call
     */
    public static <T> Call defualtSend(T requestParamBean, String targetAddr) {
        return toJsonStringPostSend(requestParamBean, targetAddr, 2);
    }
}
