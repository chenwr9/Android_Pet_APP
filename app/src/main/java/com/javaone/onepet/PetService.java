package com.javaone.onepet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PetService extends Service {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private boolean flag = true;//标记悬浮窗是否已经显示
    private LinearLayout mPetView;//悬浮窗布局
    private ImageView mPetView_image;
    //悬浮窗是否是打开状态
    private boolean open = false ;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //获取mWindowManager对象
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        //窗口高度
        int screenHeight = dm.heightPixels;
        //获取LayoutParams对象
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mLayoutParams.format = PixelFormat.TRANSLUCENT;// 支持透明
        //mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mLayoutParams.width = 550;//窗口的宽
        mLayoutParams.height = 400;//窗口的高
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
        mLayoutParams.x = (screenWidth - 550) / 2;
        mLayoutParams.y = 1000;
        //mLayoutParams.alpha = 0.1f;//窗口的透明度
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取pet_layout.xml布局文件
        mPetView = (LinearLayout) inflater.inflate(R.layout.pet_layout, null);
//        mWindowManager.addView(mPetView, mLayoutParams);

        mPetView.setOnTouchListener(new View.OnTouchListener() {
            private float startX;//拖动开始之前悬浮窗的x位置
            private float startY;//拖动开始之前悬浮窗的y位置
            private float lastX;//上个MotionEvent的x位置
            private float lastY;//上个MotionEvent的y位置
            private float nowX;//这次MotionEvent的x位置
            private float nowY;//这次MotionEvent的y位置
            private float translateX;//每次拖动产生MotionEvent事件之后窗口所要移动的x轴距离
            private float translateY;//每次拖动产生MotionEvent事件的时候窗口所要移动的x轴距离
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
                    translateX = (int) (nowX - lastX);
                    translateY = (int) (nowY - lastY);
                    mLayoutParams.x += translateX;
                    mLayoutParams.y += translateY;
                    //更新布局
                    mWindowManager.updateViewLayout(mPetView, mLayoutParams);
                    lastX = nowX;
                    lastY = nowY;
                }
                return false;
            }
        });

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mWindowManager.removeView(mPetView);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        mWindowManager.addView(mPetView, mLayoutParams);
        return super.onStartCommand(intent, flags, startId);
    }
}
