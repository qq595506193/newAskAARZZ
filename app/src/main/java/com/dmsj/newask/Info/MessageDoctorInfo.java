package com.dmsj.newask.Info;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x_wind on 18/4/17.
 */
public class MessageDoctorInfo {
    String headurl;
    String name;
    String job;
    String hospital;
    String zc;
    String zw;
    String jyjl;
    String id;
    String office;
    String WORK_NUMBER;
    String DEPT_CODE;
    List<DoctorOutInfo> list;


    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getHospital() {
        return hospital;
    }

    public void setZc(String zc) {
        this.zc = zc;
    }

    public String getZc() {
        return zc;
    }

    public void setJyjl(String jyjl) {
        this.jyjl = jyjl;
    }

    public String getJyjl() {
        return jyjl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setZw(String zw) {
        this.zw = zw;
    }

    public String getZw() {
        return zw;
    }

    public void setWORK_NUMBER(String WORK_NUMBER) {
        this.WORK_NUMBER = WORK_NUMBER;
    }

    public String getWORK_NUMBER() {
        return WORK_NUMBER;
    }

    public void setList(List<DoctorOutInfo> list) {
        if (list != null)
            this.list = list;
    }

    public List<DoctorOutInfo> getList() {
        if (list == null)
            list = new ArrayList<>();
        return list;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getOffice() {
        return office;
    }

    public void setDEPT_CODE(String DEPT_CODE) {
        this.DEPT_CODE = DEPT_CODE;
    }

    public String getDEPT_CODE() {
        return DEPT_CODE;
    }

    public static MessageDoctorInfo addInfo(String id, String name, String job, String zc, String headurl, String hospital, String jyjl, String zw, String office, String WORK_NUMBER,String DEPT_CODE, List<DoctorOutInfo> list) {
        MessageDoctorInfo info = new MessageDoctorInfo();
        info.setId(id);
        info.setZc(zc);
        info.setJyjl(jyjl);
        info.setHospital(hospital);
        info.setJob(job);
        info.setHeadurl(headurl);
        info.setName(name);
        info.setZw(zw);
        info.setList(list);
        info.setOffice(office);
        info.setWORK_NUMBER(WORK_NUMBER);
        info.setDEPT_CODE(DEPT_CODE);
        return info;
    }
}
