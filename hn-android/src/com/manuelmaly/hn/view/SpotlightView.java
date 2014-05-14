package com.manuelmaly.hn.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jmaltz on 12/23/13.
 */
public class SpotlightView extends View {

  private final float mXStart;
  private final float mYStart;
  private final float mXEnd;
  private final float mYEnd;

  private final Paint mClearPaint;
  private final Paint mBackgroundPaint;
  private final TextPaint mTextPaint;

  private final Rect mClearRect;
  Bitmap mBitmap;

  private StaticLayout mTextLayout;
  String mText;

  public SpotlightView( Context ctx, AttributeSet attrs ) {
    this( ctx, attrs, 0, 0, 0, 0, "" );
  }

  public SpotlightView( Context ctx, AttributeSet attrs, float xStart, float yStart, float xEnd, float yEnd, String text ) {
    super( ctx, attrs );
    mXStart = xStart;
    mYStart = yStart;
    mXEnd = xEnd;
    mYEnd = yEnd;

    mClearPaint = new Paint();
    mClearPaint.setColor( Color.TRANSPARENT );
    mClearPaint.setAntiAlias( true );
    mClearPaint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.CLEAR ) );
    mClearPaint.setMaskFilter( new BlurMaskFilter( 2 * getResources().getDisplayMetrics().density, Blur.NORMAL ) );

    mTextPaint = new TextPaint( Paint.ANTI_ALIAS_FLAG );
    mTextPaint.setColor( Color.WHITE );
    mTextPaint.setTextSize( (float) (getResources().getDisplayMetrics().density * 20 + .5) );
    mTextPaint.setShadowLayer( 1.5f, 1, 1, Color.BLACK );

    mBackgroundPaint = new Paint();
    mBackgroundPaint.setColor( Color.BLACK );
    mBackgroundPaint.setAlpha( 200 );

    mClearRect = new Rect( (int) mXStart, (int) mYEnd, (int) mXEnd, (int) mYStart );
    mText = text;
  }

  @Override
  public void onDraw( Canvas canvas ) {
    canvas.drawBitmap( mBitmap, 0, 0, null );
  }

  @Override
  public void onSizeChanged( int w, int h, int oldw, int oldh ) {
    float density = getResources().getDisplayMetrics().density;
    mTextLayout = new StaticLayout( mText, mTextPaint, w - (int) (density * 20 + .5), Layout.Alignment.ALIGN_CENTER, 1,
        0, false );
    mBitmap = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888 );
    Canvas canvas = new Canvas( mBitmap );
    canvas.drawRect( new Rect( 0, 0, w, h ), mBackgroundPaint );
    canvas.drawRect( mClearRect, mClearPaint );
    canvas.translate( 0, mYEnd + (int) (density * 10 + .5) );
    mTextLayout.draw( canvas );
  }
}
