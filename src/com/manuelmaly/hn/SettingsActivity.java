package com.manuelmaly.hn;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.manuelmaly.hn.server.HNCredentials;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

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

        Preference fontSizePref = findPreference(Settings.PREF_FONTSIZE);
        fontSizePref.setSummary(sharedPref.getString(Settings.PREF_FONTSIZE, "Undefined"));

        Preference htmlProviderPref = findPreference(Settings.PREF_HTMLPROVIDER);
        htmlProviderPref.setSummary(sharedPref.getString(Settings.PREF_HTMLPROVIDER, "Undefined"));

        Preference htmlViewerPref = findPreference(Settings.PREF_HTMLVIEWER);
        htmlViewerPref.setSummary(sharedPref.getString(Settings.PREF_HTMLVIEWER, "Undefined"));

        updateUserItem();

        View backView = (ImageView) findViewById(R.id.actionbar_back);
        backView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Settings.PREF_FONTSIZE) || key.equals(Settings.PREF_HTMLPROVIDER)
            || key.equals(Settings.PREF_HTMLVIEWER))
            findPreference(key).setSummary(sharedPreferences.getString(key, "Undefined"));
        else if (key.equals(Settings.PREF_USER)) {
            HNCredentials.invalidate();
            updateUserItem();
        }
    }
    
    private void updateUserItem() {
        UserPreference userPref = (UserPreference) findPreference(Settings.PREF_USER);
        userPref.setActivity(this);
        
        String userName = Settings.getUserName(this);
        if (!userName.equals(""))
            userPref.setSummary(userName);
        else
            userPref.setSummary(" ");
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

}
