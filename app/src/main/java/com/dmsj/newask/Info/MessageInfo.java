package com.dmsj.newask.Info;

import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x_wind on 17/3/15.
 */
public class MessageInfo {

    void MessageInfo() {
        list = new ArrayList<MessageBtn>();
        morelist = new ArrayList<MessageBtn>();
        doctorMessageList = new ArrayList<MessageDoctorInfo>();
        checkMoreInfoList = new ArrayList<CheckMoreInfo>();
        TScheckMoreInfoList = new ArrayList<CheckMoreInfo>();

    }

    public static final int TEXT = 1, BTN = 2, SUANBING = 3, DOCTORCARD = 4, CHECK_MORE = 5, TSCHECKBOX = 6, TIMESHOW = 7;
    private int type = 1;
    private String message = "";
    private String chongxin_shuru = "";
    private String send_code = "";
    private List<MessageBtn> list;
    private List<MessageBtn> morelist;
    private List<MessageDoctorInfo> doctorMessageList;
    private List<CheckMoreInfo> checkMoreInfoList;
    private List<CheckMoreInfo> TScheckMoreInfoList;
    private long time;
    private boolean isSend = true;
    private boolean isAdd = true;
    private boolean isSystemMessage = false;
    public List<View> viewlist;
    private boolean isHis = false;
    private int btn_align;

    public int getBtn_align() {
        return btn_align;
    }

    public void setBtn_align(int btn_align) {
        this.btn_align = btn_align;
    }

    public boolean isHis() {
        return isHis;
    }

    public void setHis(boolean his) {
        isHis = his;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setList(List<MessageBtn> list) {
        this.list = list;
    }

    public List<MessageBtn> getList() {
        return list;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setIsAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setChongxin_shuru(String chongxin_shuru) {
        this.chongxin_shuru = chongxin_shuru;
    }

    public String getChongxin_shuru() {
        return chongxin_shuru;
    }

    public void setSend_code(String send_code) {
        this.send_code = send_code;
    }

    public String getSend_code() {

        return TextUtils.isEmpty(send_code) ? "0" : send_code;
    }

    public void setMorelist(List<MessageBtn> morelist) {
        this.morelist = morelist;
    }

    public List<MessageBtn> getMorelist() {
        return morelist;
    }

    public void setDoctorMessageList(List<MessageDoctorInfo> doctorMessageList) {
        this.doctorMessageList = doctorMessageList;
    }

    public List<MessageDoctorInfo> getDoctorMessageList() {
        return doctorMessageList;
    }

    public void setCheckMoreInfoList(List<CheckMoreInfo> checkMoreInfoList) {
        this.checkMoreInfoList = checkMoreInfoList;
    }

    public List<CheckMoreInfo> getCheckMoreInfoList() {
        return checkMoreInfoList;
    }

    public void setTScheckMoreInfoList(List<CheckMoreInfo> TScheckMoreInfoList) {
        this.TScheckMoreInfoList = TScheckMoreInfoList;
    }

    public void setIsSystemMessage(boolean isSystemMessage) {
        this.isSystemMessage = isSystemMessage;
    }

    public boolean isSystemMessage() {
        return isSystemMessage;
    }

    public List<CheckMoreInfo> getTScheckMoreInfoList() {
        return TScheckMoreInfoList;
    }

    public static MessageInfo addSendMessage(String message, String send_code, long time) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setIsSend(true);
        messageInfo.setType(MessageInfo.TEXT);
        messageInfo.setMessage(message.replace("&#62;", ">").replace("&#60;", "<"));
        messageInfo.setChongxin_shuru("");
        messageInfo.setTime(time);
        messageInfo.setSend_code(send_code);
        messageInfo.setIsSystemMessage(false);
        return messageInfo;
    }

    public static MessageInfo addReceiveMessage(String message, long time, String chongxin_shuru, String send_code, boolean isSystemMessage) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setIsSend(false);
        messageInfo.setType(MessageInfo.TEXT);
        messageInfo.setMessage(message);
        messageInfo.setTime(time);
        messageInfo.setChongxin_shuru(chongxin_shuru);
        messageInfo.setSend_code(send_code);
        messageInfo.setIsSystemMessage(isSystemMessage);
        return messageInfo;
    }

    public static MessageInfo addReceiveList(String message, int btn_align, List<MessageBtn> list, long time, String chongxin_shuru, String send_code, boolean isSystemMessage) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setIsSend(false);
        messageInfo.setType(MessageInfo.BTN);
        messageInfo.setBtn_align(btn_align);
        messageInfo.setMessage(message);
        messageInfo.setList(list);
        messageInfo.setTime(time);
        messageInfo.setChongxin_shuru(chongxin_shuru);
        messageInfo.setSend_code(send_code);
        messageInfo.setIsSystemMessage(isSystemMessage);
        return messageInfo;
    }

    public static MessageInfo addReceiveMoreList(String message, List<MessageBtn> list, List<MessageBtn> morelist, long time, String chongxin_shuru, String send_code, boolean isSystemMessage) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setIsSend(false);
        messageInfo.setType(MessageInfo.SUANBING);
        messageInfo.setMessage(message);
        messageInfo.setList(list);
        messageInfo.setMorelist(morelist);
        messageInfo.setTime(time);
        messageInfo.setChongxin_shuru(chongxin_shuru);
        messageInfo.setSend_code(send_code);
        messageInfo.setIsSystemMessage(isSystemMessage);
        return messageInfo;
    }

    public static MessageInfo addDoctorMessageList(String message, List<MessageDoctorInfo> list, long time, String chongxin_shuru, String send_code, boolean isSystemMessage) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setIsSend(false);
        messageInfo.setType(MessageInfo.DOCTORCARD);
        messageInfo.setMessage(message);
        messageInfo.setDoctorMessageList(list);
        messageInfo.setTime(time);
        messageInfo.setChongxin_shuru(chongxin_shuru);
        messageInfo.setSend_code(send_code);
        messageInfo.setIsSystemMessage(isSystemMessage);
        return messageInfo;
    }

    public static MessageInfo addCheckMoreList(String message, List<CheckMoreInfo> list, long time, String chongxin_shuru, String send_code, boolean isSystemMessage) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setIsSend(false);
        messageInfo.setType(MessageInfo.CHECK_MORE);
        messageInfo.setMessage(message);
        messageInfo.setCheckMoreInfoList(list);
        messageInfo.setTime(time);
        messageInfo.setChongxin_shuru(chongxin_shuru);
        messageInfo.setSend_code(send_code);
        messageInfo.setIsSystemMessage(isSystemMessage);
        return messageInfo;
    }

    public static MessageInfo addTSCheckMoreList(String message, List<CheckMoreInfo> list, long time, String chongxin_shuru, String send_code, boolean isSystemMessage) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setIsSend(false);
        messageInfo.setType(MessageInfo.TSCHECKBOX);
        messageInfo.setMessage(message);
        messageInfo.setTScheckMoreInfoList(list);
        messageInfo.setTime(time);
        messageInfo.setChongxin_shuru(chongxin_shuru);
        messageInfo.setSend_code(send_code);
        messageInfo.setIsSystemMessage(isSystemMessage);
        return messageInfo;
    }

    public static MessageInfo addTIME(String message, long time, String chongxin_shuru, String send_code, boolean isSystemMessage) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setIsSend(false);
        messageInfo.setType(MessageInfo.TIMESHOW);
        messageInfo.setMessage(message);
        messageInfo.setTime(time);
        messageInfo.setChongxin_shuru(chongxin_shuru);
        messageInfo.setSend_code(send_code);
        messageInfo.setIsSystemMessage(isSystemMessage);
        return messageInfo;
    }

}
