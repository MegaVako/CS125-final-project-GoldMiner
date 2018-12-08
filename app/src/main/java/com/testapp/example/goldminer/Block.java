package com.testapp.example.goldminer;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

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

    private Rect currentBlock;
    private type blockType;
    private int left;
    private int top;
    private int right;
    private int bot;
    private int blockColor;
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
        //Step one
        double rangeX = GameView.getOnPathRange();
        double minerCenter = GameView.getMinerCenter();
        double slope = GameView.getOnPathSlope();
        double hookFirePositionX = GameView.getFirePositionX();
        if (rangeX < 0) {
            if (left < minerCenter + rangeX) {
                return false;
            }
        } else {
            if (left > minerCenter + rangeX) {
                return false;
            }
        }
        if (Math.abs(slope) > 3.5) {
            double fromFirePosition = Math.abs(hookFirePositionX - getCenterX());
            if (fromFirePosition < ON_PATH_RANGE_STRAIGHT) {
                return true;
            }
            Log.i(TAG, "isOnPath: from fire Position = " + fromFirePosition);
        }
        //Step two
        if (!onPathFunction(slope, minerCenter)) {
            return false;
        }
        return true;
    }

    @Override
    public Rect getBlock() {
        return currentBlock;
    }

    private boolean onPathFunction(double slope, double minerCenter) {
        double distanceToMincerCenter = Math.abs(minerCenter - (right + left) / 2);
        if (slope > 0) {
            if (right < minerCenter) {
                return false;
            }
        } else {
            if (left > minerCenter) {
                return false;
            }
        }
        Log.d(TAG, "isOnPath: pass one");
        double checker = Math.abs(slope * (distanceToMincerCenter));
        double checkFrom = Math.abs((getCenterY() - checker));
        if (checkFrom > ON_PATH_RANGE_SLOPE) {
            Log.d(TAG, "onPathFunction: false --> cf/cker" + checkFrom + "/" + checker);
            return false;
        }
        return true;
    }

    private void setBlockType() {
        int rate = (left * right * top * bot) % RATE;
        if (rate < GOLD_RATE) {
            blockType = type.gold;
            blockColor = Color.rgb(250,214,0);
        } else if (rate < STONE_RATE) {
            blockType = type.stone;
            blockColor = Color.GRAY;
        } else if (rate < DIAMOND_RATE) {
            blockType = type.diamond;
            right = left + DIAMOND_SIZE;
            bot = top + DIAMOND_SIZE;
            blockColor = Color.rgb(106, 213, 254);
        } else {
            blockType = type.mine;
            right = left + MINE_LENGTH;
            bot = top + MINE_HEIGHT;
            blockColor = Color.rgb(255, 0, 30);
        }
    }
    public enum type {
        gold, stone, diamond, mine
    }
    private void setBlock() {
        currentBlock = new Rect(left, top, right, bot);
    }
}
