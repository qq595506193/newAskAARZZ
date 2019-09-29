package com.dmsj.newask.http;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.dmsj.newask.Activity.ChatActivity;
import com.dmsj.newask.utils.MKLog;
import com.dmsj.newask.utils.SharePreUtils;
import com.dmsj.newask.utils.WeakHandler;

import org.json.JSONObject;

/**
 * Created by x_wind on 17/4/25.
 */
public class LoginHttp {
    public static final int Login = 10000;
    private String loginId = "";
    private static LoginHttp loginHttp = new LoginHttp();

    public static LoginHttp getLoginHttp() {
        return loginHttp;
    }


    public String getLoginId() {
//        if (TextUtils.isEmpty(loginId)) {
//            loginId = SharePreUtils.getId(ChatActivity.chatActivity);
//        }
        return loginId;
    }

    public void setLoginId(String loginId) {
//        SharePreUtils.saveId(ChatActivity.chatActivity, loginId);
        this.loginId = loginId;
    }

    WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Login:
                    login();
                    break;
            }
            return false;
        }
    });

    private void login() {
        HttpRestClient.Login(new JsonHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                MKLog.e(response.toString());
                if (response.optInt("code") != 0) return;
                setLoginId(response.optString("server_params"));
                mHandler.removeMessages(Login);
            }
        });
        if (TextUtils.isEmpty(getLoginId())) {
            mHandler.sendEmptyMessageDelayed(Login, 10 * 1000);
        }
    }


    public void tologin() {
        login();
    }
}
