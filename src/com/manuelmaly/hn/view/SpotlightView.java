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
        float gradientRadius = getResources().getDisplayMetrics().density * 270 + .5f;
        mRadialGradient = new RadialGradient(xCenter, yCenter, gradientRadius, 0x80000000, 0xD0000000, TileMode.CLAMP);
        
        mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mGradientPaint.setShader(mRadialGradient);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*Bitmap bmp = Bitmap.createBitmap((int)Math.ceil(mXEnd - mXStart), (int)
                Math.ceil(mYEnd - mYStart), Bitmap.Config.ALPHA_8);*/

        float highlightRadius = Math.max((mXEnd - mXStart)/2, (mYStart - mYEnd)/2);
        canvas.drawPaint(mGradientPaint);
        Rect r = new Rect((int)mXStart, (int)mYEnd, (int)mXEnd, (int)mYStart);
        float xCenter = mXStart + (mXEnd - mXStart)/2;
        float yCenter = mYStart + (mYEnd - mYStart)/2;
        //canvas.drawCircle(xCenter, mYStart, highlightRadius, mClearPaint);
        canvas.drawRect(r, mClearPaint);
    }
}
