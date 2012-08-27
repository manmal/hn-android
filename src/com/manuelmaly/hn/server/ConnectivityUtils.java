package com.manuelmaly.hn.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class ConnectivityUtils {

    /**
     * Returns the online status of the device. Note that a request to a server
     * can still fail or time-out due to network or server problems!
     * 
     * @param context
     *            of application
     * @return boolean true if online
     */
    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager cMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cMgr.getActiveNetworkInfo();
        if (netInfo == null || netInfo.getState() == null)
            return false;
        return netInfo.getState().equals(State.CONNECTED);
    }
    
}
