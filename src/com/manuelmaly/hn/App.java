package com.manuelmaly.hn;

import android.app.Application;
import android.os.StrictMode;

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

        GoogleAnalyticsTracker.getInstance().startNewSession(Const.GAN_ID, 30, this);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyLog()
            .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().penaltyLog().build());
    }

    @Override
    public void onTerminate() {
        GoogleAnalyticsTracker.getInstance().stopSession();

        super.onTerminate();
    }

    public static App getInstance() {
        return mInstance;
    }

}
