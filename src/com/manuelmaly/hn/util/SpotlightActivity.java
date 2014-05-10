package com.manuelmaly.hn.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    float xStart = getIntent().getFloatExtra( KEY_X_START, -1 );
    float xSize = getIntent().getFloatExtra( KEY_X_SIZE, -1 );
    float yStart = getIntent().getFloatExtra( KEY_Y_START, -1 );
    float ySize = getIntent().getFloatExtra( KEY_Y_SIZE, -1 );
    String text = getIntent().getStringExtra( KEY_TEXT_STRING );
    SpotlightView view = new SpotlightView( this, null, xStart, yStart, xStart + xSize, ySize, text );
    setContentView( view );
  }
}
