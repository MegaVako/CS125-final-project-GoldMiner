package com.testapp.example.goldminer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View {
    private Bitmap bitmap;
    private Canvas canvas;
    private int canvasRestoreJustAfterCreation;

    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
//        bitmap = Bitmap.createBitmap(100, 100, BITMAP_CONFIG);  // for safety
//        canvas = new Canvas(bitmap);
//        canvasRestoreJustAfterCreation = canvas.save();
//        paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setStrokeCap(Paint.Cap.SQUARE);
//        paint.setStrokeWidth(1.f);  // will be scaled with everything else as long as it's non-zero
//        checkerboardPaint = new Paint();
//        final Drawable checkerboardDrawable = ContextCompat.getDrawable(getContext(), R.drawable.checkerboard);
//        if (checkerboardDrawable == null) throw new RuntimeException("Missing R.drawable.checkerboard");
//        final Bitmap checkerboard = ((BitmapDrawable) checkerboardDrawable).getBitmap();
//        checkerboardPaint.setShader(new BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
//        blitters = new Bitmap[512];
    }
}
