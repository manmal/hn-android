package com.manuelmaly.hn.util;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.manuelmaly.hn.view.SpotlightView;

/**
 * Created by jmaltz on 12/23/13.
 */
public class SpotlightActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        float xStart = getIntent().getIntExtra("x_pos", -1);
        float xSize = getIntent().getIntExtra("x_size", -1);
        float ySize = getIntent().getIntExtra("y_size", -1);
        SpotlightView view = new SpotlightView(this, null);
        view.setCoords(xStart, 0, xStart + xSize, ySize);
        setContentView(view);
    }
}
