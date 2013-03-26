package com.manuelmaly.hn;

import android.app.Activity;

import com.manuelmaly.hn.task.ITaskFinishedHandler;

public abstract class BasePostActivity extends Activity implements ITaskFinishedHandler<Boolean> {

    @Override
    public void onTaskFinished(int taskCode, com.manuelmaly.hn.task.ITaskFinishedHandler.TaskResultCode code,
        Boolean result, Object tag) {
        // TODO Auto-generated method stub
        
    }

}
