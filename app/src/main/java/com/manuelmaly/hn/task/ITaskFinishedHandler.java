package com.manuelmaly.hn.task;

import com.manuelmaly.hn.server.IAPICommand;

public interface ITaskFinishedHandler<T> {

    public void onTaskFinished(int taskCode, TaskResultCode code, T result, Object tag);
    
    public enum TaskResultCode {
        Success, CancelledByUser, NoNetworkConnection, UnknownError;

        public static TaskResultCode fromErrorCode(int iApiCommandErrorCode) {
            switch (iApiCommandErrorCode) {
                case IAPICommand.ERROR_NONE:
                    return Success;
                case IAPICommand.ERROR_DEVICE_OFFLINE:
                    return NoNetworkConnection;
                case IAPICommand.ERROR_CANCELLED_BY_USER:
                    return CancelledByUser;
                default:
                    return UnknownError;
            }
        }
    }
    
}
