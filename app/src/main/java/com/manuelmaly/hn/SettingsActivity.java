package com.manuelmaly.hn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.manuelmaly.hn.login.LoginActivity_;
import com.manuelmaly.hn.server.HNCredentials;
import com.manuelmaly.hn.util.Run;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    public enum FONTSIZE {
        FONTSIZE_SMALL, FONTSIZE_NORMAL, FONTSIZE_BIG
    }

    public enum HTMLPROVIDER {
        HTMLPROVIDER_ORIGINAL_ARTICLE_URL,
        HTMLPROVIDER_GOOGLE,
        HTMLPROVIDER_VIEWTEXT,
        HTMLPROVIDER_INSTAPAPER
    }

    public enum HTMLVIEWER {
        HTMLVIEWER_WITHINAPP, HTMLVIEWER_BROWSER
    }

    private static final int REQUEST_LOGIN = 100;
    private Preference mUserPref;

    @Override
    @SuppressWarnings("deprecation")
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

        mUserPref= (UserPreference) findPreference(Settings.PREF_USER);
        mUserPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
              String userName = Settings.getUserName(SettingsActivity.this);

              if (userName == null || userName.equals("")) {
                onLoginClick();
              } else {
                onLogoutClik();
              }

              return false;
            }
        });

        updateUserItem();

        View backView = (ImageView) findViewById(R.id.actionbar_back);
        backView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        Run.onUiThread(new Runnable() {
            @Override
            public void run() {
                if (key.equals(Settings.PREF_FONTSIZE) || key.equals(Settings.PREF_HTMLPROVIDER)
                    || key.equals(Settings.PREF_HTMLVIEWER))
                    findPreference(key).setSummary(sharedPreferences.getString(key, "Undefined"));
                else if (key.equals(Settings.PREF_USER)) {
                    HNCredentials.invalidate();
                    updateUserItem();
                }
            }
        }, this);
    }

    private void updateUserItem() {
        String userName = Settings.getUserName(this);
        if (!userName.equals(""))
            mUserPref.setSummary(userName);
        else
            mUserPref.setSummary(" ");
    }

    private void onLoginClick() {
      Intent intent = new Intent(SettingsActivity.this, LoginActivity_.class);
      startActivityForResult(intent, REQUEST_LOGIN);
    }

    private void onLogoutClik() {
      AlertDialog logoutDialog = new AlertDialog.Builder(SettingsActivity.this).create();
      logoutDialog.setTitle(R.string.logout_question_lbl);
      logoutDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
          Settings.clearUserData(SettingsActivity.this);
        }
      });

      logoutDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (Message) null);
      logoutDialog.show();
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
        case REQUEST_LOGIN:
            // If there was a successful login, then we want to show that to
            // the user
            if(resultCode == RESULT_OK) {
                updateUserItem();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
