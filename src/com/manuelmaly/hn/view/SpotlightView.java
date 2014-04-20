package com.manuelmaly.hn.view;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jmaltz on 12/23/13.
 */
public class SpotlightView extends View {

    private float mXStart;
    private float mYStart;
    private float mXEnd;
    private float mYEnd;

    private RadialGradient mRadialGradient;

    private final Paint mClearPaint;
    private Paint mGradientPaint;

    private Rect mClearRect;

    public SpotlightView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        mClearPaint = new Paint();
        mClearPaint.setColor(Color.TRANSPARENT);
        mClearPaint.setAntiAlias(true);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mClearPaint.setMaskFilter(new BlurMaskFilter(2 * getResources().getDisplayMetrics().density , Blur.NORMAL));
        // TODO make this better and actually read in the start and end attributes
    }

    public void setCoords(float xStart, float yStart, float xEnd, float yEnd) {
        mXStart = xStart;
        mYStart = yStart;
        mXEnd = xEnd;
        mYEnd = yEnd;

        float xCenter = mXStart + (mXEnd - mXStart)/2;
        float yCenter = mYStart + (mYEnd - mYStart)/2;
        // I want to basically make everything 0xFF000000 and I don't know how
        // to properly do that.  This solves that problem.
        mRadialGradient = new RadialGradient(xCenter, yCenter, 1,
                0xFF000000, 0xFF000000, TileMode.CLAMP);

        mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mGradientPaint.setShader(mRadialGradient);

        mClearRect = new Rect((int)mXStart, (int)mYEnd, (int)mXEnd, (int)mYStart);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPaint(mGradientPaint);
        canvas.drawRect(mClearRect, mClearPaint);
    }
}
