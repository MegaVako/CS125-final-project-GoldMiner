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
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GameActivity extends AppCompatActivity {
    //==============================================================
    // ## My variables ##
    //==============================================================
    private static final String TAG = "GoldMiner/Game";
    private static int screenHeight;
    private static int screenWidth;

    private static GameView gameView;
    private static Button nextGameBtn;
    private static Button quitGameBtn;
    private PopupWindow popupWindow;
    private static GameActivity gameActivity;
    private TextView timeTrackingTextView;
    private ImageButton pauseBtn;
    private static int savedScore = 0;
    //==============================================================
    // ## My variables ##
    //==============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {

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

        gameActivity = GameActivity.this;
        //pauseBtn = (ImageButton) findViewById(R.id.onPause);
        timeTrackingTextView = (TextView) findViewById(R.id.displayTimeText);
        Log.i(TAG, "onCreate: check pauseBtn " + pauseBtn);
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

    public void onPopup(GameActivity gameActivity, int currentScore) {
        Log.i(TAG, "onPopup: is called ");
        LayoutInflater layoutInflater = (LayoutInflater) gameActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.gameover_popup,null);

        savedScore = currentScore;

        //instantiate popup window
        popupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ((TextView) popupWindow.getContentView().findViewById(R.id.popupText)).setText("Your score " + currentScore);
        popupWindow.showAtLocation(gameView, Gravity.CENTER, 0, 0);

        nextGameBtn = (Button) customView.findViewById(R.id.nextGameBtn);
        nextGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Log.d(TAG, "onClick: next game");
                createNewGame();
            }
        });
        quitGameBtn = (Button) customView.findViewById(R.id.quitGameBtn);
        quitGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Log.d(TAG, "onClick: quit game");
                quitToMain();
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
        resetSavedScore();
    }
    private void quitToMain() {
        Intent change = MainActivity.createIntent(gameActivity);
        startActivity(change);
    }
    public static int getScore() {
        return savedScore;
    }
    private static void resetSavedScore() {
        savedScore = 0;
    }
}
