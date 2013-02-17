package com.manuelmaly.hn;

import java.util.Date;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
    
    public static final String PREF_FONTSIZE = "pref_fontsize";
    public static final String PREF_HTMLPROVIDER = "pref_htmlprovider";
    public static final String PREF_HTMLVIEWER = "pref_htmlviewer";
    public static final String PREF_USER = "pref_user";
    
    public static final String USER_DATA_SEPARATOR = ":";
    
    public static String getFontSize(Context c) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPref.getString(PREF_FONTSIZE, c.getString(R.string.pref_default_fontsize));
    }

    public static String getHtmlProvider(Context c) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPref.getString(PREF_HTMLPROVIDER, c.getString(R.string.pref_default_htmlprovider));
    }

    public static String getHtmlViewer(Context c) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPref.getString(PREF_HTMLVIEWER, c.getString(R.string.pref_default_htmlviewer));
    }

    public static String getUserName(Context c) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        String[] userData = sharedPref.getString(PREF_USER, "").split(USER_DATA_SEPARATOR);
        if (userData.length > 0)
            return userData[0];
        return null;
    }
    
    public static String getUserToken(Context c) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        String[] userData = sharedPref.getString(PREF_USER, "").split(USER_DATA_SEPARATOR);
        if (userData.length > 1)
            return userData[1];
        return null;
    }

    public static void setUserData(String userName, String userToken, Context c) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        sharedPref.edit().putString(PREF_USER, userName + USER_DATA_SEPARATOR + userToken).commit();
    }

    public static CookieStore getCookieStoreWithCredentials(Context c) {
        if (getUserToken(c) == null)
            return null;
        
        BasicCookieStore basicStore = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("user", getUserToken(c));
        cookie.setDomain("news.ycombinator.com");
        cookie.setPath("/");
        basicStore.addCookie(cookie);
        return basicStore;
    }
    
}
