package com.manuelmaly.hn.reuse;

import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ImageViewFader {

    public static void startFadeOverToImage(final ImageView view, final int toImageRes, final long durationMillis,
        final Activity activity) {
        Animation fadeOut = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out);
        fadeOut.setDuration(durationMillis);
        fadeOut.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                view.setImageResource(toImageRes);
                Animation fadeIn = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in);
                fadeIn.setDuration(durationMillis);
                view.startAnimation(fadeIn);
            }
        });
        view.startAnimation(fadeOut);
    }

}
