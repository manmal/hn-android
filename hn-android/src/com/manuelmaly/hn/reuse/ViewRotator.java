package com.manuelmaly.hn.reuse;

import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class ViewRotator {

    public static void startRotating(final View view) {
        // Why this layout listener craziness? Because the view's dimensions are only known
        // after layout is finished. requestLayout() makes sure that the listener is called.
        view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                RotateAnimation anim = new RotateAnimation(0f, 350f, view.getWidth() / 2.0f, view.getHeight() / 2.0f);
                anim.setInterpolator(new LinearInterpolator());
                anim.setRepeatCount(Animation.INFINITE);
                anim.setDuration(1000);
                view.startAnimation(anim);
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        view.requestLayout();
    }
    
    public static void stopRotating(View view) {
        view.clearAnimation();
    }
    
}
