package com.javaone.onepet;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import java.util.List;

class WechatLogService extends AccessibilityService {
    /**
     * 监听通知栏中微信接收消息的事件
     * @param event
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        Log.e("eventType", String.valueOf(eventType));
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            if (event.getPackageName().toString().equals("com.tencent.mm")) {
                List<CharSequence> messages = event.getText();
                for (CharSequence message : messages) {
                    String content = message.toString();
                    if (!TextUtils.isEmpty(content)) {
                        String msgUsername = "";
                        String msgContent  = "";
                        if (content.contains(":")) {
                            msgUsername = content.substring(0, content.indexOf(':'));
                            msgContent  = content.substring(content.indexOf(':')+1);
                            // 若字符内容大于15则需要省略
                            if (msgContent.length() > 15) {
                                msgContent = msgContent.substring(0, 15);
                                msgContent += "...";
                            }
                            Notification notification = (Notification)event.getParcelableData();
                            PetService.removeMessageWindow();
                            PetService.createMessageWindow(this, msgUsername, msgContent);
                        }
                    }
                }
            }
        }
    }

    /**
     * 必须重写的方法：系统要中断此service返回的响应时会调用。在整个生命周期会被调用多次。
     */

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "微信消息通知服务中断", Toast.LENGTH_SHORT).show();
    }

    /**
     * 服务开始连接
     */
    @Override
    protected void onServiceConnected() {
        Toast.makeText(this, "微信消息通知服务已开启", Toast.LENGTH_SHORT).show();
        super.onServiceConnected();
    }

    /**
     * 服务断开
     *
     * @param intent 点击打开链接
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "微信消息通知服务已关闭", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }
}
