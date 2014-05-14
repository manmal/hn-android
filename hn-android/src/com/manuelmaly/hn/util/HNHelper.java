package com.manuelmaly.hn.util;

public class HNHelper {
    
    public static String resolveRelativeHNURL(String url) {
        if (url == null)
            return null;
        
        String hnurl = "https://news.ycombinator.com/";
        
        if (url.startsWith("http") || url.startsWith("ftp")) {
            return url;
        } else if (url.startsWith("/"))
            return hnurl + url.substring(1);
        else
            return hnurl + url;
    }

}
