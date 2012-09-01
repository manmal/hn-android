package com.manuelmaly.hn;

import android.app.Activity;
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
import com.manuelmaly.hn.reuse.ImageViewFader;
import com.manuelmaly.hn.reuse.ViewRotator;
import com.manuelmaly.hn.task.HNFeedTask;
import com.manuelmaly.hn.util.FontHelper;
import com.manuelmaly.hn.util.Run;

@EActivity(R.layout.article_activity)
public class ArticleReaderActivity extends Activity {

    public static final String EXTRA_HNPOST = "HNPOST";

    @SystemService
    LayoutInflater mInflater;

    @ViewById(R.id.article_webview)
    WebView mWebView;

    @ViewById(R.id.actionbar)
    FrameLayout mActionbarLayout;

    @ViewById(R.id.actionbar_title_button)
    Button mActionbarTitleButton;

    @ViewById(R.id.actionbar_share)
    ImageView mShareImageView;

    @ViewById(R.id.actionbar_back)
    ImageView mBackImageView;

    @ViewById(R.id.actionbar_refresh)
    ImageView mRefreshImageView;

    HNPost mPost;

    boolean mIsLoading;

    @AfterViews
    public void init() {
        mActionbarTitleButton.setTypeface(FontHelper.getComfortaa(this, true));
        mActionbarTitleButton.setText(getString(R.string.article));
        mActionbarTitleButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ArticleReaderActivity.this, CommentsActivity_.class);
                i.putExtra(CommentsActivity.EXTRA_HNPOST, mPost);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        mBackImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        mRefreshImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (mWebView.getProgress() < 100 && mIsLoading) {
                    mWebView.stopLoading();
                    mIsLoading = false;
                    ViewRotator.stopRotating(mRefreshImageView);
                } else {
                    mIsLoading = true;
                    ViewRotator.startRotating(mRefreshImageView);
                    mWebView.loadUrl(mPost.getURL());
                }
            }
        });
        
        mShareImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_article_url));
                i.putExtra(Intent.EXTRA_TEXT, mPost.getURL());
                startActivity(Intent.createChooser(i, getString(R.string.share_article_url)));
            }
        });

        mPost = (HNPost) getIntent().getSerializableExtra(EXTRA_HNPOST);
        if (mPost != null && mPost.getURL() != null)
            mWebView.loadUrl(mPost.getURL());
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new HNReaderWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100 && mIsLoading) {
                    mIsLoading = false;
                    ViewRotator.stopRotating(mRefreshImageView);
                } else if (!mIsLoading) {
                    // Most probably, user tapped on a link in the webview - 
                    // let's spin the refresh icon:
                    mIsLoading = true;
                    ViewRotator.startRotating(mRefreshImageView);
                }
            }
        });

        mIsLoading = true;
        ViewRotator.startRotating(mRefreshImageView);

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