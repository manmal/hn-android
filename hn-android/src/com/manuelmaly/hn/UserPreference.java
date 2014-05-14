package com.manuelmaly.hn;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

public class UserPreference extends Preference{

    private String mUsertoken;
    private String mUsername;

    public UserPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public String getData() {
        if (mUsername == null || mUsertoken == null)
            return null;

        return mUsername.concat(Settings.USER_DATA_SEPARATOR).concat(mUsertoken);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            String userData = getPersistedString("");
            mUsername = getUserNameFromUserdata(userData);
            mUsertoken = getUserTokenFromUserdata(userData);
        } else {
            mUsername = null;
            mUsertoken = null;
            persistString(getData());
        }
        super.onSetInitialValue(restorePersistedValue, defaultValue);
    }

    public static String getUserNameFromUserdata(String userData) {
        String[] splitData = userData.split(Settings.USER_DATA_SEPARATOR);
        if (splitData.length > 0)
            return splitData[0];
        return null;
    }

    public static String getUserTokenFromUserdata(String userData) {
        String[] splitData = userData.split(Settings.USER_DATA_SEPARATOR);
        if (splitData.length > 1)
            return splitData[1];
        return null;
    }
}
