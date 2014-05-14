package com.manuelmaly.hn.util;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class ExceptionUtil {
    
    public static void sendToGoogleAnalytics(Throwable t, String actionName) {
        GoogleAnalyticsTracker.getInstance().trackEvent(Const.GAN_CATEGORY_ERROR, actionName,
            t.getLocalizedMessage(), 0);
    }

}
