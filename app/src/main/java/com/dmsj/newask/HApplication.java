package com.dmsj.newask;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.dmsj.newask.Activity.WelcomeActivity;
import com.dmsj.newask.http.ConfigUtils;
import com.dmsj.newask.http.HttpRestClient;
import com.dmsj.newask.http.HttpUrls;
import com.dmsj.newask.utils.MKLog;
import com.dmsj.newask.utils.SystemUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by Mickey.Li on 2016-3-22.
 */
public class HApplication {
    private static Context mApplication;

    /**
     * 系统所有http请求地址集合
     */
    public static HttpUrls httpUrls;


    public void init(Context context) {


        mApplication = context.getApplicationContext();
        init();
    }



    private void init() {
        //SpeechUtility.createUtility(mApplication, SpeechConstant.APPID + "=" + "5c0f47a2");
        SpeechUtility.createUtility(mApplication, SpeechConstant.APPID + "=" + "5a0007f1");
        MKLog.init(BuildConfig.LOG_DEBUG);
        if (httpUrls == null) httpUrls = new HttpUrls(ConfigUtils.WEB_IP);
        HttpRestClient.setmHttpUrls(httpUrls);

        try {
            HttpRestClient.addHttpHeader("ProjectVersion", SystemUtils.getAppVersionName());
            HttpRestClient.addHttpHeader("OSName", "Android");
            HttpRestClient.addHttpHeader("OSSYstemVersion", android.os.Build.VERSION.RELEASE);
            HttpRestClient.addHttpHeader("OSDeviceName", android.os.Build.MODEL);
            HttpRestClient.addHttpHeader("OSLocale", mApplication.getResources().getConfiguration().locale.toString());
        } catch (Exception e) {

        }
    }


    public static synchronized Context getApplication() {
        if (mApplication == null)
            mApplication = new Application();
        return mApplication;
    }

}
