package com.manuelmaly.hn.task;

import android.app.Activity;
import android.content.Context;

import com.manuelmaly.hn.model.HNFeed;

public class HNFeedTaskLoadMore extends HNFeedTaskBase {

    private HNFeed mFeedToAttachResultsTo;

    private static HNFeedTaskLoadMore instance;
    public static final String BROADCAST_INTENT_ID = "HNFeedLoadMore";

    private static HNFeedTaskLoadMore getInstance(int taskCode) {
        synchronized (HNFeedTaskLoadMore.class) {
            if (instance == null)
                instance = new HNFeedTaskLoadMore(taskCode);
        }
        return instance;
    }

    private HNFeedTaskLoadMore(int taskCode) {
        super(BROADCAST_INTENT_ID, taskCode);
    }

    @Override
    protected String getFeedURL() {
        return mFeedToAttachResultsTo.getNextPageURL();
    }

    public static void start(Activity activity, ITaskFinishedHandler<HNFeed> finishedHandler,
        HNFeed feedToAttachResultsTo, int taskCode) {
        HNFeedTaskLoadMore task = getInstance(taskCode);
        task.setOnFinishedHandler(activity, finishedHandler, HNFeed.class);
        task.setFeedToAttachResultsTo(feedToAttachResultsTo);
        if (task.isRunning())
            task.cancel();
        task.startInBackground();
    }

    public static void stopCurrent(Context applicationContext, int taskCode) {
        getInstance(taskCode).cancel();
    }

    public static boolean isRunning(Context applicationContext, int taskCode) {
        return getInstance(taskCode).isRunning();
    }

    public void setFeedToAttachResultsTo(HNFeed feedToAttachResultsTo) {
        mFeedToAttachResultsTo = feedToAttachResultsTo;
    }

}
