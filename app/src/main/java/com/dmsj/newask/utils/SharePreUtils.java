package com.dmsj.newask.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dmsj.newask.Activity.ThemeChangeActivity;
import com.dmsj.newask.Activity.WelcomeActivity;

/**
 * xml操作
 *
 * @author jack_tang
 */
public class SharePreUtils {

    public static final String USER_INFO = "user_info";

    public static SharedPreferences getSharePreFernces(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static void saveThemei(Context ctx, int i) {
        SharedPreferences preferences = getSharePreFernces(ctx, "themeid");
        preferences.edit().putInt("themei", i).commit();
    }

    public static void getTheme(Context ctx) {
        SharedPreferences preferences = getSharePreFernces(ctx, "themeid");
        int n = preferences.getInt("themei", 0);
        if (n < ThemeChangeActivity.themeIds.length) {
            WelcomeActivity.themeId = ThemeChangeActivity.themeIds[n];
        } else {
            WelcomeActivity.themeId = ThemeChangeActivity.themeIds[0];
        }

    }

    public static boolean getSoundsBoolean(Context context) {
        SharedPreferences preferences = getSharePreFernces(context, USER_INFO);
        return preferences.getBoolean("SOUNDS_BOOLEAN", false);
    }

    public static void saveSoundsBoolean(Context context, Boolean aBoolean) {
        SharedPreferences preferences = getSharePreFernces(context, USER_INFO);
        preferences.edit().putBoolean("SOUNDS_BOOLEAN", aBoolean).commit();
    }

    public static String getId(Context context) {
        SharedPreferences preferences = getSharePreFernces(context, USER_INFO);
        return preferences.getString("LOGINID", "");
    }

    public static void saveId(Context context, String id) {
        SharedPreferences preferences = getSharePreFernces(context, USER_INFO);
        preferences.edit().putString("LOGINID", id).commit();
    }


}
