package com.manuelmaly.hn;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    public static final String PREF_FONTSIZE = "pref_fontsize";
    public static final String PREF_HTMLPROVIDER = "pref_htmlprovider";
    public static final String PREF_HTMLVIEWER = "pref_htmlviewer";

    public enum FONTSIZE {
        FONTSIZE_SMALL, FONTSIZE_NORMAL, FONTSIZE_BIG
    }

    public enum HTMLPROVIDER {
        HTMLPROVIDER_ORIGINAL_ARTICLE_URL, HTMLPROVIDER_GOOGLE, HTMLPROVIDER_VIEWTEXT
    }
    
    public enum HTMLVIEWER {
        HTMLVIEWER_WITHINAPP, HTMLVIEWER_BROWSER
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Preference fontSizePref = findPreference(PREF_FONTSIZE);
        fontSizePref.setSummary(sharedPref.getString(PREF_FONTSIZE, "Undefined"));

        Preference htmlProviderPref = findPreference(PREF_HTMLPROVIDER);
        htmlProviderPref.setSummary(sharedPref.getString(PREF_HTMLPROVIDER, "Undefined"));
        
        Preference htmlViewerPref = findPreference(PREF_HTMLVIEWER);
        htmlViewerPref.setSummary(sharedPref.getString(PREF_HTMLVIEWER, "Undefined"));

        View backView = (ImageView) findViewById(R.id.actionbar_back);
        backView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_FONTSIZE) || key.equals(PREF_HTMLPROVIDER) || key.equals(PREF_HTMLVIEWER))
            findPreference(key).setSummary(sharedPreferences.getString(key, "Undefined"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

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

}
