package com.testapp.example.goldminer;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {
    //==============================================================
    // ## My variables ##
    //==============================================================
    private static final String TAG = "GoldMiner/Game";
    private static int screenHeight;
    private static int screenWidth;

    private static GameView gameView;
    private static Button closePopupBtn;
    private PopupWindow popupWindow;
    private static GameActivity gameActivity;
    private TextView timeTrackingTextView;
    //==============================================================
    // ## My variables ##
    //==============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gameActivity = GameActivity.this;
        gameView = (GameView) findViewById(R.id.canvas);
        timeTrackingTextView = (TextView) findViewById(R.id.displayTimeText);
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
            Log.i(TAG, "actionBar is null, " + e.getMessage());
        }
        super.onCreate(savedInstanceState);
        gameView = new GameView(this, this);
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

    public void onPopup(GameActivity gameActivity) {
        Log.i(TAG, "onPopup: is called ");
        LayoutInflater layoutInflater = (LayoutInflater) gameActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.gameover_popup,null);

        closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);

        //instantiate popup window
        popupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        //display the popup window
        popupWindow.showAtLocation(gameView, Gravity.CENTER, 0, 0);
        closePopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Log.d(TAG, "onClick: after dismiss");
                createNewGame();
            }
        });
    }
    public void setTimerText(int gameTimeCounter) {
        Log.i(TAG, "GameView: check timeTrackingTextView " + timeTrackingTextView);
        timeTrackingTextView.setText(gameTimeCounter);
    }
    private void createNewGame() {
        int tempScore = gameView.getScore();
        gameView = (GameView) findViewById(R.id.canvas);
        gameView = new GameView(GameActivity.this, this, tempScore);
        setContentView(gameView);
    }
}
