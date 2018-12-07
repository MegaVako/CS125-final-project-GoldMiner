package com.testapp.example.goldminer;

import android.graphics.Rect;

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
}
