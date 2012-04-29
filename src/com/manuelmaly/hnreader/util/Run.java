package com.manuelmaly.hnreader.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;

public class Run {

    private final static Executor backgroundExecutor = Executors.newCachedThreadPool();

    public static void onUiThread(Runnable r, Activity a) {
        a.runOnUiThread(r);
    }

    public static void inBackground(Runnable r, Context context) {
        backgroundExecutor.execute(r);
    }

}
