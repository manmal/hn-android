package com.manuelmaly.hnreader;

import android.app.Activity;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.ViewById;
import com.manuelmaly.hnreader.model.HNPost;

@EActivity(R.layout.article_activity)
public class ArticleReaderActivity extends Activity {

    public static final String EXTRA_HNPOST = "HNPOST";

    @SystemService
    LayoutInflater mInflater;

    @ViewById(R.id.article_webview)
    WebView mWebView;

    HNPost mPost;

    @AfterViews
    public void init() {
        mPost = (HNPost) getIntent().getSerializableExtra(EXTRA_HNPOST);
        if (mPost != null && mPost.getURL() != null)
            mWebView.loadUrl(mPost.getURL());
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new HNReaderWebViewClient());
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