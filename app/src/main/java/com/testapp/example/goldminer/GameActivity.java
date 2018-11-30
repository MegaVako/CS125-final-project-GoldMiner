package com.testapp.example.goldminer;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
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
    private GameView gameView;

    private int positionX = 100;
    private int positionY = 100;
    private static int vectorX = 4;
    private static int vectorY = 4;

    private static int maximumObjects = 8;
    //==============================================================
    // ## My variables ##
    //==============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gameView = (GameView) findViewById(R.id.canvas);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        try {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        } catch (NullPointerException e) {
            Log.i(TAG, "actionBar is null");
        }
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);
        setScreenSize();
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
        }, 0, 20);
        Log.i(TAG, "onTouchEvent");
        return true;
    }

    private void changePosition() {

    }

    private void setImageViewPosition() {

    }

    public static Intent createIntent(Context previousActivity) {
        Intent change = new Intent(previousActivity, GameActivity.class);
        return change;
    }
}
