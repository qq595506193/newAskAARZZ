package com.dmsj.newask.utils;

import android.content.Context;
import android.widget.Toast;

import com.dmsj.newask.HApplication;



/**
 * 此类用于所有的toast提示
 *
 * @author Administrator
 */


public class ToastUtil {

    private static final int TShortTime = 800;


    private ToastUtil() {

    }

    /**
     * 最基本的short toast提示
     *
     * @param context
     * @param content
     */
    public static void showBasicShortToast(Context context, String content) {
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        toast.show();
    }

    // 默认的文字提示 请稍后重试
    public static void showBasicErrorShortToast(Context context) {
        Toast toast = Toast.makeText(context, "网络失败,请稍后重试", Toast.LENGTH_LONG);
        toast.show();
    }


    public static void onShow(Context context, int strid, int duration) {
        Toast toast = Toast.makeText(context, context.getString(strid), duration);
        toast.show();
    }

    public static void onShow(Context context, String str, int duration) {
        Toast toast = Toast.makeText(context, str, duration);
        toast.show();
    }

    public static void showShort(Context context, int strid) {
        onShow(context, strid, TShortTime);
    }

    public static void showShort(Context context, String str) {
        onShow(context, str, TShortTime);
    }

    public static void showLong(Context context, int strid) {
        onShow(context, strid, Toast.LENGTH_LONG);
    }

    public static void showLong(Context context, String content) {
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showShort(String str) {
        showShort(HApplication.getApplication(), str);
    }


}
