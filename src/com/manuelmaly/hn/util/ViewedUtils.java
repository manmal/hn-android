package com.manuelmaly.hn.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ViewedUtils {

  private static final String PREFS_VIEWED_ACTIVITES = "viewed_activites";

  public static void setActivityViewed( Activity activity ) {
    SharedPreferences prefs = activity.getSharedPreferences( PREFS_VIEWED_ACTIVITES, Context.MODE_PRIVATE );
    Editor editor = prefs.edit();
    editor.putBoolean( activity.getClass().getCanonicalName(), true );
    editor.commit();
  }

  public static boolean getActivityViewed( Activity activity ) {
    SharedPreferences prefs = activity.getSharedPreferences( PREFS_VIEWED_ACTIVITES, Context.MODE_PRIVATE );
    return prefs.getBoolean( activity.getClass().getCanonicalName(), false );
  }
}
