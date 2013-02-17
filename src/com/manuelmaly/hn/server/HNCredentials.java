package com.manuelmaly.hn.server;

import java.util.Date;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Context;

import com.manuelmaly.hn.Settings;

public class HNCredentials {
    
    private static CookieStore cookieStore;
    
    private static final String COOKIE_USER = "user";
    
    public static CookieStore getCookieStore(Context c) {
        if (cookieStore != null)
            return cookieStore;
        
        cookieStore = new BasicCookieStore();
        String userToken = Settings.getUserToken(c);
        
        if (userToken != null) {
            BasicClientCookie cookie = new BasicClientCookie(COOKIE_USER, userToken);
            cookie.setExpiryDate(new Date(2147368447000L));
            cookie.setDomain("news.ycombinator.com");
            cookie.setPath("/");
            cookieStore.addCookie(cookie);
        }
        
        return cookieStore;
    }
    
}
