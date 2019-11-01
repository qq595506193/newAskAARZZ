package com.dmsj.newask.utils;

import java.util.Calendar;


public class TimeShow {



    public static int getDayOfMonth(String year, String month) {
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(year), Integer.parseInt(month), 0); //输入类型为int类型
        return c.get(Calendar.DAY_OF_MONTH);
    }

}
