package com.dmsj.newask.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by x_wind on 18/6/12.
 */
public class myCheckBoc extends CheckBox {
    public myCheckBoc(Context context) {
    super(context);
}
    public myCheckBoc(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public myCheckBoc(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //返回textview是否处在选中的状态
    //而只有选中的textview才能够实现跑马灯效果
    @Override
    public boolean isFocused() {
        return true;
    }
}
