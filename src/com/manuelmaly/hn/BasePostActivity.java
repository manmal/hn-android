package com.manuelmaly.hn;

import com.manuelmaly.hn.task.ITaskFinishedHandler;

import android.app.Activity;

public class BasePostActivity extends Activity implements ITaskFinishedHandler<Boolean> {

    @Override
    public void onTaskFinished(int taskCode, com.manuelmaly.hn.task.ITaskFinishedHandler.TaskResultCode code,
        Boolean result, Object tag) {
        // TODO Auto-generated method stub
        
    }

}
