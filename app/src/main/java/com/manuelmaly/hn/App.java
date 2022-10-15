package com.manuelmaly.hn;

import android.app.Application;

import org.androidannotations.annotations.EApplication;

@EApplication
public class App extends Application {

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static App getInstance() {
        return mInstance;
    }

}
