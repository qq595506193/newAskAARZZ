package com.dmsj.newask.Views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dmsj.newask.R;


/**
 * 双按钮弹出框
 * @author zhao
 */
@SuppressLint("ValidFragment")
public class DoubleBtnFragmentDialog extends DialogFragment {
	
	public interface OnDilaogClickListener{
		void onDismiss(DialogFragment fragment);
		void onClick(DialogFragment fragment, View v);
	}
	
	private OnDilaogClickListener mClickListener;
	
	public static DoubleBtnFragmentDialog  show(FragmentManager manager,String title,String content,String leftBtn,String rightBtn,OnDilaogClickListener click){
		Fragment fragment = manager.findFragmentByTag("DOUBLE_DIALOG");
		FragmentTransaction ft = manager.beginTransaction();
		if(fragment != null){
			ft.remove(fragment);
		}
		DoubleBtnFragmentDialog dialog = new DoubleBtnFragmentDialog(click);
		Bundle bundle = new Bundle();
		bundle.putString("title",title);
		bundle.putString("content",content);
		bundle.putString("leftBtn",leftBtn);
		bundle.putString("rightBtn",rightBtn);
		dialog.setArguments(bundle);
		ft.add(dialog,"DOUBLE_DIALOG");
		ft.commitAllowingStateLoss();
		AlertDialog d;
		return dialog;
	}
	
	/*
	 *默认 title为壹健康
	 */
	public static DoubleBtnFragmentDialog  showDefault(FragmentManager manager,String content,String leftBtn,String rightBtn,OnDilaogClickListener click){
		return show(manager, "小壹", content, leftBtn, rightBtn, click);
	}
	
	
	
	public DoubleBtnFragmentDialog() {}
	
	public DoubleBtnFragmentDialog(OnDilaogClickListener clickListener) {
		this.mClickListener = clickListener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity(), R.style.translucent_dialog);
//		Dialog dialog = new Dialog(getActivity(),R.style.translucent_dialog);
		Bundle bundle = getArguments();
		dialog.setCancelable(false);//使双按钮对话框点击屏幕不可被取消
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
				}
				return true;
			}
		});
		dialog.setContentView(R.layout.dialog_doublebtn_layout);
//		dialog.setContentView(MResource.getIdByName(getActivity().getApplicationContext(), "layout", "dialog_doublebtn_layout"));
//		TextView titleTxtV = (TextView)dialog.findViewById(MResource.getIdByName(getActivity().getApplicationContext(), "id", "dialog_title"));
		TextView titleTxtV = (TextView)dialog.findViewById(R.id.dialog_title);
//		TextView contentTxtV = (TextView)dialog.findViewById(MResource.getIdByName(getActivity().getApplicationContext(), "id", "dialog_note"));
		TextView contentTxtV = (TextView)dialog.findViewById(R.id.dialog_note);
//		Button button1 = (Button)dialog.findViewById(MResource.getIdByName(getActivity().getApplicationContext(), "id", "dialog_cancel"));
		Button button1 = (Button)dialog.findViewById(R.id.dialog_cancel);
//		Button button2 = (Button)dialog.findViewById(MResource.getIdByName(getActivity().getApplicationContext(), "id", "dialog_ok"));
		Button button2 = (Button)dialog.findViewById(R.id.dialog_ok);
		
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
				if(mClickListener != null)mClickListener.onDismiss(DoubleBtnFragmentDialog.this);
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DoubleBtnFragmentDialog.this.dismissAllowingStateLoss();
				if(mClickListener != null)mClickListener.onClick(DoubleBtnFragmentDialog.this,v);
			}
		});
		titleTxtV.setText(bundle.getString("title"));
		contentTxtV.setText(bundle.getString("content"));
		button1.setText(bundle.getString("leftBtn"));
		button2.setText(bundle.getString("rightBtn"));
		return dialog;
	}
	
}
