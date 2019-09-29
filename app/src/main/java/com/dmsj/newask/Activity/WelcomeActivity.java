package com.dmsj.newask.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.dmsj.newask.HApplication;
import com.dmsj.newask.R;
import com.dmsj.newask.utils.SystemUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class WelcomeActivity extends Activity {
    public static String MERCHANT_ID = "5";
    public static String site_id = "5";
    public static String name = "就医咨询";
    public static String KEY = "EUmTO4OXAoftcylL";




    public static int welcomeid = R.layout.activity_welcome_qk;
    public static int themeId = R.style.AQUA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(welcomeid);
//        LoginHttp.getLoginHttp().tologin();
        Observable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        Intent intent = new Intent(WelcomeActivity.this, ChatActivity.class);

                        intent.putExtra("appUserId", SystemUtils.getMyUUID(HApplication.getApplication()));
                        startActivity(intent);
                        finish();
                        return null;
                    }
                }).subscribe();
    }
}
