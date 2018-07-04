package com.javaone.onepet;

import android.app.PendingIntent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageWindowView extends LinearLayout{
    public static int viewWidth, viewHeight;
    public static AccessibilityEvent event;
    public MessageWindowView(final Context context,
                             final String msgUsrname,
                             final String msgContent) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.popupwindow, this);
        View view = findViewById(R.id.wx_msg_window);
        TextView tvMsgUsrname = (TextView)view.findViewById(R.id.wx_msg_usrname);
        tvMsgUsrname.setText(msgUsrname);
        TextView tvMsgContent = (TextView)view.findViewById(R.id.wx_msg_content);
        tvMsgContent.setText(msgContent);
        viewWidth  = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;

        Button btCancel = (Button)findViewById(R.id.button_cancel);
        btCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                PetService.removeMessageWindow();
            }
        });

    }
}
