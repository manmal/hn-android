package com.manuelmaly.hn.task;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.manuelmaly.hn.App;
import com.manuelmaly.hn.reuse.CancelableRunnable;
import com.manuelmaly.hn.server.IAPICommand;
import com.manuelmaly.hn.task.ITaskFinishedHandler.TaskResultCode;
import com.manuelmaly.hn.util.Run;

import java.io.Serializable;
import java.lang.ref.SoftReference;

/**
 * Generic base for tasks performed asynchronously. Unlike {@link AsyncTask},
 * its on-finished-notification will be passed to every Activity instance which
 * has registered (listeners are notified via an intent sent to
 * {@link LocalBroadcastManager}). Meaning, there will be no Zombie tasks
 * performing stuff for nothing (e.g. because their callback Activity has been
 * destroyed because of orientation change).
 * 
 * @author manuelmaly
 * @param <T>
 *            result type
 */
public abstract class BaseTask<T extends Serializable> implements Runnable {

    public static final String BROADCAST_INTENT_EXTRA_ERROR = "error";
    public static final String BROADCAST_INTENT_EXTRA_RESULT = "result";

    protected String mNotificationBroadcastIntentID;
    protected T mResult;
    protected int mErrorCode;
    protected boolean mIsRunning;
    protected CancelableRunnable mTaskRunnable;
    protected int mTaskCode;
    protected Object mTag;

    public BaseTask(String notificationBroadcastIntentID, int taskCode) {
        mNotificationBroadcastIntentID = notificationBroadcastIntentID;
        mTaskCode = taskCode;
    }

    protected void startInBackground() {
        Run.inBackground(this);
    }

    /**
     * The broadcast will be received by listeners on the main thread
     * implicitly.
     */
    public void notifyFinished(int errorCode, Serializable result) {
        Intent broadcastIntent = new Intent(mNotificationBroadcastIntentID);
        broadcastIntent.putExtra(BROADCAST_INTENT_EXTRA_ERROR, errorCode);
        broadcastIntent.putExtra(BROADCAST_INTENT_EXTRA_RESULT, result);
        LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcast(broadcastIntent);
    }

    /**
     * 
     * @param tag
     */
    public void setTag(Object tag) {
        mTag = tag;
    }

    /**
     * Registers the given {@link BroadcastReceiver} to this task's
     * finished-notification.
     * 
     * @param receiver
     */
    public void registerForFinishedNotification(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter(mNotificationBroadcastIntentID);
        LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(receiver, filter);
    }

    /**
     * Schedules behaviour to be executed when this task has finished, for the
     * given Activity.
     * 
     * @param activity
     * @param finishedHandler
     * @param resultClazz
     */
    public void setOnFinishedHandler(Activity activity, ITaskFinishedHandler<T> finishedHandler,
        final Class<T> resultClazz) {
        final SoftReference<Activity> activityRef = new SoftReference<Activity>(activity);
        final SoftReference<ITaskFinishedHandler<T>> finishedHandlerRef = new SoftReference<ITaskFinishedHandler<T>>(
            finishedHandler);
        BroadcastReceiver finishedListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(this);

                if (activityRef == null || activityRef.get() == null || finishedHandlerRef == null
                    || finishedHandlerRef.get() == null)
                    return;

                // Make hard references until the end of processing, so we don't
                // lose those objects:
                Activity activity = activityRef.get();
                final ITaskFinishedHandler<T> finishedHandler = finishedHandlerRef.get();

                int lowLevelErrorCode = intent.getIntExtra(BaseTask.BROADCAST_INTENT_EXTRA_ERROR,
                    IAPICommand.ERROR_NONE);
                final int errorCode;
                final T result;
                Serializable rawResult = intent.getSerializableExtra(BaseTask.BROADCAST_INTENT_EXTRA_RESULT);
                if (resultClazz.isInstance(rawResult)) {
                    result = resultClazz.cast(rawResult);
                    errorCode = lowLevelErrorCode;
                } else {
                	result = null;
                	if (lowLevelErrorCode == IAPICommand.ERROR_NONE) {
                		// We have no error so far, but cannot cast the result data:
                		errorCode = IAPICommand.ERROR_UNKNOWN;
                	} else {
                		errorCode = lowLevelErrorCode;
                	}
                }

                Runnable r = new Runnable() {
                    public void run() {
                        finishedHandler
                            .onTaskFinished(mTaskCode, TaskResultCode.fromErrorCode(errorCode), result, mTag);
                    }
                };
                Run.onUiThread(r, activity);
            }
        };
        this.registerForFinishedNotification(finishedListener);
    }

    @Override
    public void run() {
        mIsRunning = true;
        mTaskRunnable = getTask();
        mTaskRunnable.run();
        mIsRunning = false;
        notifyFinished(mErrorCode, mResult);
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public T getResult() {
        return mResult;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public void cancel() {
        Run.inBackground(new Runnable() {
            @Override
            public void run() {
                if (mTaskRunnable != null)
                    mTaskRunnable.cancel();
            }
        });
    }

    public abstract CancelableRunnable getTask();

}
