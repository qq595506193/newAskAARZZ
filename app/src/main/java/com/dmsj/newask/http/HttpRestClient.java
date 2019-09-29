package com.dmsj.newask.http;

import android.content.Context;
import android.text.TextUtils;

import com.dmsj.newask.Activity.ChatActivity;
import com.dmsj.newask.Activity.WelcomeActivity;
import com.dmsj.newask.HApplication;
import com.dmsj.newask.utils.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

public class HttpRestClient {

    private static HttpUrls mHttpUrls;
    public static AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();
    public static String mDeviceId;//设备包名
    public static String mKey = "58781ed63f807a8f5d157f2180d95e82";

    public static Map<String, String> getDefaultHeaders() {
        return mAsyncHttpClient.getHeaders();
    }

    public static void getDeviceId(Context context) {
//		mDeviceId = SystemUtils.getIMEI(context);
        mDeviceId = context.getApplicationInfo().packageName;
    }


    public static HttpUrls getmHttpUrls() {
        return mHttpUrls;
    }

    public static void setmHttpUrls(HttpUrls mHttpUrls) {
        HttpRestClient.mHttpUrls = mHttpUrls;
    }

    public static void addHttpHeader(String key, String value) {
        if (mAsyncHttpClient != null) {
            mAsyncHttpClient.addHeader(key, value);
        }
    }


    public static void doHttpVirtualDoctor(String message,String send_code,
                                           AsyncHttpResponseHandler handler) {
//        RequestParams params = new RequestParams();
//        params.put("Type", WelcomeActivity.Type);
//        params.put("site_id", WelcomeActivity.site_id);//
//        params.put("customer_id", LoginHttp.getLoginHttp().getLoginId());//
//        params.put("merchant_id", WelcomeActivity.MERCHANT_ID);
//        params.put("content", message);
//
//        params.put("batch_code", "1");
//        mAsyncHttpClient.get(mHttpUrls.post_ask, params, handler);
        //   http://robot.second-vision.net/Robot/consultation?APPKEY=EUmTO4OXAoftcylL&customer_id=322456&content=%E5%A4%B4%E7%96%BC%E3%80%82

//        http://157.122.68.20/XiaoYiRobotSer/consultation?tertype=7&customer_id=&content=%E5%A4%B4%E7%96%BC&APPKEY=EUmTO4OXAoftcylL&TERMINAL_ID=(唯一设备id)&PLATFORM_USER_ID=(中肿用户id)

        RequestParams params = new RequestParams();
//        params.put("APPKEY", "EUmTO4OXAoftcylL");
        params.put("APPKEY", WelcomeActivity.KEY);
        params.put("customer_id", LoginHttp.getLoginHttp().getLoginId());//
        params.put("content", message);
        params.put("PLATFORM_USER_ID", ChatActivity.userid);
        params.put("TERMINAL_ID", ChatActivity.deviceid);
        params.put("uid", UUID.randomUUID().toString().replace("-", ""));
        params.put("send_code", send_code);
        if (!TextUtils.isEmpty(ChatActivity.chatActivity.phoneType)) {
            params.put("phoneType", ChatActivity.chatActivity.phoneType);
        }
        params.put("tertype", "9");

//        params.put("batch_code", "1");http://zszl.second-vision.net
//        mAsyncHttpClient.get("http://192.168.1.222:9090/Robot/consultation", params, handler);
        mAsyncHttpClient.get(mHttpUrls.post_ask + "consultation", params, handler);
    }


    public static void doHttpMESGHISTORY(RequestParams params, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mHttpUrls.post_ask + "RobotSer", params, handler);
    }

    public static void doHttpHISTROY(RequestParams params, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mHttpUrls.post_ask + "history", params, handler);
    }

    public static void Login(AsyncHttpResponseHandler handler) {

        RequestParams params = new RequestParams();
        params.put("Type", "getCusid");
        params.put("TERMINALID", SystemUtils.getMyUUID(HApplication.getApplication()));//
        params.put("idfa", "123123");//
        params.put("MERCHANT_ID", WelcomeActivity.MERCHANT_ID);
        params.put("terminalidtype", "9");
        params.put("siteid", WelcomeActivity.site_id);

        mAsyncHttpClient.get(mHttpUrls.post_ask, params, handler);
    }

}
