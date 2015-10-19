package com.manuelmaly.hn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.manuelmaly.hn.model.HNPost;
import com.manuelmaly.hn.util.FontHelper;
import com.manuelmaly.hn.util.SpotlightActivity;
import com.manuelmaly.hn.util.ViewedUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import java.net.URLEncoder;

@EActivity(R.layout.article_activity)
public class ArticleReaderActivity extends ActionBarActivity {

  public static final int ACTIVITY_LOGIN = 137;

  private static final String WEB_VIEW_SAVED_STATE_KEY = "webViewSavedState";
  public static final String EXTRA_HNPOST = "HNPOST";
  public static final String EXTRA_HTMLPROVIDER_OVERRIDE = "HTMLPROVIDER_OVERRIDE";

  private static final String HTMLPROVIDER_PREFIX_VIEWTEXT = "http://viewtext.org/article?url=";
  private static final String HTMLPROVIDER_PREFIX_GOOGLE = "http://www.google.com/gwt/x?u=";
  private static final String HTMLPROVIDER_PREFIX_INSTAPAPER = "http://www.instapaper.com/text?u=";

  @ViewById(R.id.article_webview)
  WebView mWebView;

  @ViewById(R.id.article_swiperefreshlayout)
  SwipeRefreshLayout mSwipeRefreshLayout;

  TextView mActionbarTitle;

  @SystemService
  LayoutInflater mInflater;

  HNPost mPost;
  String mHtmlProvider;

  boolean mShouldShowRefreshing = false;
  private Bundle mWebViewSavedState;

  boolean mWebViewIsLoading = false;

  @AfterViews
  @SuppressLint("SetJavaScriptEnabled")
  public void init() {
    mActionbarTitle = (TextView) getSupportActionBar().getCustomView().findViewById( R.id.actionbar_title );

    mPost = (HNPost) getIntent().getSerializableExtra( EXTRA_HNPOST );
    if (mPost != null && mPost.getURL() != null) {
      String htmlProviderOverride = getIntent().getStringExtra( EXTRA_HTMLPROVIDER_OVERRIDE );
      if (htmlProviderOverride != null) {
        mHtmlProvider = htmlProviderOverride;
      } else {
        mHtmlProvider = Settings.getHtmlProvider( this );
      }
      mWebView.loadUrl( getArticleViewURL( mPost, mHtmlProvider, this ) );
    }

    mWebView.getSettings().setBuiltInZoomControls( true );
    mWebView.getSettings().setLoadWithOverviewMode( true );
    mWebView.getSettings().setUseWideViewPort( true );
    mWebView.getSettings().setJavaScriptEnabled( true );
    mWebView.setWebViewClient( new HNReaderWebViewClient() );

    if (mWebViewSavedState != null) {
      mWebView.restoreState( mWebViewSavedState );
    }

    toggleSwipeRefreshLayout();

    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        mWebView.loadUrl(getArticleViewURL(mPost, mHtmlProvider, ArticleReaderActivity.this));
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();

    mActionbarTitle.setTypeface( FontHelper.getComfortaa( this, true ) );
    mActionbarTitle.setText( getString( R.string.article ) );
    mActionbarTitle.setOnClickListener( new OnClickListener() {
      @Override
      public void onClick( View v ) {
        launchCommentsActivity();
      }
    } );

    if (!ViewedUtils.getActivityViewed( this )) {
      Handler handler = new Handler( Looper.getMainLooper() );
      handler.postDelayed( new Runnable() {

        @Override
        public void run() {
          int[] posArray = new int[2];
          mActionbarTitle.getLocationInWindow( posArray );
          Intent intent = SpotlightActivity.intentForSpotlightActivity( ArticleReaderActivity.this, posArray[0],
              mActionbarTitle.getWidth(), 0, getSupportActionBar().getHeight(), getString( R.string.click_on_article ) );
          startActivityForResult( intent, ACTIVITY_LOGIN );
          overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out );
        }
      }, 250 );
      ViewedUtils.setActivityViewed( this );
    }

    // User may have toggled pull-down refresh, so toggle the SwipeRefreshLayout.
    toggleSwipeRefreshLayout();
  }

  @Override
  public boolean onCreateOptionsMenu( Menu menu ) {
    getMenuInflater().inflate( R.menu.menu_share_refresh, menu );
    return super.onCreateOptionsMenu( menu );
  }

  @Override
  public boolean onPrepareOptionsMenu( Menu menu ) {
    MenuItem refreshItem = menu.findItem( R.id.menu_refresh );

    if (!mShouldShowRefreshing) {
      MenuItemCompat.setActionView( refreshItem, null );
    } else {
      View refreshView = mInflater.inflate( R.layout.refresh_icon, null );
      MenuItemCompat.setActionView( refreshItem, refreshView );
    }

    return super.onPrepareOptionsMenu( menu );
  }

  @Override
  public boolean onOptionsItemSelected( MenuItem item ) {
    switch (item.getItemId()) {
    case android.R.id.home:
      finish();
      return true;
    case R.id.menu_refresh:
      if (mWebViewIsLoading) {
        mWebView.stopLoading();
      } else {
        mWebView.loadUrl( getArticleViewURL( mPost, mHtmlProvider, ArticleReaderActivity.this ) );
      }
      return true;
    case R.id.menu_share:
      Intent shareIntent = new Intent( Intent.ACTION_SEND );
      shareIntent.setType( "text/plain" );
      shareIntent.putExtra( Intent.EXTRA_SUBJECT, mPost.getTitle() );
      shareIntent.putExtra( Intent.EXTRA_TEXT, mWebView.getUrl() );
      startActivity( Intent.createChooser( shareIntent, getString( R.string.share_article_url ) ) );
      return true;
    default:
      return super.onOptionsItemSelected( item );
    }
  }

  private void toggleSwipeRefreshLayout() {
    mSwipeRefreshLayout.setEnabled(Settings.isPullDownRefresh(ArticleReaderActivity.this));
  }

  @SuppressWarnings("deprecation")
  public static String getArticleViewURL( HNPost post, String htmlProvider, Context c ) {
    String encodedURL = URLEncoder.encode( post.getURL() );
    if (htmlProvider.equals( c.getString( R.string.pref_htmlprovider_viewtext ) )) {
      return HTMLPROVIDER_PREFIX_VIEWTEXT + encodedURL;
    } else if (htmlProvider.equals( c.getString( R.string.pref_htmlprovider_google ) )) {
      return HTMLPROVIDER_PREFIX_GOOGLE + encodedURL;
    } else if (htmlProvider.equals( c.getString( R.string.pref_htmlprovider_instapaper ) )) {
      return HTMLPROVIDER_PREFIX_INSTAPAPER + encodedURL;
    } else {
      return post.getURL();
    }
  }

  @Override
  public void onBackPressed() {
    if (mWebView.canGoBack()) {
      mWebView.goBack();
    } else {
      super.onBackPressed();
    }
  }

  @Override
  protected void onSaveInstanceState( Bundle outState ) {
    Bundle webViewSavedState = new Bundle();
    mWebView.saveState( webViewSavedState );
    outState.putBundle( WEB_VIEW_SAVED_STATE_KEY, webViewSavedState );
    super.onSaveInstanceState( outState );
  }

  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    if (savedInstanceState != null) {
      mWebViewSavedState = savedInstanceState.getBundle( WEB_VIEW_SAVED_STATE_KEY );
    }
  }

  @Override
  protected void onDestroy() {
    mWebView.loadData( "", "text/html", "utf-8" ); // Destroy any players (e.g.
                                                   // Youtube, Soundcloud) if
                                                   // any
    // Calling mWebView.destroy(); would not always work according to here:
    // http://stackoverflow.com/questions/6201615/how-do-i-stop-flash-after-leaving-a-webview?rq=1

    super.onDestroy();
  }

  @Override
  public void onActivityResult( int requestCode, int resultCode, Intent data ) {
    switch (requestCode) {
    case ACTIVITY_LOGIN:
      if (resultCode == RESULT_OK) {
        launchCommentsActivity();
      }
    }
  }

  private void launchCommentsActivity() {
    Intent i = new Intent( ArticleReaderActivity.this, CommentsActivity_.class );
    i.putExtra( CommentsActivity.EXTRA_HNPOST, mPost );
    if (getIntent().getStringExtra( EXTRA_HTMLPROVIDER_OVERRIDE ) != null) {
      i.putExtra( EXTRA_HTMLPROVIDER_OVERRIDE, getIntent().getStringExtra( EXTRA_HTMLPROVIDER_OVERRIDE ) );
    }
    startActivity( i );
    overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out );
    finish();
  }

  private void setShowRefreshing(boolean showRefreshing) {
    if (!Settings.isPullDownRefresh(ArticleReaderActivity.this)) {
      mShouldShowRefreshing = showRefreshing;
      supportInvalidateOptionsMenu();
    }

    if (mSwipeRefreshLayout.isEnabled() && (!mSwipeRefreshLayout.isRefreshing() || !showRefreshing)) {
      mSwipeRefreshLayout.setRefreshing(showRefreshing);
    }
  }

  private class HNReaderWebViewClient extends WebViewClient {
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
      setShowRefreshing(true);
      mWebViewIsLoading = true;

      super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      setShowRefreshing(false);
      mWebViewIsLoading = false;

      super.onPageFinished(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading( WebView view, String url ) {
      view.loadUrl( url );
      return true;
    }
  }

}
