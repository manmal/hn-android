package com.manuelmaly.hn.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class DisplayHelper {

	private static Float scale;
	
	public static int dpToPixel(int dp, Context context) {
		if(scale == null)
			scale = context.getResources().getDisplayMetrics().density;
		return (int)((float)dp * scale);
	} 
	
	@SuppressWarnings("deprecation")
	public static BitmapDrawable bitmapWithConstraints(int bitmapResource, Context ctx, int dpConstraintWidthAndHeight, int padding) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		Bitmap bitmapOrg = BitmapFactory.decodeResource(ctx.getResources(), bitmapResource, options);

		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();
		int newWidth = dpToPixel(dpConstraintWidthAndHeight, ctx) - 2*dpToPixel(padding, ctx);
		int newHeight = newWidth;
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
		return new BitmapDrawable(resizedBitmap);
	}
	
	@SuppressWarnings("deprecation")
	public static int getScreenWidth(Activity a) {
		Display display = a.getWindowManager().getDefaultDisplay(); 
		return display.getWidth();
	}
	
	@SuppressWarnings("deprecation")
	public static int getScreenHeight(Activity a) {
		Display display = a.getWindowManager().getDefaultDisplay(); 
		return display.getHeight();
	}
	
	public static void setDialogParams(Dialog d, Activity a, boolean hasTitleBar, View layout, int marginHorizontalDP) {
    	if (!hasTitleBar)
    		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	d.setContentView(layout);
    	WindowManager.LayoutParams lp = d.getWindow().getAttributes();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.dimAmount = 0.8f;
        lp.width = getScreenWidth(a) - 2 * dpToPixel(marginHorizontalDP, a);
	}
	
}
