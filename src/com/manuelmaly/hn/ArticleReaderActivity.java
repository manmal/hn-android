package com.manuelmaly.hn;

import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.ViewById;
import com.manuelmaly.hn.model.HNPost;
import com.manuelmaly.hn.reuse.ViewRotator;
import com.manuelmaly.hn.util.FontHelper;

@EActivity(R.layout.article_activity)
public class ArticleReaderActivity extends Activity {

    public static final String EXTRA_HNPOST = "HNPOST";
    public static final String EXTRA_HTMLPROVIDER_OVERRIDE = "HTMLPROVIDER_OVERRIDE";

    private static final String HTMLPROVIDER_PREFIX_VIEWTEXT = "http://viewtext.org/article?url=";
    private static final String HTMLPROVIDER_PREFIX_GOOGLE = "http://www.google.com/gwt/x?u=";

    @ViewById(R.id.article_webview)
    WebView mWebView;

    @ViewById(R.id.actionbar)
    FrameLayout mActionbarContainer;

    @ViewById(R.id.actionbar_title_button)
    Button mActionbarTitle;

    @ViewById(R.id.actionbar_share)
    ImageView mActionbarShare;

    @ViewById(R.id.actionbar_back)
    ImageView mActionbarBack;

    @ViewById(R.id.actionbar_refresh)
    ImageView mActionbarRefresh;

    @SystemService
    LayoutInflater mInflater;

    HNPost mPost;
    String mHtmlProvider;

    boolean mIsLoading;

    @AfterViews
    public void init() {
        mActionbarTitle.setTypeface(FontHelper.getComfortaa(this, true));
        mActionbarTitle.setText(getString(R.string.article));
        mActionbarTitle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ArticleReaderActivity.this, CommentsActivity_.class);
                i.putExtra(CommentsActivity.EXTRA_HNPOST, mPost);
                if (getIntent().getStringExtra(EXTRA_HTMLPROVIDER_OVERRIDE) != null)
                    i.putExtra(EXTRA_HTMLPROVIDER_OVERRIDE, getIntent().getStringExtra(EXTRA_HTMLPROVIDER_OVERRIDE));
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        mActionbarBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        mActionbarRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (mWebView.getProgress() < 100 && mIsLoading) {
                    mWebView.stopLoading();
                    mIsLoading = false;
                    ViewRotator.stopRotating(mActionbarRefresh);
                } else {
                    mIsLoading = true;
                    ViewRotator.startRotating(mActionbarRefresh);
                    mWebView.loadUrl(getArticleViewURL(mPost, mHtmlProvider, ArticleReaderActivity.this));
                }
            }
        });

        mActionbarShare.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, mPost.getTitle());
                i.putExtra(Intent.EXTRA_TEXT, mPost.getURL());
                startActivity(Intent.createChooser(i, getString(R.string.share_article_url)));
            }
        });

        mPost = (HNPost) getIntent().getSerializableExtra(EXTRA_HNPOST);
        if (mPost != null && mPost.getURL() != null) {
            String htmlProviderOverride = getIntent().getStringExtra(EXTRA_HTMLPROVIDER_OVERRIDE);
            if (htmlProviderOverride != null)
                mHtmlProvider = htmlProviderOverride;
            else
                mHtmlProvider = Settings.getHtmlProvider(this);
            mWebView.loadUrl(getArticleViewURL(mPost, mHtmlProvider, this));
        }
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new HNReaderWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100 && mIsLoading) {
                    mIsLoading = false;
                    ViewRotator.stopRotating(mActionbarRefresh);
                } else if (!mIsLoading) {
                    // Most probably, user tapped on a link in the webview -
                    // let's spin the refresh icon:
                    mIsLoading = true;
                    ViewRotator.startRotating(mActionbarRefresh);
                }
            }
        });

        mIsLoading = true;
        ViewRotator.startRotating(mActionbarRefresh);
    }

    public static String getArticleViewURL(HNPost post, String htmlProvider, Context c) {
        String encodedURL = URLEncoder.encode(post.getURL());
        if (htmlProvider.equals(c.getString(R.string.pref_htmlprovider_viewtext)))
            return HTMLPROVIDER_PREFIX_VIEWTEXT + encodedURL;
        else if (htmlProvider.equals(c.getString(R.string.pref_htmlprovider_google)))
            return HTMLPROVIDER_PREFIX_GOOGLE + encodedURL;
        else
            return post.getURL();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack())
            mWebView.goBack();
        else
            super.onBackPressed();
    }

    private class HNReaderWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}