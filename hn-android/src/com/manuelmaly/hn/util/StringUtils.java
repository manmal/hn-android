package com.manuelmaly.hn.util;

public class StringUtils {

    public static CharSequence trimTrailingWhitespace(CharSequence source) {

        if(source == null)
            return "";

        int i = source.length();

        // loop back to the first non-whitespace character
        while(--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        return source.subSequence(0, i+1);
    }
}
