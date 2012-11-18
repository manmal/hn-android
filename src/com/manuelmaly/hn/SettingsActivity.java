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
    
    public enum FONTSIZE {FONTSIZE_SMALL, FONTSIZE_NORMAL, FONTSIZE_BIG}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        addPreferencesFromResource(R.xml.preferences);
        
        Preference connectionPref = findPreference(PREF_FONTSIZE);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        connectionPref.setSummary(sharedPref.getString(PREF_FONTSIZE, "Undefined"));
        
        View backView = (ImageView)findViewById(R.id.actionbar_back);
        backView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_FONTSIZE)) {
            Preference connectionPref = findPreference(key);
            connectionPref.setSummary(sharedPreferences.getString(key, "Undefined"));
        }
    }
 
    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
    
    public static FONTSIZE getFontSize(Context c) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        String fontSizeSetting = sharedPref.getString(PREF_FONTSIZE, "Normal");
        if (fontSizeSetting.equals("Small"))
            return FONTSIZE.FONTSIZE_SMALL;
        else if (fontSizeSetting.equals("Normal"))
            return FONTSIZE.FONTSIZE_NORMAL;
        else
            return FONTSIZE.FONTSIZE_BIG;
    }
    
}
