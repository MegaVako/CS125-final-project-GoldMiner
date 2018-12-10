package com.testapp.example.goldminer;

import android.graphics.Rect;

import java.util.HashMap;

public interface BlockData {
    double getPositionX();
    double getPositionY();
    double getCenterX();
    double getCenterY();
    int getColor();
    Block.type getType();
    double getWidth();
    double getHeight();
    boolean isOnPath();
    Rect getBlock();
    HashMap<Block.slopeType, Double> getSlopes();
    double getTop();
    double getLeft();
    double getRight();
    int getValue();
}
