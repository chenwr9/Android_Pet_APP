package com.javaone.onepet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.Random;
import java.util.Date;

public class PetService extends Service {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager.LayoutParams mSmallLayoutParams;
    private LinearLayout mPetView;//悬浮窗布局
    private LinearLayout mSmallPetView;//贴边后显示的悬浮窗布局
    private ImageView mPetView_image;
    private int screenWidth, screenHeight;
    private boolean isShowPetView, isShowSmallPetView;

    private int petType;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // pet type
        petType = 1;

        isShowPetView = false;
        isShowSmallPetView = false;
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

        mPetView.setBackgroundResource(R.drawable.pika_walk);
        mSmallPetView.setBackgroundResource(R.drawable.pika_walk1);
        final AnimationDrawable moveAnim = (AnimationDrawable) mPetView.getBackground();
        moveAnim.start();

        mPetView.setOnClickListener(new View.OnClickListener() {
            AnimationDrawable moveAnim;

            @Override
            public void onClick(View v) {
                Random rnd = new Random((new Date()).getTime());
                int[] actions;
                int randNum;
                switch (petType) {
                    case 1:
                        // moveAnim = (AnimationDrawable) mPetView.getBackground();
                        // moveAnim.stop();
                        actions = new int[]{
                                R.drawable.pika_walk, R.drawable.pika_backwalk, R.drawable.pika_ball,
                                R.drawable.pika_eat, R.drawable.pika_fall, R.drawable.pika_jump,
                                R.drawable.pika_lightning, R.drawable.pika_swim};
                        randNum = rnd.nextInt(actions.length);
                        mPetView.setBackgroundResource(actions[randNum]);
                        mSmallPetView.setBackgroundResource(R.drawable.pika_walk1);
                        moveAnim = (AnimationDrawable) mPetView.getBackground();
                        moveAnim.start();
                        break;
                    case 2:
                        // moveAnim = (AnimationDrawable) mPetView.getBackground();
                        // moveAnim.stop();
                        actions = new int[]{
                                R.drawable.kid_walk, R.drawable.kid_bird, R.drawable.kid_change,
                                R.drawable.kid_hang, R.drawable.kid_leg, R.drawable.kid_look,
                                R.drawable.kid_skate, R.drawable.kid_vertical};
                        randNum = rnd.nextInt(actions.length);
                        mPetView.setBackgroundResource(actions[randNum]);
                        mSmallPetView.setBackgroundResource(R.drawable.kid_walk1);
                        moveAnim = (AnimationDrawable) mPetView.getBackground();
                        moveAnim.start();
                        break;
                    case 3:
                        // moveAnim = (AnimationDrawable) mPetView.getBackground();
                        // moveAnim.stop();
                        actions = new int[]{
                                R.drawable.meiko_walk, R.drawable.meiko_look, R.drawable.meiko_leg,
                                R.drawable.meiko_hand, R.drawable.meiko_fall};
                        randNum = rnd.nextInt(actions.length);
                        mPetView.setBackgroundResource(actions[randNum]);
                        mSmallPetView.setBackgroundResource(R.drawable.meiko_walk1);
                        moveAnim = (AnimationDrawable) mPetView.getBackground();
                        moveAnim.start();
                        break;
                    case 4:
                        mPetView.setBackgroundResource(R.drawable.bear);
                        mSmallPetView.setBackgroundResource(R.drawable.bear);
                        break;
                    default:
                        break;
                }
            }
        });

        mPetView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AnimationDrawable moveAnim;
                switch(petType) {
                    case 1:
                        mPetView.setBackgroundResource(R.drawable.kid_walk);
                        mSmallPetView.setBackgroundResource(R.drawable.kid_walk1);
                        moveAnim = (AnimationDrawable) mPetView.getBackground();
                        moveAnim.start();
                        petType += 1;
                        break;
                    case 2:
                        mPetView.setBackgroundResource(R.drawable.meiko_walk);
                        mSmallPetView.setBackgroundResource(R.drawable.meiko_walk1);
                        moveAnim = (AnimationDrawable) mPetView.getBackground();
                        moveAnim.start();
                        petType += 1;
                        break;
                    case 3:
                        mPetView.setBackgroundResource(R.drawable.bear);
                        mSmallPetView.setBackgroundResource(R.drawable.bear);
                        petType += 1;
                        break;
                    case 4:
                        mPetView.setBackgroundResource(R.drawable.pika_walk);
                        mSmallPetView.setBackgroundResource(R.drawable.pika_walk1);
                        moveAnim = (AnimationDrawable) mPetView.getBackground();
                        moveAnim.start();
                        petType = 1;
                        break;
                    default:
                        break;
                }
                /**
                 * 点击消息是否进行拦截？
                 * 如果是true   不会触发后续事件
                 * 如果是false  会触发后续事件 比如说单击事件
                 */
                return true;
            }
        });
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
