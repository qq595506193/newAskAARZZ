package com.dmsj.newask.Info;

/**
 * Created by x_wind on 17/3/15.
 */
public class MessageBtn {
    private String id;
    private String btn_text;
    private String color;
    private boolean isAsk;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setBtn_text(String btn_text) {
        this.btn_text = btn_text;
    }

    public String getBtn_text() {
        return btn_text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setIsAsk(boolean isAsk) {
        this.isAsk = isAsk;
    }

    public boolean isAsk() {
        return isAsk;
    }

    public static MessageBtn addbtn(String id, String message, String color, String isAsk) {
        MessageBtn messageBtn = new MessageBtn();
        messageBtn.setId(id);
        messageBtn.setBtn_text(message);
        messageBtn.setColor(color);
        messageBtn.setIsAsk(isAsk.equals("1"));
        return messageBtn;
    }
}
