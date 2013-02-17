package com.manuelmaly.hn.server;

import java.util.Date;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Context;

import com.manuelmaly.hn.Settings;

public class HNCredentials {
    
    private static CookieStore cookieStore;
    private static boolean invalidated;
    
    private static final String COOKIE_USER = "user";
    
    public static CookieStore getCookieStore(Context c) {
        if (cookieStore != null && !invalidated)
            return cookieStore;
        
        cookieStore = new BasicCookieStore();
        String userToken = Settings.getUserToken(c);
        
        if (userToken != null) {
            BasicClientCookie cookie = new BasicClientCookie(COOKIE_USER, userToken);
            cookie.setDomain("news.ycombinator.com");
            cookie.setPath("/");
            cookieStore.addCookie(cookie);
        }
        
        invalidated = false;
        
        return cookieStore;
    }
    
    public static void invalidate() {
        cookieStore = null;
        invalidated = true;
    }
    
    public static boolean isInvalidated() {
        return invalidated;
    }
    
}
