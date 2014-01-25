package com.manuelmaly.hn.task;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;

import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.model.HNPost;
import com.manuelmaly.hn.reuse.CancelableRunnable;

public class HNSearchTask extends BaseTask<HNFeed> {

  public static final String BROADCAST_INTENT_ID = "HNSearchTask";
  private static HNSearchTask mInstance;

  public HNSearchTask( int taskCode ) {
    super( BROADCAST_INTENT_ID, taskCode );
  }

  @Override
  public CancelableRunnable getTask() {
    return new CancelableRunnable() {
      
      @Override
      public void run() {
        //TODO query search service
        mResult = new HNFeed(new ArrayList<HNPost>(), "", "");
        
      }
      
      @Override
      public void onCancelled() {
      }
    };
  }

  public static void startOrReattach( String query, Activity activity, ITaskFinishedHandler<HNFeed> finishedHandler, int taskCode ) {
    HNSearchTask task = getInstance( taskCode );
    task.setOnFinishedHandler( activity, finishedHandler, HNFeed.class );
    if (!task.isRunning())
      task.startInBackground();
  }

  public static void stopCurrent( Context applicationContext ) {
    getInstance( 0 ).cancel();
  }

  public static boolean isRunning( Context applicationContext ) {
    return getInstance( 0 ).isRunning();
  }

  private static HNSearchTask getInstance( int taskCode ) {
    synchronized (HNSearchTask.class) {
      if (mInstance == null)
        mInstance = new HNSearchTask( taskCode );
    }
    return mInstance;
  }

}
