package com.manuelmaly.hn;

import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.manuelmaly.hn.util.FontHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.about)
public class AboutActivity extends ActionBarActivity {
    
    @ViewById(R.id.about_hn)
    TextView mHNView;
    
    @ViewById(R.id.about_by)
    TextView mByView;
    
    @ViewById(R.id.about_url)
    TextView mURLView;
    
    @ViewById(R.id.about_github)
    TextView mGithubView;

    @AfterViews
    public void init() {
        Typeface tf = FontHelper.getComfortaa(this, true);
        mHNView.setTypeface(tf);
        
        mURLView.setMovementMethod(LinkMovementMethod.getInstance());
        mURLView.setText(Html.fromHtml("<a href=\"http://www.creativepragmatics.com\">creativepragmatics.com</a>"));
        
        mGithubView.setMovementMethod(LinkMovementMethod.getInstance());
        mGithubView.setText(Html.fromHtml("<a href=\"https://github.com/manmal/hn-android/\">Fork this at Github</a>"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // TODO: properly start the home activity with flags and such
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}