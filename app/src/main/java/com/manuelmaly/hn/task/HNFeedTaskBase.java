package com.manuelmaly.hn.task;

import android.util.Log;

import com.manuelmaly.hn.App;
import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.parser.HNFeedParser;
import com.manuelmaly.hn.reuse.CancelableRunnable;
import com.manuelmaly.hn.server.HNCredentials;
import com.manuelmaly.hn.server.IAPICommand;
import com.manuelmaly.hn.server.IAPICommand.RequestType;
import com.manuelmaly.hn.server.StringDownloadCommand;
import com.manuelmaly.hn.util.Const;
import com.manuelmaly.hn.util.ExceptionUtil;
import com.manuelmaly.hn.util.FileUtil;
import com.manuelmaly.hn.util.Run;

import java.util.HashMap;

public abstract class HNFeedTaskBase extends BaseTask<HNFeed> {

    public HNFeedTaskBase(String notificationBroadcastIntentID, int taskCode) {
        super(notificationBroadcastIntentID, taskCode);
    }

    @Override
    public CancelableRunnable getTask() {
        return new HNFeedTaskRunnable();
    }
    
    protected abstract String getFeedURL();

    class HNFeedTaskRunnable extends CancelableRunnable {

        StringDownloadCommand mFeedDownload;

        @Override
        public void run() {
            mFeedDownload = new StringDownloadCommand(getFeedURL(), new HashMap<String, String>(), RequestType.GET, false, null,
                App.getInstance(), HNCredentials.getCookieStore(App.getInstance()));
            
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
                    mResult = null;
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
