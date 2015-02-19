package com.manuelmaly.hn.task;

import android.app.Activity;
import android.util.Log;

import com.manuelmaly.hn.App;
import com.manuelmaly.hn.Settings;
import com.manuelmaly.hn.parser.HNNewsLoginParser;
import com.manuelmaly.hn.reuse.CancelableRunnable;
import com.manuelmaly.hn.server.GetHNUserTokenHTTPCommand;
import com.manuelmaly.hn.server.IAPICommand;
import com.manuelmaly.hn.server.IAPICommand.RequestType;
import com.manuelmaly.hn.server.StringDownloadCommand;
import com.manuelmaly.hn.util.Const;
import com.manuelmaly.hn.util.ExceptionUtil;

import java.util.HashMap;

public class HNLoginTask extends BaseTask<Boolean> {

    private static final String NEWSLOGIN_URL = "https://news.ycombinator.com/login";
    private static final String GET_USERTOKEN_URL = "https://news.ycombinator.com/login";
    public static final String BROADCAST_INTENT_ID = "HNLoginTask";

    private static HNLoginTask instance;

    private String mUsername;
    private String mPassword;
    private String mFNID;

    private static HNLoginTask getInstance(int taskCode) {
        synchronized (HNLoginTask.class) {
            if (instance == null)
                instance = new HNLoginTask(taskCode);
        }
        return instance;
    }

    public HNLoginTask(int taskCode) {
        super(BROADCAST_INTENT_ID, taskCode);
    }

    @Override
    public CancelableRunnable getTask() {
        return new HNLoginTaskRunnable();
    }

    public void setData(String username, String password) {
        mUsername = username;
        mPassword = password;
    }

    public static void start(String username, String password, Activity activity,
        ITaskFinishedHandler<Boolean> finishedHandler, int taskCode) {
        HNLoginTask task = getInstance(taskCode);
        task.setOnFinishedHandler(activity, finishedHandler, Boolean.class);
        task.setData(username, password);
        if (task.isRunning())
            task.cancel();
        task.startInBackground();
    }

    class HNLoginTaskRunnable extends CancelableRunnable {

        StringDownloadCommand newsLoginDownload;
        GetHNUserTokenHTTPCommand getUserTokenCommand;

        @Override
        public void run() {
//            mFNID = getFNID();
//            if (mFNID == null)
//                return;

            String userToken = getUserToken();
            if (userToken != null && !userToken.equals("")) {
                mResult = true;
                Settings.setUserData(mUsername, userToken, App.getInstance());
            }
            else 
                mResult = false;
        }

        private String getFNID() {
            newsLoginDownload = new StringDownloadCommand(NEWSLOGIN_URL, new HashMap<String, String>(), RequestType.GET, false, null,
                App.getInstance(), null);
            newsLoginDownload.run();

            if (mCancelled)
                mErrorCode = IAPICommand.ERROR_CANCELLED_BY_USER;
            else
                mErrorCode = newsLoginDownload.getErrorCode();

            if (!mCancelled && mErrorCode == IAPICommand.ERROR_NONE) {
                HNNewsLoginParser loginParser = new HNNewsLoginParser();
                try {
                    return loginParser.parse(newsLoginDownload.getResponseContent());
                } catch (Exception e) {
                    Log.e("HNFeedTask", "Login Page Parser Error :(", e);
                    ExceptionUtil.sendToGoogleAnalytics(e, Const.GAN_ACTION_PARSING);
                }
            }
            return null;
        }

        private String getUserToken() {
            HashMap<String, String> queryParams = new HashMap<String, String>();
            queryParams.put("goto", "news");
            HashMap<String, String> body = new HashMap<String, String>();
            body.put("goto", "news");
            body.put("acct", mUsername);
            body.put("pw", mPassword);

            getUserTokenCommand = new GetHNUserTokenHTTPCommand(GET_USERTOKEN_URL, queryParams, RequestType.POST, false, null, App.getInstance(), body);
            getUserTokenCommand.run();

            if (mCancelled)
                mErrorCode = IAPICommand.ERROR_CANCELLED_BY_USER;
            else
                mErrorCode = getUserTokenCommand.getErrorCode();

            if (!mCancelled && mErrorCode == IAPICommand.ERROR_NONE) {
                return getUserTokenCommand.getResponseContent();
            }
            return null;
        }

        @Override
        public void onCancelled() {
            if (newsLoginDownload != null)
                newsLoginDownload.cancel();
            if (getUserTokenCommand != null)
                getUserTokenCommand.cancel();
        }

    }

}
