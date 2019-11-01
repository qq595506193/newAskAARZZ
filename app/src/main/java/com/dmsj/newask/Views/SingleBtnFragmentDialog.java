package com.dmsj.newask.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dmsj.newask.R;


/**
 * 单按钮弹出框
 *
 * @author zhao
 */
@SuppressLint("ValidFragment")
public class SingleBtnFragmentDialog extends DialogFragment {

    private String shareDialog = "";
    private Activity onAttachActivity;
    private OnClickSureBtnListener listener;

    public SingleBtnFragmentDialog(OnClickSureBtnListener listener) {
        super();
        this.listener = listener;
    }

    public SingleBtnFragmentDialog() {
        super();
    }


    public static void show(FragmentManager manager, String title, String content, String btnStr) {
        Fragment fragment = manager.findFragmentByTag("charge");
        FragmentTransaction ft = manager.beginTransaction();
        if (fragment != null) {
            ft.remove(fragment);
        }
        SingleBtnFragmentDialog dialog = new SingleBtnFragmentDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putString("btnStr", btnStr);
        dialog.setArguments(bundle);
        ft.add(dialog, "dialog1");
        ft.commitAllowingStateLoss();
    }

    /**
     * 默认title为多美小壹 button为知道了
     *
     * @param manager
     * @param content
     */
    public static void showDefault(FragmentManager manager, String content) {
        show(manager, "小壹", content, "知道了");
    }

    /**
     * 默认title为多美小壹 button为知道了
     *
     * @param manager
     * @param content
     * @param listener1
     */
    public static void showDefault(FragmentManager manager, String content, OnClickSureBtnListener listener1) {
        show(manager, "小壹", content, "知道了", listener1);
    }

    public static SingleBtnFragmentDialog show(FragmentManager manager, String title, String content, String btnStr, OnClickSureBtnListener listener1) {
        Fragment fragment = manager.findFragmentByTag("charge");
        FragmentTransaction ft = manager.beginTransaction();
        if (fragment != null) {
            ft.remove(fragment);
        }
        SingleBtnFragmentDialog dialog = new SingleBtnFragmentDialog(listener1);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putString("btnStr", btnStr);
        dialog.setArguments(bundle);
        ft.add(dialog, "dialog1");
        ft.commitAllowingStateLoss();
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            onAttachActivity = activity;
            listener = (OnClickSureBtnListener) activity;
        } catch (ClassCastException e) {
        }
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity(),R.style.translucent_dialog);
        dialog.setCancelable(false);//使双按钮对话框点击屏幕不可被取消
        dialog.setCanceledOnTouchOutside(false);
        Bundle bundle = getArguments();
        if (!shareDialog.equals("share")) {
            dialog.setContentView(R.layout.dialog_singlebtn_layout);
            TextView titleTxtV = (TextView) dialog.findViewById(R.id.dialog_title);
            TextView contentTxtV = (TextView) dialog.findViewById(R.id.dialog_note);
            contentTxtV.setMovementMethod(ScrollingMovementMethod.getInstance());
            Button button = (Button) dialog.findViewById(R.id.dialog_ok);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissAllowingStateLoss();
                    if (listener != null) {
                        listener.onClickSureHander();
                    }
                }
            });
            titleTxtV.setText(bundle.getString("title"));
            contentTxtV.setText(bundle.getString("content"));
            button.setText(bundle.getString("btnStr"));
        }
        return dialog;
    }

    public interface OnClickSureBtnListener {
        void onClickSureHander();
    }

}
