package com.manuelmaly.hn.server;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.manuelmaly.hn.Settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class HNCredentials {
    
    private static CookieStore cookieStore;
    
    private static final String COOKIE_USER = "user";
    
    public static CookieStore getCookieStore(Context c, String user) {
        if (cookieStore != null)
            return cookieStore;
        
        cookieStore = new BasicCookieStore();
        String userToken = Settings.getUserToken(c);
        
        if (userToken != null) {
            Cookie cookie = new BasicClientCookie(COOKIE_USER, userToken);
            cookieStore.addCookie(cookie);
        }
        
        return cookieStore;
    }
    
}
