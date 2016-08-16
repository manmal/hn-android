package com.manuelmaly.hn.util;

import android.app.Activity;
import android.os.Handler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Run {

    private final static Executor backgroundExecutor = Executors.newCachedThreadPool();

    public static void onUiThread(Runnable r, Activity a) {
        a.runOnUiThread(r);
    }

    public static void inBackground(Runnable r) {
        backgroundExecutor.execute(r);
    }
    
    public static void delayed(Runnable r, long delayMillis) {
        Handler h = new Handler();
        h.postDelayed(r, delayMillis);
    }

}
