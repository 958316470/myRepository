package com.xyd.splider;

/**
 * http响应的状态值
 * Created by DXM_0020 on 2017/5/25.
 */
public class HttpStatus {

    public static int HS_OK = 200;//请求成功

    public static int HS_NEWRESOURCES = 201;//请求完成，结果是创建了新资源。新创建资源的URI 可在响应的实体中得到

    public static int HS_WAITTING = 202; //请求被接受，但处理尚未完成阻塞等待

    public static int HS_NORESPONSE = 204;//服务器端已经实现了请求，但是没有返回新的信息。如果客户是用户代理，则无须为此更新自身的文档视图

    public static int HS_MORERESOURCES = 300;//该状态码不被 HTTP/1.0 的应用程序直接使用，只是作为 3XX 类型回应的默认解释。存在多个可用的被请求资源

    public static int HS_FOREVERURL = 301;//请求到的资源都会分配一个永久的URL，这样就可以在将来通过该 URL 来访问此资源

    public static int HS_TEMPORARYURL = 302;//请求到的资源在一个不同的 URL 处临时保存

    public static int HS_NOTUPDATE = 304;//请求的资源未更新

    public static int HS_ILLEAGLEREQUEST = 400;//非法请求

    public static int HS_UNAUTHORIZE = 401;//未授权

    public static int HS_FORDDING = 403;//禁止

    public static int HS_NOTFOUND = 404;//没有找到

}
