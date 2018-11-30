package com.testapp.example.goldminer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class GameView extends View {
    private static final int MAXIMUM_NUMBER_OF_STONES = 3;
    private static final int STONE_TOP_BOUND = 0;
    private static final int STONE_BOT_BOUND = 500;
    private static final int STONE_LEFT_BOUND = 100;
    private static final int STONE_RIGHT_BOUND = 1500;
    private static final int STONE_WIDTH_MAXIMUM_BOUND = 50;
    private static final int STONE_WIDTH_MINIMUM_BOUND = 20;
    private static final int STONE_HEIGHT_MAXIMUM_BOUND = 50;
    private static final int STONE_HEIGHT_MINIMUM_BOUND = 20;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private String TAG = "GoldMiner/GameView";
    private Rect[] stones = new Rect[MAXIMUM_NUMBER_OF_STONES];
    public GameView(Context context) {
        super(context);
        init();
    }
    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }
    private void init() {
        initPaint();
        initStones();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw");
        mPaint.setColor(Color.rgb(0,0,0));
        canvas.drawRect((float)0, (float)0, (float)2000, (float)2000, mPaint);
        mPaint.setColor(Color.rgb(255,255,255));
        for (Rect rect : stones) {
            canvas.drawRect(rect, mPaint);
        }
        mPaint.setColor(Color.rgb(200,100,200));
        canvas.drawRect(new Rect(300, 300, 600, 600), mPaint);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }
    //onMeasure Not yet working !!
    @Override
    protected void onMeasure(int x, int y){
        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(4000, 4000);
    }
    private void initStones() {
        for (int i = 0; i < stones.length; i++) {
            int[] temp = generateRandomNumber();
            stones[i] = new Rect(temp[0], temp[2], temp[1], temp[3]);
        }
    }
    private int[] generateRandomNumber() {
        int[] result = new int[4];
        Random rand = new Random();
        for (int i = 0; i < result.length; i++) {
            switch (i) {
                case 0:
                    result[0] = rand.nextInt(STONE_RIGHT_BOUND) + STONE_LEFT_BOUND;
                    break;
                case 1:
                    result[1] = (int)(Math.random() * (result[0] + STONE_WIDTH_MAXIMUM_BOUND)
                            + (result[0] + STONE_WIDTH_MINIMUM_BOUND));
                    break;
                case 2:
                    result[2] = rand.nextInt(STONE_BOT_BOUND) + STONE_TOP_BOUND;
                    break;
                case 3:
                    result[3] = (int)(Math.random() * (result[2] + STONE_HEIGHT_MAXIMUM_BOUND)
                            + (result[2] + STONE_HEIGHT_MINIMUM_BOUND));
                    break;
            }
            rand = new Random();
        }
        for (int i : result) {
            Log.i(TAG, "generateRandomNumber: " + i);
        }
        return result;
    }
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(4f);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }
}
