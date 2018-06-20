package com.anji.plus.myfloatwindowdemo;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by SummerChen on 2018/6/20.
 */

public class FloatingButtonService extends Service {
    public static boolean isStarted = false;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private Button button;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.height = 500;
        layoutParams.width = 300;
        layoutParams.x = 300;
        layoutParams.y = 300;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showFloatWindow() {
        if (Settings.canDrawOverlays(this)) {
            button = new Button(getApplicationContext());
            button.setText("haaha");
            button.setBackgroundColor(Color.BLUE);
            windowManager.addView(button, layoutParams);

            button.setOnTouchListener(new FloatTouchListener());

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class FloatTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x= (int) motionEvent.getRawX();
                    y= (int) motionEvent.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX= (int) motionEvent.getRawX();
                    int nowY= (int) motionEvent.getRawY();
                    int moveX=nowX-x;
                    int moveY=nowY-y;
                    x=nowX;
                    y=nowY;
                    layoutParams.x=layoutParams.x+moveX;
                    layoutParams.y=layoutParams.y+moveY;
                    windowManager.updateViewLayout(button,layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
