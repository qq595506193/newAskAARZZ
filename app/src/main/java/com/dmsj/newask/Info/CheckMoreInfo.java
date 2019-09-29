package com.dmsj.newask.Info;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wihaohao.PageGridView;

/**
 * Created by x_wind on 18/4/17.
 */
public class CheckMoreInfo implements PageGridView.ItemModel {
    public static final String singlebtn = "1", doublebtn = "2";
    boolean isCheck = false;
    String message;
    String color;
    String singleDouble = doublebtn;

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setSingleDouble(String singleDouble) {
        this.singleDouble = singleDouble;
    }

    public String getSingleDouble() {
        return singleDouble;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public static CheckMoreInfo addInfo(String message, String singleDouble, String color) {
        CheckMoreInfo info = new CheckMoreInfo(message);
        info.setMessage(message);
        info.setSingleDouble(singleDouble);
        info.setColor(color);
        return info;
    }

    public CheckMoreInfo(String message) {
        this.message = message;
    }

    @Override
    public String getItemName() {
        return null;
    }

    @Override
    public void setIcon(ImageView imageView) {

    }

    @Override
    public void setItemView(View itemView) {
        CheckBox ck_index = (CheckBox) itemView;
        //ck_index.setText(message);
        //ck_index.setSelected(true);
        //checkBox.setChecked(mdInfo.isCheck());
        setCheckboxListener.checkBoxListener(ck_index);

    }

    private SetCheckboxListener setCheckboxListener;

    public interface SetCheckboxListener {
        void checkBoxListener(CheckBox ck_index);
    }

    public void setSetCheckboxListener(SetCheckboxListener setCheckboxListener) {
        this.setCheckboxListener = setCheckboxListener;
    }
}
