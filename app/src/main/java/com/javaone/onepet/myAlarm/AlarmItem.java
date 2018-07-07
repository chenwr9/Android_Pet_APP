package com.javaone.onepet.myAlarm;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class AlarmItem extends LinearLayout {
    public AlarmItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlarmItem(Context context) {
        super(context);
    }

    @Override
    public void setPressed(boolean pressed) {
        if (pressed && ((View) getParent()).isPressed()) {
            return;
        }
        super.setPressed(pressed);
    }
}