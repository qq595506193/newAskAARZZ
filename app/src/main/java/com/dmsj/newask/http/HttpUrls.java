package com.dmsj.newask.http;

/**
 * http请求地址
 *
 * @author zhao
 */
public class HttpUrls {

    /**
     * 根路径
     */
    public final String WEB_ROOT;
    public String post_ask;


    public HttpUrls(String webRoot) {
        WEB_ROOT = webRoot;
        post_ask = WEB_ROOT + "/XiaoYiRobotSer/";
    }
}
