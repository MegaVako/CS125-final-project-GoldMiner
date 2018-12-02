package com.testapp.example.goldminer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class GameView extends View {
    private static final int MAXIMUM_NUMBER_OF_STONES = 8;
    private static final int STONE_TOP_BOUND = 700;
    private static final int STONE_SPACE = 200;
    private static final int STONE_LEFT_BOUND = 10;
    private static final int STONE_WIDTH_MAXIMUM = 90;
    private static final int STONE_HEIGHT_MAXIMUM = 50;
    private static final int STONE_HEIGHT_MAXIMUM_DEVIATION = 200;
    private static final int MINER_LEFT = 800;
    private static final int MINER_WIDTH = 200;
    private static final int MINER_TOP = 50;
    private static final int MINER_LENGTH = 80;
    private static final int HOOK_RADIUS = 30;
    private static final int REFRESH_RATE = 20; //ms

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private String TAG = "GoldMiner/GameView";
    private Rect[] stones = new Rect[MAXIMUM_NUMBER_OF_STONES];
    private final Rect miner = new Rect(MINER_LEFT, MINER_TOP, MINER_LEFT + MINER_WIDTH, MINER_LENGTH);
    private float hookPositionX = 800;
    private float hookPositionY = 50;
    /** Time tracking textView */
    private TextView timeTrackingTextView;
    /** timeTracker for hook Position */
    private double timeTracker = 0;

    /** Hook speed */
    private double direction = 0.01;

    /** timeCounter for timer on screen */
    private int timeCounter = 20;

    /** Track moving */
    private boolean isMoving = false;

    /** Track extending hook */
    private boolean isExtending = false;

    public GameView(Context context) {
        super(context);
        init();
    }
    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
        timeTrackingTextView = findViewById(R.id.timeTracker);
        Log.i(TAG, "GameView: check timeTrackingTextView " + timeTrackingTextView);
        timeTrackingTextView.setText(String.valueOf(timeCounter));
    }
    private void init() {
        initPaint();
        initStones();
        setInitTimeCounter();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw");
        mPaint.setColor(Color.rgb(0,0,0));
        canvas.drawRect((float)0, (float)0, (float)2000, (float)2000, mPaint);
        mPaint.setColor(Color.rgb(250,214,0));
        for (Rect rect : stones) {
            canvas.drawRect(rect, mPaint);
        }
        //Draw miner
        mPaint.setColor(Color.rgb(200,100,200));
        canvas.drawRect(miner, mPaint);
        //Draw hook
        mPaint.setColor(Color.rgb(255,255,255));
        canvas.drawCircle(hookPositionX, hookPositionY, HOOK_RADIUS, mPaint);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }
    private void initStones() {
        for (int i = 0; i < stones.length; i++) {
            int temp = (int) (Math.random() * STONE_HEIGHT_MAXIMUM_DEVIATION);
            if (temp % 2 == 0) {
                temp = -temp;
            }
            stones[i] = new Rect(
                    STONE_SPACE * i + STONE_LEFT_BOUND + temp,
                    STONE_TOP_BOUND + temp,
                    STONE_SPACE * i + STONE_WIDTH_MAXIMUM + STONE_LEFT_BOUND + temp,
                    STONE_TOP_BOUND + STONE_HEIGHT_MAXIMUM + temp);
            Log.i(TAG, "initStones: temp/dev = " + temp);
        }
    }
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(4f);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isMoving && timeCounter > 0) {
            movementRunnable.run();
            Log.i(TAG, "onTouchEvent: do");
        } else if (isMoving && timeCounter > 0) {

        }
        return true;
    }
    private void doMovement() {
        float tempX = (float) (Math.sin(timeTracker));
        float tempY = (float) (Math.cos(timeTracker));
        hookPositionX += tempX;
        if (timeTracker > Math.PI) {
            hookPositionY -= tempY;
        } else {
            hookPositionY += tempY;
        }
        //y = sqrt(radius - x^2)
        timeTracker += direction;
        if (timeTracker >= 2 * Math.PI) {
            timeTracker = 0;
        }
        //Log.d(TAG, "doMovement: hpX/hpY -- > " + hookPositionX + "/" + hookPositionY) ;
    }

    //Animation stuff
    /**
     * Call any necessary methods under run()
     */
    private Handler handler = new Handler(Looper.getMainLooper());
    Runnable movementRunnable = new Runnable(){
        public void run(){
            //Put your methods here
            doMovement(); //do hook movement
            if (!isExtending) {
                //Put extend hook code here
            }
            //calculateTime();
            invalidate(); //will trigger the onDraw
            handler.postDelayed(this, REFRESH_RATE);
        }
    };

    private void calculateTime() {
        int temp = (int) Math.abs(timeTracker / direction);
        if ((temp * REFRESH_RATE) % 1000 == 0) {
            timeCounter--;
            timeTrackingTextView.setText(String.valueOf(timeCounter));
            Log.d(TAG, "calculateTime: " + timeCounter);
        }
        if (temp == 0) {
            handler.removeCallbacks(movementRunnable);
        }
    }

    private void setInitTimeCounter() {
        timeCounter = 20;
    }
}
