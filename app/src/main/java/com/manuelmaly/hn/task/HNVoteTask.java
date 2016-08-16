package com.manuelmaly.hn.task;

import android.app.Activity;

import com.manuelmaly.hn.App;
import com.manuelmaly.hn.reuse.CancelableRunnable;
import com.manuelmaly.hn.server.HNCredentials;
import com.manuelmaly.hn.server.HNVoteCommand;
import com.manuelmaly.hn.server.IAPICommand;
import com.manuelmaly.hn.server.IAPICommand.RequestType;

public class HNVoteTask extends BaseTask<Boolean> {

    public static final String BROADCAST_INTENT_ID = "HNVoteTask";

    private static HNVoteTask instance;

    private String mVoteURL;

    private static HNVoteTask getInstance(int taskCode) {
        synchronized (HNVoteTask.class) {
            if (instance == null)
                instance = new HNVoteTask(taskCode);
        }
        return instance;
    }

    public HNVoteTask(int taskCode) {
        super(BROADCAST_INTENT_ID, taskCode);
    }

    @Override
    public CancelableRunnable getTask() {
        return new HNVoteTaskRunnable();
    }

    public void setVoteURL(String voteURL) {
        mVoteURL = voteURL;
    }
    
    public static void start(String voteURL, Activity activity,
        ITaskFinishedHandler<Boolean> finishedHandler, int taskCode, Object tag) {
        HNVoteTask task = getInstance(taskCode);
        task.setTag(tag);
        task.setOnFinishedHandler(activity, finishedHandler, Boolean.class);
        if (task.isRunning())
            task.cancel();
        task.setVoteURL(voteURL);
        task.startInBackground();
    }

    class HNVoteTaskRunnable extends CancelableRunnable {

        HNVoteCommand mVoteCommand;

        @Override
        public void run() {
            mResult = vote();
        }

        private Boolean vote() {
            mVoteCommand = new HNVoteCommand(mVoteURL, null, RequestType.GET, false, null,
                App.getInstance(), HNCredentials.getCookieStore(App.getInstance()));
            mVoteCommand.run();
            
            if (mCancelled || mErrorCode != IAPICommand.ERROR_NONE)
                return null;
            
            return mVoteCommand.getResponseContent();

        }

        @Override
        public void onCancelled() {
            if (mVoteCommand != null)
                mVoteCommand.cancel();
        }

    }

}
