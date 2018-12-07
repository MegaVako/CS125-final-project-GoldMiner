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

import java.util.ArrayList;

public class GameView extends View {
    private static final int MAXIMUM_NUMBER_OF_BLOCKS = 8;
    private static final int STONE_TOP_BOUND = 700;
    private static final int STONE_SPACE = 200;
    private static final int STONE_LEFT_BOUND = 10;
    private static final int STONE_WIDTH_MAXIMUM = 90;
    private static final int STONE_HEIGHT_MAXIMUM = 100;
    private static final int STONE_HEIGHT_MAXIMUM_DEVIATION = 200;
    private static final int MINER_LEFT = 800;
    private static final int MINER_WIDTH = 200;
    private static final int MINER_TOP = 50;
    private static final int MINER_LENGTH = 80;
    private static final int HOOK_RADIUS = 30;
    private static final int REFRESH_RATE = 20; //ms
    private static final double HOOK_SWING_RATE = (Math.PI/100);
    private static final double HOOK_EXTEND_RATE = 0.1;
    private static final double GAME_TIME_COUNTER_RATE = 0.01;
    private static final float SWING_INDEX = (float) (100 * HOOK_SWING_RATE);
    private static final Rect miner = new Rect(MINER_LEFT, MINER_TOP, MINER_LEFT + MINER_WIDTH, MINER_LENGTH);
    private static final Rect BACKGROUND = new Rect(0, 0, 2000,2000);
    private static final int HOOK_INIT_Y = 50;
    private static final int HOOK_INIT_X = 800;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private String TAG = "GoldMiner/GameView";

    private ArrayList<BlockData> blocks = new ArrayList<>(MAXIMUM_NUMBER_OF_BLOCKS);
    private Rect capturedBlock = new Rect(1,2,3,4);
    private float hookPositionX = 800;
    private float hookPositionY = 50;
    /** Time tracking textView */
    private TextView timeTrackingTextView;

    private double gameTimeCounter = 0;
    
    private int displayTime = 0;

    private static double slopeX = 0;

    private static double slopeY = 0;

    private hookStatus hook = hookStatus.stop;

    private static double firePositionY = 0;
    private static double firePositionX = 0;

    private static double screenHeight = 0;
    private static double screenWidth = 0;

    private boolean isCaptured = false;

    private ArrayList<BlockData> onPathBlock = new ArrayList<>();

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
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(BACKGROUND, mPaint);
        for (BlockData b : blocks) {
            mPaint.setColor(b.getColor());
            canvas.drawRect(b.getBlock(), mPaint);
        }
        //Draw miner
        mPaint.setColor(Color.rgb(200,100,200));
        canvas.drawRect(miner, mPaint);
        //Draw string from hook to miner center
        mPaint.setStrokeWidth(4f);
        mPaint.setColor(Color.rgb(255, 0, 0));
        canvas.drawLine(hookPositionX, hookPositionY, (MINER_LEFT + MINER_WIDTH/2), MINER_TOP, mPaint);
        //Draw captured block
        if (isCaptured && onPathBlock.size() == 1) {
            mPaint.setColor(onPathBlock.get(0).getColor());
            canvas.drawRect(capturedBlock, mPaint);
        }
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
        for (int i = 0; i < MAXIMUM_NUMBER_OF_BLOCKS; i++) {
            int temp = (int) (Math.random() * STONE_HEIGHT_MAXIMUM_DEVIATION);
            if (temp % 2 == 0) {
                temp = -temp;
            }
            int left = STONE_SPACE * i + STONE_LEFT_BOUND + temp;
            int top = STONE_TOP_BOUND + temp;
            int right = STONE_SPACE * i + STONE_WIDTH_MAXIMUM + STONE_LEFT_BOUND + temp;
            int bot = STONE_TOP_BOUND + STONE_HEIGHT_MAXIMUM + temp;
            blocks.add(new Block(left, top, right, bot));
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
                setScreenSize();
                movementRunnable.run();
                hook = hookStatus.swinging;
                return true;
            } else if (hook == hookStatus.swinging && displayTime > 0) {
                    isCaptured = false;
                    onPathBlock.clear();
                    setExtendSlope();
                    setFirePosition();
                    setOnPathBlock();
                    if (onPathBlock.size() == 0) {
                        Log.d(TAG, "onTouchEvent: nothing is on my path");
                    } else {
                        BlockData b = onPathBlock.get(0);
                        Log.d(TAG, "onTouchEvent: this is on my path --> x/y "
                                + b.getPositionX() + "/" + b.getPositionY());
                    }
                    hook = hookStatus.extending;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
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
    private double onSwing(double swingCounter) {
        float tempX = (float) (Math.sin(swingCounter));
        float tempY = (float) (Math.cos(swingCounter));
        //too much temp values --> need to fix
        float tempIndex = (float) (MINER_LENGTH / (22));
        hookPositionX += tempIndex * tempX;
        if (swingCounter >= Math.PI) {
            hookPositionY -= tempIndex * tempY;
        } else {
            hookPositionY += tempIndex * tempY;
        }
        //y = sqrt(radius - x^2)
        swingCounter += HOOK_SWING_RATE;
        if (swingCounter >= 2 * Math.PI) {
            swingCounter = 0;
            onHookReCenter();
            Log.i(TAG, "onSwing: hookX/Y" + hookPositionX + "/" + hookPositionY);
        }
        return swingCounter;
        //Log.d(TAG, "doMovement: hpX/hpY -- > " + hookPositionX + "/" + hookPositionY) ;
    }
    private double onExtend(double extendCounter) {
        if (hook == hookStatus.extending) {
            if (onPathBlock.size() == 1) {
                if (!isCaptured && onGrab()) {
                    isCaptured = true;
                }
            }
            hookPositionX += (HOOK_EXTEND_RATE * slopeX);
            hookPositionY += (HOOK_EXTEND_RATE * slopeY);
        } else if (hook == hookStatus.retracting) {
            hookPositionX -= (HOOK_EXTEND_RATE * slopeX);
            hookPositionY -= (HOOK_EXTEND_RATE * slopeY);
            if (isCaptured) {
                BlockData tempB = onPathBlock.get(0);
                capturedBlock.set((int) hookPositionX, (int) hookPositionY,
                        (int) (hookPositionX + tempB.getWidth()), (int) (hookPositionY + tempB.getHeight()));
                Log.i(TAG, "onExtend: captured moving");
            }
        }
        if ((hookPositionY > screenHeight || hookPositionX < 0) || (hookPositionX < 0 || hookPositionX > screenWidth)) {
            hook = hookStatus.retracting;
        }
        if (hookPositionY <= firePositionY) {
            hook = hookStatus.swinging;
            isCaptured = false;
            onPathBlock.clear();
            Log.i(TAG, "onExtend: done retracting " + onPathBlock.size());
        }
        return extendCounter;
    }

    private void setInitTimeCounter() {
        displayTime = 100;
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
        firePositionY = hookPositionY;
        firePositionX = hookPositionX;
    }

    private void setScreenSize() {
        double temp[] = GameActivity.getScreenSize();
        screenWidth = temp[0];
        screenHeight = temp[1];
    }
    private void setOnPathBlock() {
        onPathBlock.clear();
        for (BlockData b : blocks) {
            if (b.isOnPath()) {
                onPathBlock.add(b);
            }
        }
        if (onPathBlock.size() > 1) {
            double smallestY = onPathBlock.get(0).getPositionY();
            BlockData closest = onPathBlock.get(0);
            for (BlockData b : onPathBlock) {
                if (b.getPositionY() < smallestY) {
                    closest = b;
                }
            }
            onPathBlock.clear();
            onPathBlock.add(closest);
        }
    }

    private boolean onGrab() {
        double distance = calculateDistance(onPathBlock.get(0));
        if (distance <= HOOK_RADIUS + 50) {
            Log.i(TAG, "onGrab: is grabbed");
            hook = hookStatus.retracting;
            for (BlockData b : blocks) {
                if (b.equals(onPathBlock.get(0))) {
                    blocks.remove(b);
                    break;
                }
            }
            return true;
        }
        Log.i(TAG, "onGrab: not hit d = " + distance);
        return false;
    }

    private double calculateDistance(BlockData blockData) {
        double distanceX = blockData.getCenterX() - hookPositionX;
        double distanceY = blockData.getCenterY() - hookPositionY;
        return Math.sqrt(distanceX * distanceX + distanceY * distanceY);
    }

    private void onHookReCenter() {
        hookPositionY = HOOK_INIT_Y;
        hookPositionX = HOOK_INIT_X;
    }

    public static double getOnPathRange() {
        return (screenHeight - MINER_TOP) / (slopeY / slopeX);
    }

    public static double getMinerCenter() {
        return (MINER_LEFT + MINER_WIDTH/2);
    }

    public static double getOnPathSlope() {
        return (slopeY / slopeX);
    }

    public static double getFirePositionX() {
        return firePositionX;
    }
}
