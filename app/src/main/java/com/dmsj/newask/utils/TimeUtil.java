package com.dmsj.newask.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String getMesgTime(Long time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return sdr.format(new Date(time));
    }


    public static long getMesgTime(String time) {
        if (TextUtils.isEmpty(time)) {
            return System.currentTimeMillis();
        }
        SimpleDateFormat sdr = new SimpleDateFormat("yyyyMMddHHmmss");
        long times;
        try {
            times = sdr.parse(time).getTime();
        } catch (ParseException e) {
            times = System.currentTimeMillis();
        }
        return times;
    }


}
