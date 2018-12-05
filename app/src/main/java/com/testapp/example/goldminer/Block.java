package com.testapp.example.goldminer;

import android.graphics.Rect;
import android.util.Log;

public class Block implements BlockData {
    private static final int RATE = 1000;
    private static final int GOLD_RATE = (int) ((1 - 0.5) * RATE);
    private static final int DIAMOND_RATE = (int) ((1 - 0.2) * RATE);
    private static final int STONE_RATE = (int) ((1 - 0.3) * RATE);
    private static final int MINE_RATE = (int) ((1 - 0.05) * RATE);
    private static final String TAG = "GoldMiner/GameView/B";

    private Rect currentBlock;
    private type blockType;
    private int left;
    private int top;
    private int right;
    private int bot;

    Block (int left, int top, int right, int bot){
        this.left = left;
        this.top = top;
        this.right = right;
        this.bot = bot;
        currentBlock = new Rect(left, top, right, bot);
        setBlockType();
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
        if (rangeX < 0) {
            if (left < minerCenter + rangeX) {
                return false;
            }
        } else {
            if (left > minerCenter + rangeX) {
                return false;
            }
        }
        Log.d(TAG, "isOnPath: pass one");
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
        double topMax;
        double botMin;
        double hookRadius = GameView.getHookRadius();
        double checkerOne =
                Math.abs(slope * left - minerCenter + hookRadius);
        double checkerTwo =
                Math.abs(slope * left - minerCenter - hookRadius);
        if (slope > 0) {
            topMax = checkerOne;
            botMin = checkerTwo;
        } else {
            topMax = checkerTwo;
            botMin = checkerOne;
        }

        if (top > topMax) {
            return false;
        } else if (bot < botMin) {
            return false;
        }
        return true;
    }

    private void setBlockType() {
        int rate = (left * right * top * bot) % RATE;
        if (rate < GOLD_RATE) {
            blockType = type.gold;
        } else if (rate < STONE_RATE) {
            blockType = type.stone;
        } else if (rate < DIAMOND_RATE) {
            blockType = type.diamond;
        } else {
            blockType = type.mine;
        }
    }
    public enum type {
        gold, stone, diamond, mine
    }
}
