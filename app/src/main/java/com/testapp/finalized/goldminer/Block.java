package com.testapp.finalized.goldminer;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import java.util.HashMap;

public class Block implements BlockData {
    private static final int RATE = 1000;
    private static final int GOLD_RATE = (int) ((1 - 0.5) * RATE);
    private static final int DIAMOND_RATE = (int) ((1 - 0.1) * RATE);
    private static final int STONE_RATE = (int) ((1 - 0.3) * RATE);
    private static final int MINE_RATE = (int) ((1 - 0.05) * RATE);
    private static final int DIAMOND_SIZE = 30;
    private static final int MINE_LENGTH = 100;
    private static final int MINE_HEIGHT = 40;
    private static final String TAG = "GoldMiner/GameView/B";
    private static final int ON_PATH_RANGE_SLOPE  = 145;
    private static final int ON_PATH_RANGE_STRAIGHT = 145;

    private HashMap<slopeType, Double> slopes = new HashMap<>(3);
    private Rect currentBlock;
    private type blockType;
    private int left;
    private int top;
    private int right;
    private int bot;
    private int blockColor;
    private int value;

    Block (int left, int top, int right, int bot){
        this.left = left;
        this.top = top;
        this.right = right;
        this.bot = bot;
        setBlockType();
        setBlock();
    }
    @Override
    public double getPositionX() {
        return left;
    }

    @Override
    public double getPositionY() {
        return top;
    }

    @Override
    public double getCenterX() {
        return (left + right) / 2;
    }

    @Override
    public double getCenterY() {
        return (top + bot) / 2;
    }

    @Override
    public int getColor() {
        return blockColor;
    }

    @Override
    public type getType() {
        return blockType;
    }

    @Override
    public double getWidth() {
        return (right - left);
    }

    @Override
    public double getHeight() {
        return (bot - top);
    }

    @Override
    public boolean isOnPath() {
        //Log.i(TAG, "isOnPath: CALLED");
        double centerX = GameView.getMinerCenterX();
        double centerY = GameView.getMinerCenterY();
        //Compare to this slope!
        double fireSlope = GameView.getOnPathSlope();
        if (fireSlope > 0) {
            slopes.put(slopeType.higher, Math.abs((top - centerY) / (right - centerX)));
            slopes.put(slopeType.lower, Math.abs((bot - centerY) / (left - centerX)));
            slopes.put(slopeType.mid, Math.abs((top - centerY) / (left - centerX)));
        } else {
            slopes.put(slopeType.higher, Math.abs((top - centerY) / (left - centerX)));
            slopes.put(slopeType.lower, Math.abs((bot - centerY) / (right - centerX)));
            slopes.put(slopeType.mid, Math.abs((top - centerY) / (right - centerX)));
        }
        fireSlope = Math.abs(fireSlope);
//        Log.i(TAG, "isOnPath: is false");
//        Log.d(TAG, "isOnPath: h " + slopes.get(slopeType.higher));
//        Log.d(TAG, "isOnPath: l " + slopes.get(slopeType.lower));
//        Log.d(TAG, "isOnPath: m " + slopes.get(slopeType.mid));
//        Log.d(TAG, "isOnPath: fire " + fireSlope);
        if (fireSlope > slopes.get(slopeType.lower) || fireSlope < slopes.get(slopeType.higher)) {
            Log.d(TAG, "isOnPath: is false");
            return false;
        } else {
            Log.i(TAG, "isOnPath: is true");
            return true;
        }
    }

    @Override
    public Rect getBlock() {
        return currentBlock;
    }

    @Override
    public HashMap<slopeType, Double> getSlopes() {
        return slopes;
    }

    @Override
    public double getTop() {
        return top;
    }

    @Override
    public double getLeft() {
        return left;
    }

    @Override
    public double getRight() {
        return right;
    }

    @Override
    public int getValue() {
        return value;
    }

    private void setBlockType() {
        int rate = (left * right * top * bot) % RATE;
        if (rate < GOLD_RATE) {
            blockType = type.gold;
            blockColor = Color.rgb(250,214,0);
            value = 100;
        } else if (rate < STONE_RATE) {
            blockType = type.stone;
            blockColor = Color.GRAY;
            value = 30;
        } else if (rate < DIAMOND_RATE) {
            blockType = type.diamond;
            right = left + DIAMOND_SIZE;
            bot = top + DIAMOND_SIZE;
            blockColor = Color.rgb(106, 213, 254);
            value = 300;
        } else {
            blockType = type.mine;
            right = left + MINE_LENGTH;
            bot = top + MINE_HEIGHT;
            blockColor = Color.rgb(255, 0, 30);
            value = 0;
        }
    }
    public enum type {
        gold, stone, diamond, mine
    }
    private void setBlock() {
        currentBlock = new Rect(left, top, right, bot);
    }
    //types of slope
    public enum slopeType {
        //higher for smaller y
        higher, lower, mid
    }
}
