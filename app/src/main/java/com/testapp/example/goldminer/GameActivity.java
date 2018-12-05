package com.testapp.example.goldminer;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class GameActivity extends AppCompatActivity {
    //==============================================================
    // ## My variables ##
    //==============================================================
    private static final String TAG = "GoldMiner/Game";
    private static int screenHeight;
    private static int screenWidth;

    private GameView gameView;

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
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
            screenWidth = size.x;
            Log.d(TAG, "setScreenSize: h/w = "+ screenHeight + "/" + screenWidth);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static double[] getScreenSize() {
        return new double[]{screenWidth, screenHeight};
    }

    public static Intent createIntent(Context previousActivity) {
        Intent change = new Intent(previousActivity, GameActivity.class);
        return change;
    }
}
