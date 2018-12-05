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
    private static final double HOOK_SWING_RATE = 0.01;
    private static final double HOOK_EXTEND_RATE = 0.1;
    private static final double GAME_TIME_COUNTER_RATE = 0.01;

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

    private double gameTimeCounter = 0;
    
    private int displayTime = 0;

    private double slopeX = 0;

    private double slopeY = 0;

    private hookStatus hook = hookStatus.stop;

    private double firePositionX = 0;
    private double firePositionY = 0;

    public GameView(Context context) {
        super(context);
        init();
    }
    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
        timeTrackingTextView = findViewById(R.id.timeTracker);
        Log.i(TAG, "GameView: check timeTrackingTextView " + timeTrackingTextView);
        timeTrackingTextView.setText(String.valueOf(gameTimeCounter));
    }
    private void init() {
        initPaint();
        initStones();
        setInitTimeCounter();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
        //Draw hook to miner center
        mPaint.setColor(Color.rgb(255, 0, 0));
        canvas.drawLine(hookPositionX, hookPositionY, (MINER_LEFT + MINER_WIDTH/2), MINER_TOP, mPaint);
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
        Log.i(TAG, "onTouchEvent: clicked called");
        if (displayTime <= 0) {
            return false;
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (hook == hookStatus.stop && displayTime > 0) {
                movementRunnable.run();
                hook = hookStatus.swinging;
                return true;
            } else if (hook == hookStatus.swinging && displayTime > 0) {
                    setExtendSlope();
                    setFirePosition();
                    hook = hookStatus.extending;

                return true;
            } else {
                //isMoving = false;
                return false;
            }
        } else {
            return false;
        }
    }
    private double onSwing(double swingCounter) {
        float tempX = (float) (Math.sin(swingCounter));
        float tempY = (float) (Math.cos(swingCounter));
        hookPositionX += tempX;
        if (swingCounter > Math.PI) {
            hookPositionY -= tempY;
        } else {
            hookPositionY += tempY;
        }
        //y = sqrt(radius - x^2)
        swingCounter += HOOK_SWING_RATE;
        if (swingCounter >= 2 * Math.PI) {
            swingCounter = 0;
        }
        return swingCounter;
        //Log.d(TAG, "doMovement: hpX/hpY -- > " + hookPositionX + "/" + hookPositionY) ;
    }

    //Animation stuff
    /**
     * Call any necessary methods under run()
     */
    private Handler handler = new Handler(Looper.getMainLooper());
    Runnable movementRunnable = new Runnable(){
        double swingTimeCounter = 0;
        double extendTimeCounter = 0;
        public void run(){
            gameTimeCounter += GAME_TIME_COUNTER_RATE;
            if (hook == hookStatus.swinging) {
                swingTimeCounter = onSwing(swingTimeCounter); //do hook movement
            } else if (hook == hookStatus.extending || hook == hookStatus.retracting) {
                extendTimeCounter = onExtend(extendTimeCounter);
            }
            calculateTime();
            invalidate(); //will trigger the onDraw
            if (displayTime > 0) {
                handler.postDelayed(this, REFRESH_RATE);
            } else {
                Log.d(TAG, "calculateTime: time is up");
                handler.removeCallbacks(movementRunnable);
            }
        }
    };

    private void calculateTime() {
        int temp = (int) Math.abs(gameTimeCounter / GAME_TIME_COUNTER_RATE);
        if ((temp * REFRESH_RATE) % 1000 == 0) {
            displayTime--;
            //timeTrackingTextView.setText(String.valueOf(timeCounter));
            Log.d(TAG, "calculateTime: " +  displayTime);
        }
    }

    private double onExtend(double extendCounter) {
        if (hook == hookStatus.extending) {
            hookPositionX += (HOOK_EXTEND_RATE * slopeX);
            hookPositionY += (HOOK_EXTEND_RATE * slopeY);
        } else if (hook == hookStatus.retracting) {
            hookPositionX -= (HOOK_EXTEND_RATE * slopeX);
            hookPositionY -= (HOOK_EXTEND_RATE * slopeY);
        }
        if ((hookPositionY > 1200 || hookPositionX < 0) || (hookPositionX < 0 || hookPositionX > 1960)) {
            hook = hookStatus.retracting;
        }
        if (hookPositionY <= firePositionY) {
            hook = hookStatus.swinging;
        }
        return extendCounter;
    }

    private void setInitTimeCounter() {
        displayTime = 20;
    }

    private void setExtendSlope() {
        slopeX = (hookPositionX - (MINER_LEFT + MINER_WIDTH/2));
        slopeY = (hookPositionY - MINER_TOP);
        Log.d(TAG, "setExtendSlope: slopeX/slopeY = " + slopeX + "/" + slopeY);
    }

    private enum hookStatus {
        extending, retracting, swinging, stop
    }

    private void setFirePosition() {
        firePositionX = hookPositionX;
        firePositionY = hookPositionY;
    }
}
