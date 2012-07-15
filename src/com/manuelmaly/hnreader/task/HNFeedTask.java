package com.manuelmaly.hnreader.task;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.manuelmaly.hnreader.App;
import com.manuelmaly.hnreader.model.HNFeed;
import com.manuelmaly.hnreader.parser.HNFeedParser;
import com.manuelmaly.hnreader.reuse.CancelableRunnable;
import com.manuelmaly.hnreader.server.HTMLDownloadCommand;
import com.manuelmaly.hnreader.server.IAPICommand;
import com.manuelmaly.hnreader.server.IAPICommand.RequestType;
import com.manuelmaly.hnreader.util.Const;
import com.manuelmaly.hnreader.util.ExceptionUtil;
import com.manuelmaly.hnreader.util.FileUtil;
import com.manuelmaly.hnreader.util.Run;

public class HNFeedTask extends BaseTask<HNFeed> {

    public static final String BROADCAST_INTENT_ID = "HNFeed";
    private static HNFeedTask instance;

    /**
     * I know, Singleton is generally a no-no, but the only other option would
     * be to store the currently running HNFeedTask in the App object, which I
     * consider far worse. If you find a better solution, please tweet me at @manuelmaly
     * 
     * @return
     */
    private static HNFeedTask getInstance() {
        synchronized (HNFeedTask.class) {
            if (instance == null)
                instance = new HNFeedTask();
        }
        return instance;
    }

    public static void startOrReattach(Activity activity, ITaskFinishedHandler<HNFeed> finishedHandler) {
        HNFeedTask task = getInstance();
        task.setOnFinishBehaviour(activity, finishedHandler, HNFeed.class);
        if (!task.isRunning())
            task.startInBackground();
    }

    public static void stopCurrent(Context applicationContext) {
        getInstance().cancel();
    }

    public static boolean isRunning(Context applicationContext) {
        return getInstance().isRunning();
    }

    private HNFeedTask() {
        super(BROADCAST_INTENT_ID);
    }

    @Override
    public CancelableRunnable getTask() {
        return new HNFeedTaskRunnable();
    }

    class HNFeedTaskRunnable extends CancelableRunnable {

        HTMLDownloadCommand mFeedDownload;

        @Override
        public void run() {
            mFeedDownload = new HTMLDownloadCommand("http://news.ycombinator.com/", "", RequestType.GET, false, null,
                App.getInstance());
            mFeedDownload.run();

            if (mCancelled)
                mErrorCode = IAPICommand.ERROR_CANCELLED_BY_USER;
            else
                mErrorCode = mFeedDownload.getErrorCode();

            if (!mCancelled && mErrorCode == IAPICommand.ERROR_NONE) {
                HNFeedParser feedParser = new HNFeedParser();
                try {
                    mResult = feedParser.parse(mFeedDownload.getResponseContent());
                    Run.inBackground(new Runnable() {
                        public void run() {
                            FileUtil.setLastHNFeed(mResult);
                        }
                    });
                } catch (Exception e) {
                    ExceptionUtil.sendToGoogleAnalytics(e, Const.GAN_ACTION_PARSING);
                    Log.e("HNFeedTask", "HNFeed Parser Error :(", e);
                }
            }

            if (mResult == null)
                mResult = new HNFeed();
        }

        @Override
        public void onCancelled() {
            mFeedDownload.cancel();
        }

    }

}
