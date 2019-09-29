package com.dmsj.newask.Info;

import java.util.List;

/**
 * Created by x_wind on 18/6/19.
 */
public class DoctorOutInfo {
    String REGISTER_DATE;
    String NOON_NAME;
    String SESSION_TYPE;

    public void setNOON_NAME(String NOON_NAME) {
        this.NOON_NAME = NOON_NAME;
    }

    public String getNOON_NAME() {
        return NOON_NAME;
    }

    public void setSESSION_TYPE(String SESSION_TYPE) {
        this.SESSION_TYPE = SESSION_TYPE;
    }

    public String getSESSION_TYPE() {
        return SESSION_TYPE;
    }

    public void setREGISTER_DATE(String REGISTER_DATE) {
        this.REGISTER_DATE = REGISTER_DATE;
    }

    public String getREGISTER_DATE() {
        return REGISTER_DATE;
    }

    public static DoctorOutInfo addInfo(String NOON_NAME, String REGISTER_DATE, String SESSION_TYPE) {
        DoctorOutInfo info = new DoctorOutInfo();
        info.setNOON_NAME(NOON_NAME);
        info.setREGISTER_DATE(REGISTER_DATE);
        info.setSESSION_TYPE(SESSION_TYPE);
        return info;
    }
}
