package com.manuelmaly.hn;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.manuelmaly.hn.util.FontHelper;

@EActivity(R.layout.about)
public abstract class AboutActivity extends Activity {

    @ViewById(R.id.actionbar_back)
    ImageView mActionbarBack;
    
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
        mURLView.setText(Html.fromHtml("<a href=\"mailto:me@manuelmaly.com\">me@manuelmaly.com</a>"));
        
        mGithubView.setMovementMethod(LinkMovementMethod.getInstance());
        mGithubView.setText(Html.fromHtml("<a href=\"https://github.com/manmal/hn-android/\">Get the Sourcecode</a>"));
    }

    @Click(R.id.actionbar_back)
    void backClicked() {
        finish();
    }

}