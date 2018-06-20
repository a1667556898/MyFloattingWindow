package com.anji.plus.myfloatwindowdemo;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by SummerChen on 2018/6/20.
 */

public class FloatingImgService extends Service {
    public static boolean isStarted = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private View displayView;

    private int[] images;
    private int imageIndex = 0;

    private Handler changeImageHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 500;
        layoutParams.height = 500;
        layoutParams.x = 300;
        layoutParams.y = 300;
        images = new int[]{
                R.drawable.image_01,
                R.drawable.image_02,
                R.drawable.image_03,
                R.drawable.image_04,
                R.drawable.image_05

        };
        changeImageHandler = new Handler(getMainLooper(), changeImageCallback);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)){
           displayView= LayoutInflater.from(this).inflate(R.layout.image_display,null);
           displayView.setOnTouchListener(new FloatingOnTouchListener());
            ImageView imageView=displayView.findViewById(R.id.img);
            imageView.setImageResource(images[imageIndex]);
            windowManager.addView(displayView,layoutParams);
            changeImageHandler.sendEmptyMessageDelayed(0,2000);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
    private Handler.Callback changeImageCallback=new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what==0){
                imageIndex++;
                if (imageIndex>=5){
                    imageIndex=0;
                }
                if (displayView!=null){
                    ((ImageView)displayView.findViewById(R.id.img)).setImageResource(images[imageIndex]);
                }
                changeImageHandler.sendEmptyMessageDelayed(0,2000);
            }
            return false;
        }
    };
}
