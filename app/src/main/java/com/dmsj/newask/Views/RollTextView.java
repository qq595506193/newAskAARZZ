package com.dmsj.newask.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class RollTextView extends android.support.v7.widget.AppCompatTextView {
    public RollTextView (Context context) {
        super(context);
    }
    public RollTextView (Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public RollTextView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
