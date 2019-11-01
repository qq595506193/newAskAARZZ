package com.dmsj.newask.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * string 工具类
 *
 * @author Administrator
 */
public class HStringUtil {

    /**
     * 判断字符串是否为null
     * return true 表示为null或者length=0
     * false 表示length>0
     */
    public static boolean isEmpty(String str) {
        if (str == null || TextUtils.isEmpty(str.trim()) || str.equals("null"))
            return true;
        else
            return false;
    }


    //半角转为全角 避免排版混乱
    public static String ToFormatDBC(String input) {
        if (isEmpty(input)) return "";
        input = stringFilter(input);
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    private static String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("，", ",")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号   
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }


    public static String stringFormat(String s) {
        if (isEmpty(s) || null == s || s.equals("null")) {
            return "";
        } else {
            return s;
        }
    }


//    public static String chatRadom() {
//        int i = new Random().nextInt(5);
//        switch (i) {
//            case 1:
//                return HApplication.getAppResources().getString(R.string.welcome_language_1);
//            case 2:
//                return HApplication.getAppResources().getString(R.string.welcome_language_2);
//            case 3:
//                return HApplication.getAppResources().getString(R.string.welcome_language_3);
//            case 4:
//                return HApplication.getAppResources().getString(R.string.welcome_language_4);
//            case 5:
//                return HApplication.getAppResources().getString(R.string.welcome_language_5);
//            case 6:
//                return HApplication.getAppResources().getString(R.string.welcome_language_6);
//            case 7:
//                return HApplication.getAppResources().getString(R.string.welcome_language_7);
//            case 8:
//                return HApplication.getAppResources().getString(R.string.welcome_language_8);
//            case 9:
//                return HApplication.getAppResources().getString(R.string.welcome_language_9);
//            default:
//                return HApplication.getAppResources().getString(R.string.welcome_language_10);
//        }
//    }
}
