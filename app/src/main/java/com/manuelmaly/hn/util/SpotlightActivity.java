package com.manuelmaly.hn.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;

import com.manuelmaly.hn.view.SpotlightView;

/**
 * Created by jmaltz on 12/23/13.
 */
public class SpotlightActivity extends Activity {

  public static final String KEY_X_START = "x_start";
  public static final String KEY_X_SIZE = "x_size";
  public static final String KEY_Y_START = "y_start";
  public static final String KEY_Y_SIZE = "y_size";
  public static final String KEY_TEXT_STRING = "text_string";

  private float mXStart;
  private float mYStart;
  private float mXEnd;
  private float mYEnd;

  public static Intent intentForSpotlightActivity( Context context, float xStart, float xSize, float yStart,
      float ySize, String text ) {
    Intent intent = new Intent( context, SpotlightActivity.class );
    intent.putExtra( KEY_X_START, xStart );
    intent.putExtra( KEY_X_SIZE, xSize );
    intent.putExtra( KEY_Y_START, yStart );
    intent.putExtra( KEY_Y_SIZE, ySize );
    intent.putExtra( KEY_TEXT_STRING, text );
    return intent;
  }

  @Override
  public void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    requestWindowFeature( Window.FEATURE_NO_TITLE );
    mXStart = getIntent().getFloatExtra( KEY_X_START, -1 );
    mXEnd = getIntent().getFloatExtra( KEY_X_SIZE, -1 ) + mXStart;
    mYStart = getIntent().getFloatExtra( KEY_Y_START, -1 );
    mYEnd = getIntent().getFloatExtra( KEY_Y_SIZE, -1 ) + mYStart;
    String text = getIntent().getStringExtra( KEY_TEXT_STRING );
    SpotlightView view = new SpotlightView( this, null, mXStart, mYStart, mXEnd, mYEnd, text );
    view.setOnTouchListener( mSpotlightTouchListener );
    setContentView( view );
  }

  private final OnTouchListener mSpotlightTouchListener = new OnTouchListener() {

    @Override
    public boolean onTouch( View v, MotionEvent event ) {
      if (event.getX() > mXStart && event.getX() < mXEnd && event.getY() > mYStart && event.getY() < mYEnd) {
        setResult( RESULT_OK );
      } else {
        setResult( RESULT_CANCELED );
      }
      overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out );
      finish();
      return true;
    }
  };
}
