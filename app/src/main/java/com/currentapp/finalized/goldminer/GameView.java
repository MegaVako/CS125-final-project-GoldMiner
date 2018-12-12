package com.currentapp.finalized.goldminer;

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
import android.widget.ImageButton;

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

    private static final int MINER_COLOR = Color.rgb(200,100,200);
    private static final int HOOK_COLOR = Color.rgb(255,255,255);
    private static final int HOOK_STRING_COLOR = Color.rgb(255,0, 0);
    private static final int TIMER_COLOR = Color.WHITE;

    private static final int GAME_TIME = 30;
    private static final int MAXIMUM_ON_GRAB_DEV = 55;
    private static final String TAG = "GoldMiner/GameView";
    private final float TIMER_TEXT_SIZE = 48f;
    private static final int TIMER_X = 1700;
    private static final int TIMER_Y = 50;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;


    private ArrayList<BlockData> blocks = new ArrayList<>(MAXIMUM_NUMBER_OF_BLOCKS);
    private Rect capturedBlock = new Rect(1,2,3,4);
    private float hookPositionX = 800;
    private float hookPositionY = 50;

    private double gameTimeCounter = 0;
    
    private int displayTime = 0;

    private static double slopeX = 0;

    private static double slopeY = 0;

    private hookStatus hook = hookStatus.stop;

    /** Record fire position X/Y */
    private static double firePositionY = 0;
    private static double firePositionX = 0;

    /** Record screen size */
    private static double screenHeight = 0;
    private static double screenWidth = 0;

    /** Indentify if captured block */
    private boolean isCaptured = false;

    /** List of possible hit blocks. NOTE: ONLY BLOCK WITH LOWEST Y (TOP) WILL BE GRABBED */
    private ArrayList<BlockData> onPathBlock = new ArrayList<>();

    private int playerScore;

    private ImageButton pauseBtn;

    private GameActivity gameActivity;



    public GameView(Context context, GameActivity gameActivity) {
        super(context);
        this.gameActivity = gameActivity;
        playerScore = 0;
        init(context);
    }
    public GameView(Context context, GameActivity gameActivity, int previousScore) {
        super(context);
        this.gameActivity = gameActivity;
        playerScore = previousScore;
        init(context);
    }
    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }
    private void init(Context context) {
        initPaint();
        initStones();
        setInitTimeCounter();
        initPauseBtn(context);
        mPaint.setTextSize(TIMER_TEXT_SIZE);
    }
    private void initPauseBtn(Context context) {
        pauseBtn = new ImageButton(context);
        pauseBtn.setPadding(100, 200, 500, 600);
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: pauseBtn clicked");
            }
        });
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(BACKGROUND, mPaint);

        //Draw blocks
        for (BlockData b : blocks) {
            mPaint.setColor(b.getColor());
            canvas.drawRect(b.getBlock(), mPaint);
        }

        //Draw miner
        mPaint.setColor(MINER_COLOR);
        canvas.drawRect(miner, mPaint);
        //Draw string from hook to miner center
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(HOOK_STRING_COLOR);
        canvas.drawLine(hookPositionX, hookPositionY, (MINER_LEFT + MINER_WIDTH/2), MINER_TOP, mPaint);

        //Draw captured block
        if (isCaptured && onPathBlock.size() == 1) {
            mPaint.setColor(onPathBlock.get(0).getColor());
            canvas.drawRect(capturedBlock, mPaint);
        }

        //Draw hook
        mPaint.setColor(HOOK_COLOR);
        canvas.drawCircle(hookPositionX, hookPositionY, HOOK_RADIUS, mPaint);

        //Draw Time
        mPaint.setColor(TIMER_COLOR);
        canvas.drawText(String.valueOf(displayTime), TIMER_X, TIMER_Y, mPaint);
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
                onFinishGame();
            }
        }
    };

    private void calculateTime() {
        int temp = (int) Math.abs(gameTimeCounter / GAME_TIME_COUNTER_RATE);
        if ((temp * REFRESH_RATE) % 1000 == 0) {
            displayTime--;
            //NULL POINTER !!
            //gameActivity.setTimerText(displayTime);
            Log.d(TAG, "calculateTime: " +  displayTime);
            if (displayTime < 0) {
                onFinishGame();
            }
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
                updateCapturedBlock();
            }
        }
        if ((hookPositionY > screenHeight || hookPositionX < 0) || (hookPositionX < 0 || hookPositionX > screenWidth)) {
            hook = hookStatus.retracting;
        }
        if (hookPositionY <= firePositionY) {
            onReturnToSwing();
            if (blocks.size() == 0) {
                //Game is finished
                Log.i(TAG, "onExtend: Game is over// win");
                onFinishGame();
            }
        }
        return extendCounter;
    }

    private void setInitTimeCounter() {
        displayTime = GAME_TIME;
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
            double smallestDistanceY = findIntersectionY(onPathBlock.get(0));
            BlockData closest = onPathBlock.get(0);
            for (BlockData b : onPathBlock) {
                double tempDistanceY = findIntersectionY(b);
                if (tempDistanceY < smallestDistanceY) {
                    closest = b;
                    smallestDistanceY = tempDistanceY;
                    Log.i(TAG, "setOnPathBlock: changed");
                }
                Log.i(TAG, "setOnPathBlock: smallestDistanceY " + smallestDistanceY);
            }
            onPathBlock.clear();
            onPathBlock.add(closest);
        }
    }

    private boolean onGrab() {
        double distance = calculateDistance(onPathBlock.get(0));
        if (distance <= HOOK_RADIUS + MAXIMUM_ON_GRAB_DEV) {
            Log.i(TAG, "onGrab: is grabbed");
            hook = hookStatus.retracting;
            for (BlockData b : blocks) {
                if (b.equals(onPathBlock.get(0))) {
                    blocks.remove(b);
                    break;
                }
            }
            updateCapturedBlock();
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
    private void onReturnToSwing() {
        hook = hookStatus.swinging;
        if (isCaptured && onPathBlock.size() == 1) {
            playerScore += onPathBlock.get(0).getValue();
        }
        isCaptured = false;
        onPathBlock = new ArrayList<>();
        Log.i(TAG, "onExtend: done retracting " + onPathBlock.size());
    }
    private void onHookReCenter() {
        hookPositionY = HOOK_INIT_Y;
        hookPositionX = HOOK_INIT_X;
    }
    private void onFinishGame() {
        try {
            gameActivity.onPopup(gameActivity, playerScore);
            handler.removeCallbacks(movementRunnable);
        } catch (NullPointerException e) {
            Log.d(TAG, "onFinishGame: nullPointer GA = " + gameActivity + e.getMessage());
        }
    }
    public int getScore() {
        return playerScore;
    }
    public static double getOnPathRange() {
        return (screenHeight - MINER_TOP) / (slopeY / slopeX);
    }
    private double getOnPathBlockDistanceX(BlockData blockData) {
        return Math.abs(blockData.getCenterX() - firePositionX);
    }
    private double getOnPathBlockDistanceY(BlockData blockData) {
        return Math.abs(blockData.getCenterY() - firePositionY);
    }
    public static double getMinerCenterX() {
        return (MINER_LEFT + MINER_WIDTH / 2);
    }
    public static double getMinerCenterY() {
        return (MINER_TOP);
    }
    public static double getOnPathSlope() {
        return (slopeY / slopeX);
    }

    public static double getFirePositionX() {
        return firePositionX;
    }

    private double findIntersectionY(BlockData blockData) {
        double fireSlope = slopeY / slopeX;
        double blockMidSlope = 0;
        try {
            blockMidSlope = blockData.getSlopes().get(Block.slopeType.mid);
        } catch (NullPointerException e) {
            Log.d(TAG, "findIntersectionY: slope is null");
            return 0;
        }
        if (blockMidSlope < Math.abs(fireSlope)) {
            return blockData.getTop();
        } else {
            if (fireSlope > 0) {
                return Math.abs(fireSlope * (blockData.getLeft() - getMinerCenterX()));
            } else {
                return Math.abs((fireSlope) * (blockData.getRight() - getMinerCenterX()));
            }
        }
    }
    private void updateCapturedBlock() {
        BlockData tempB = onPathBlock.get(0);
        capturedBlock.set((int) hookPositionX,
                (int) hookPositionY,
                (int) (hookPositionX + tempB.getWidth()),
                (int) (hookPositionY + tempB.getHeight()));
    }
}
