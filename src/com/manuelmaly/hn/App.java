package com.manuelmaly.hn;

import android.app.Application;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.googlecode.androidannotations.annotations.EApplication;
import com.manuelmaly.hn.util.Const;

@EApplication
public class App extends Application {
    
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        
        GoogleAnalyticsTracker.getInstance().startNewSession(Const.GAN_ID, this);
        GoogleAnalyticsTracker.getInstance().dispatch();
    }
    
    @Override
    public void onTerminate() {
        GoogleAnalyticsTracker.getInstance().stopSession();
        GoogleAnalyticsTracker.getInstance().dispatch();
        
        super.onTerminate();
    }
    
    public static App getInstance() {
        return mInstance;
    }
    
}
