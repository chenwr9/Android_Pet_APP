package com.javaone.onepet;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PetService extends Service {
    private static WindowManager mWindowManager;
    private static WindowManager.LayoutParams mLayoutParams;
    private static WindowManager.LayoutParams mSmallLayoutParams;
    private static WindowManager.LayoutParams messageWindowParams;
    private static LinearLayout mPetView;      // 悬浮窗布局
    private static LinearLayout mSmallPetView; // 贴边后显示的悬浮窗布局
    private static MessageWindowView messageWindow;
    private ImageView mPetView_image;
    private int screenWidth, screenHeight;
    private static boolean isShowPetView, isShowSmallPetView, isMsgWindowView;

    /**
     * 创建微信消息弹窗
     * @param context    上下文
     * @param msgUsrname 记录发送消息的用户
     * @param msgContent 记录发送消息的内容
     */
    public static void createMessageWindow(Context context,
                                           String msgUsrname,
                                           String msgContent) {
        if (messageWindow == null) {
            messageWindow = new MessageWindowView(context, msgUsrname, msgContent);
            if (messageWindowParams == null) {
                messageWindowParams = new WindowManager.LayoutParams();
                // 需要根据API的版本设置悬浮窗的权限
                if (Build.VERSION.SDK_INT >= 26) {
                    messageWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    messageWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                messageWindowParams.format = PixelFormat.RGBA_8888;
                messageWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                messageWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                messageWindowParams.width = MessageWindowView.viewWidth;
                messageWindowParams.height = MessageWindowView.viewHeight;
            }
            // 宠物贴边时弹窗的显示位置
            if (isShowSmallPetView) {
                messageWindowParams.x = mSmallLayoutParams.x - MessageWindowView.viewWidth;
                messageWindowParams.y = mSmallLayoutParams.y;
            }
            // 宠物不贴边时弹窗的显示位置
            else if (isShowPetView) {
                messageWindowParams.x = mLayoutParams.x - MessageWindowView.viewWidth;
                messageWindowParams.y = mLayoutParams.y;
            }
            mWindowManager.addView(messageWindow, messageWindowParams);
            isMsgWindowView = true;
        }
    }

    /**
     * 移除微信消息弹窗
     */
    public static void removeMessageWindow() {
        if (messageWindow != null) {
            mWindowManager.removeView(messageWindow);
            messageWindow = null;
            isMsgWindowView = false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        isShowPetView = false;
        isShowSmallPetView = false;
        isMsgWindowView = false;

        //获取mWindowManager对象
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //取得窗口的宽度和高度
        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        //设置mLayoutParams对象
        mLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= 26) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mLayoutParams.format = PixelFormat.TRANSLUCENT;// 支持透明
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mLayoutParams.width = 200;//窗口的宽
        mLayoutParams.height = 200;//窗口的高
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
        //设置mSmallLayoutParams对象
        mSmallLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= 26) {
            mSmallLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mSmallLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mSmallLayoutParams.format = PixelFormat.TRANSLUCENT;// 支持透明
        mSmallLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mSmallLayoutParams.width = 100;//窗口的宽
        mSmallLayoutParams.height = 200;//窗口的高
        mSmallLayoutParams.gravity = Gravity.START | Gravity.TOP;
        mSmallLayoutParams.x = 0;
        mSmallLayoutParams.y = 0;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取pet_layout.xml布局文件
        mPetView = (LinearLayout) inflater.inflate(R.layout.pet_layout, null);
        //获取small_pet_layout.xml布局文件
        mSmallPetView = (LinearLayout) inflater.inflate(R.layout.small_pet_layout, null);


        mPetView.setOnTouchListener(new View.OnTouchListener() {
            private float startX;//拖动开始之前悬浮窗的x位置
            private float startY;//拖动开始之前悬浮窗的y位置
            private float lastX;//上个MotionEvent的x位置
            private float lastY;//上个MotionEvent的y位置
            private float nowX;//这次MotionEvent的x位置
            private float nowY;//这次MotionEvent的y位置
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    startX = mLayoutParams.x;
                    startY = mLayoutParams.y;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    nowX = event.getRawX();
                    nowY = event.getRawY();
                    mLayoutParams.x += (int) (nowX - lastX);
                    mLayoutParams.y += (int) (nowY - lastY);
                    // 更新宠物的位置
                    if (mLayoutParams.x < 0) {
                        mLayoutParams.x = 0;
                    } else if (mLayoutParams.x > PetService.this.screenWidth - mLayoutParams.width) {
                        mLayoutParams.x = PetService.this.screenWidth - mLayoutParams.width;
                    }
                    if (mLayoutParams.y < 0) {
                        mLayoutParams.y = 0;
                    } else if (mLayoutParams.y > PetService.this.screenHeight - mLayoutParams.height) {
                        mLayoutParams.y = PetService.this.screenHeight - mLayoutParams.height;
                    }

                    // 更新微信消息弹窗的位置
                    if (isMsgWindowView) {
                        if (mLayoutParams.x < PetService.this.screenWidth - MessageWindowView.viewWidth) {
                            messageWindowParams.x = mLayoutParams.x + mLayoutParams.width + 5;
                        } else {
                            messageWindowParams.x = mLayoutParams.x - MessageWindowView.viewWidth;
                        }
                        messageWindowParams.y = mLayoutParams.y;
                        mWindowManager.updateViewLayout(messageWindow, messageWindowParams);
                    }
                    mWindowManager.updateViewLayout(mPetView, mLayoutParams);
                    lastX = nowX;
                    lastY = nowY;
                } else if (action == MotionEvent.ACTION_UP) {
                    nowX = event.getRawX();
                    nowY = event.getRawY();
                    if (mLayoutParams.x <= 0 + 5) {
                        // 左贴边
                        mSmallLayoutParams.x = 0;
                        mSmallLayoutParams.y = mLayoutParams.y;
                        isShowPetView = false;
                        isShowSmallPetView = true;
                        mWindowManager.removeView(mPetView);
                        mWindowManager.addView(mSmallPetView, mSmallLayoutParams);
                    } else if (mLayoutParams.x >= PetService.this.screenWidth - mLayoutParams.width - 5) {
                        // 右贴边
                        mSmallLayoutParams.x = PetService.this.screenWidth - mSmallLayoutParams.width;
                        mSmallLayoutParams.y = mLayoutParams.y;
                        isShowPetView = false;
                        isShowSmallPetView = true;
                        mWindowManager.removeView(mPetView);
                        mWindowManager.addView(mSmallPetView, mSmallLayoutParams);
                    }
                }
                return false;
            }
        });

        mSmallPetView.setOnTouchListener(new View.OnTouchListener() {
            private float lastX;//上个MotionEvent的x位置
            private float lastY;//上个MotionEvent的y位置
            private float nowX;//这次MotionEvent的x位置
            private float nowY;//这次MotionEvent的y位置

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    nowX = event.getRawX();
                    nowY = event.getRawY();
                    mSmallLayoutParams.x += (int) (nowX - lastX);
                    mSmallLayoutParams.y += (int) (nowY - lastY);
                    if (mSmallLayoutParams.x < 0) {
                        mSmallLayoutParams.x = 0;
                    } else if (mSmallLayoutParams.x > PetService.this.screenWidth - mSmallLayoutParams.width) {
                        mSmallLayoutParams.x = PetService.this.screenWidth - mSmallLayoutParams.width;
                    }
                    if (mSmallLayoutParams.y < 0) {
                        mSmallLayoutParams.y = 0;
                    } else if (mSmallLayoutParams.y > PetService.this.screenHeight - mSmallLayoutParams.height) {
                        mSmallLayoutParams.y = PetService.this.screenHeight - mSmallLayoutParams.height;
                    }

                    // 更新微信消息弹窗的位置
                    if (isMsgWindowView) {
                        if (mSmallLayoutParams.x < PetService.this.screenWidth - MessageWindowView.viewWidth) {
                            messageWindowParams.x = mSmallLayoutParams.x + mSmallLayoutParams.width + 5;
                        } else {
                            messageWindowParams.x = mSmallLayoutParams.x - MessageWindowView.viewWidth;
                        }
                        messageWindowParams.y = mSmallLayoutParams.y;
                        mWindowManager.updateViewLayout(messageWindow, messageWindowParams);
                    }
                    mWindowManager.updateViewLayout(mSmallPetView, mSmallLayoutParams);
                    lastX = nowX;
                    lastY = nowY;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mSmallLayoutParams.x > 0 || mSmallLayoutParams.x < PetService.this.screenWidth - mSmallLayoutParams.width) {
                        mLayoutParams.x = mSmallLayoutParams.x;
                        mLayoutParams.y = mSmallLayoutParams.y;
                        isShowPetView = true;
                        isShowSmallPetView = false;
                        mWindowManager.removeView(mSmallPetView);
                        mWindowManager.addView(mPetView, mLayoutParams);
                    }
                }
                return false;
            }
        });

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (isShowPetView)
            mWindowManager.removeView(mPetView);
        if (isShowSmallPetView)
            mWindowManager.removeView(mSmallPetView);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isShowPetView = true;
        mWindowManager.addView(mPetView, mLayoutParams);
        return super.onStartCommand(intent, flags, startId);
    }
}
