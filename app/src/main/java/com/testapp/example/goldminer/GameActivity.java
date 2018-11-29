package com.testapp.example.goldminer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    //==============================================================
    // ## My variables ##
    //==============================================================
    private static final String TAG = "GoldMiner/Game";
    private static int screenHeight;
    private static int screenWidth;

    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private ImageView tempImageView;

    private int positionX = 100;
    private int positionY = 100;
    //==============================================================
    // ## My variables ##
    //==============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setScreenSize();
        tempImageView = findViewById(R.id.tempObject);
    }

    private void setScreenSize() {
        try {
            WindowManager wm = getWindowManager();
            Display dispaly = wm.getDefaultDisplay();
            Point size = new Point();
            dispaly.getSize(size);

            screenHeight = size.y;
            screenWidth = size.x;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changePosition();
                    }
                });
            }
        }, 0, 1000);
        Log.i(TAG, "onTouchEvent");
        return true;
    }

    private void changePosition() {
        positionX += 4;
        positionY += 4;
        setImageViewPosition();
    }

    private void setImageViewPosition() {
        tempImageView.setX((float) positionX);
        tempImageView.setY((float) positionY);
    }

    public static Intent createIntent(Context previousActivity) {
        Intent change = new Intent(previousActivity, GameActivity.class);
        return change;
    }
}
