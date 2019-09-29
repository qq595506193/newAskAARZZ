package com.dmsj.newask.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.PopupWindow;

import com.dmsj.newask.R;
import com.dmsj.newask.Views.WheelView;
import com.dmsj.newask.adapter.ArrayWheelAdapter;

import java.util.List;
import java.util.Map;


public class WheelUtils {


    /**
     * 显示单滚轮
     *
     * @param context
     * @param list
     */
    public static PopupWindow showSingleWheel(
            final Context context,
            final List<Map<String, String>> list,
            final View parentView,
            final OnClickListener listener
    ) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View wheelView = inflater.inflate(R.layout.double_wheel, null);
        final PopupWindow pop = new PopupWindow(wheelView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final WheelView wheelOne = (WheelView) wheelView.findViewById(R.id.wheel_one);
        final WheelView wheelTwo = (WheelView) wheelView.findViewById(R.id.wheel_two);
        wheelTwo.setVisibility(View.GONE);
        Button wheelCancel = (Button) wheelView.findViewById(R.id.wheel_cancel);
        Button wheelSure = (Button) wheelView.findViewById(R.id.wheel_sure);


        wheelCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        wheelSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    v.setTag(R.id.wheel_one, wheelOne.getCurrentItem());
                    listener.onClick(v);
                }
                pop.dismiss();
            }
        });
        wheelOne.setAdapter(new ArrayWheelAdapter(list, list.size()));
        wheelOne.setCurrentItem(0);
        wheelOne.setCyclic(false);
        wheelOne.setInterpolator(new AnticipateOvershootInterpolator());

        pop.setFocusable(true);
        pop.setAnimationStyle(R.style.PopBottomShow);
        pop.setTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);
        pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        return pop;
    }

    /**
     * 显示单滚轮
     *
     * @param context
     */
    public static PopupWindow showSingleWheel(
            final Context context,
            final String[] strs,
            final View parentView,
            final OnClickListener listener,
            final int n
    ) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View wheelView = inflater.inflate(R.layout.double_wheel, null);
        final PopupWindow pop = new PopupWindow(wheelView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final WheelView wheelOne = (WheelView) wheelView.findViewById(R.id.wheel_one);
        final WheelView wheelTwo = (WheelView) wheelView.findViewById(R.id.wheel_two);
        wheelTwo.setVisibility(View.GONE);
        Button wheelCancel = (Button) wheelView.findViewById(R.id.wheel_cancel);
        Button wheelSure = (Button) wheelView.findViewById(R.id.wheel_sure);


        wheelCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        wheelSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    v.setTag(R.id.wheel_one, wheelOne.getCurrentItem());
                    listener.onClick(v);
                }
                pop.dismiss();
            }
        });
        wheelOne.setAdapter(new ArrayWheelAdapter(strs, strs.length));
        wheelOne.setCurrentItem(n);
        wheelOne.setCyclic(false);
        wheelOne.setInterpolator(new AnticipateOvershootInterpolator());

        pop.setFocusable(true);
        pop.setAnimationStyle(R.style.PopBottomShow);
        pop.setTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);
        pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        return pop;
    }
}
