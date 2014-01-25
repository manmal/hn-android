package com.manuelmaly.hn;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.ViewById;
import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.model.HNPost;
import com.manuelmaly.hn.parser.BaseHTMLParser;
import com.manuelmaly.hn.server.HNCredentials;
import com.manuelmaly.hn.task.HNFeedTaskLoadMore;
import com.manuelmaly.hn.task.HNFeedTaskMainFeed;
import com.manuelmaly.hn.task.HNSearchTask;
import com.manuelmaly.hn.task.HNVoteTask;
import com.manuelmaly.hn.task.ITaskFinishedHandler;
import com.manuelmaly.hn.util.FileUtil;
import com.manuelmaly.hn.util.FontHelper;

@EActivity(R.layout.main)
public class MainActivity extends BaseListActivity implements ITaskFinishedHandler<HNFeed> {

  @ViewById(R.id.main_list)
  ListView mPostsList;

  @ViewById(R.id.main_root)
  LinearLayout mRootView;

  @ViewById(R.id.actionbar_title)
  TextView mActionbarTitle;

  @ViewById(R.id.actionbar_refresh)
  ImageView mActionbarRefresh;

  @ViewById(R.id.actionbar_refresh_container)
  LinearLayout mActionbarRefreshContainer;

  @ViewById(R.id.actionbar_refresh_progress)
  ProgressBar mActionbarRefreshProgress;

  @ViewById(R.id.actionbar_more)
  ImageView mActionbarMore;

  EditText mSearchField;

  @SystemService
  LayoutInflater mInflater;

  @SystemService
  InputMethodManager mInputMethodManager;

  TextView mEmptyListPlaceholder;
  HNFeed mFeed;
  HNFeed mFeedBackup;
  PostsAdapter mPostsListAdapter;
  Set<HNPost> mUpvotedPosts;
  Set<Integer> mAlreadyRead;

  String mCurrentFontSize = null;
  int mFontSizeTitle;
  int mFontSizeDetails;
  int mTitleColor;
  int mTitleReadColor;
  boolean mActivityStart;

  private static final int TASKCODE_LOAD_FEED = 10;
  private static final int TASKCODE_LOAD_MORE_POSTS = 20;
  private static final int TASKCODE_SEARCH_RESULTS = 30;
  private static final int TASKCODE_VOTE = 100;

  private static final String LIST_STATE = "listState";
  private static final String ALREADY_READ_ARTICLES_KEY = "HN_ALREADY_READ";
  private Parcelable mListState = null;

  @AfterViews
  public void init() {
    mActivityStart = true;
    mFeed = new HNFeed( new ArrayList<HNPost>(), null, "" );
    mPostsListAdapter = new PostsAdapter();
    mUpvotedPosts = new HashSet<HNPost>();
    mActionbarRefresh.setImageDrawable( getResources().getDrawable( R.drawable.refresh ) );
    mActionbarTitle.setTypeface( FontHelper.getComfortaa( this, true ) );

    mActionbarRefreshProgress.setVisibility( View.GONE );
    mEmptyListPlaceholder = getEmptyTextView( mRootView );
    mPostsList.setEmptyView( mEmptyListPlaceholder );
    mPostsList.setAdapter( mPostsListAdapter );
    mPostsList.setOnScrollListener( new OnScrollListener() {

      private boolean searchIsVisible = false;
      private boolean searchWasVisible = false;

      @Override
      public void onScrollStateChanged( AbsListView view, int scrollState ) {

      }

      @Override
      public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount ) {
        if (firstVisibleItem == 0 && mSearchField != null) {
          searchIsVisible = true;

        }

        if (firstVisibleItem > 0 && mSearchField != null) {
          searchIsVisible = false;
        }

        toggle();
      }

      private void toggle() {
        if (searchIsVisible != searchWasVisible) {
          if (searchIsVisible) {
            mSearchField.requestFocus();
            mInputMethodManager.showSoftInput( mSearchField, 0 );
          } else {
            mInputMethodManager.hideSoftInputFromWindow( mSearchField.getWindowToken(), 0 );
            mSearchField.clearFocus();
          }
        }

        searchWasVisible = searchIsVisible;
      }
    } );

    mEmptyListPlaceholder.setTypeface( FontHelper.getComfortaa( this, true ) );

    mTitleColor = getResources().getColor( R.color.dark_gray_post_title );
    mTitleReadColor = getResources().getColor( R.color.gray_post_title_read );

    loadAlreadyReadCache();
    loadIntermediateFeedFromStore();
    startFeedLoading();
  }

  @Override
  protected void onResume() {
    super.onResume();

    boolean registeredUserChanged = mFeed.getUserAcquiredFor() != null
        && (!mFeed.getUserAcquiredFor().equals( Settings.getUserName( this ) ));

    // We want to reload the feed if a new user logged in
    if (HNCredentials.isInvalidated() || registeredUserChanged) {
      showFeed( new HNFeed( new ArrayList<HNPost>(), null, "" ) );
      startFeedLoading();
    }

    // refresh if font size changed
    if (refreshFontSizes())
      mPostsListAdapter.notifyDataSetChanged();

    // restore vertical scrolling position if applicable
    if (mListState != null)
      mPostsList.onRestoreInstanceState( mListState );
    mListState = null;
  }

  @Click(R.id.actionbar)
  void actionBarClicked() {
    adjustScrollTopPositionIfNecessary( true );
  }

  @Click(R.id.actionbar_refresh_container)
  void refreshClicked() {
    if (HNFeedTaskMainFeed.isRunning( getApplicationContext() ))
      HNFeedTaskMainFeed.stopCurrent( getApplicationContext() );
    else
      startFeedLoading();
  }

  @Click(R.id.actionbar_more)
  void moreClicked() {
    mActionbarMore.setSelected( true );
    LinearLayout moreContentView = (LinearLayout) mInflater.inflate( R.layout.main_more_content, null );

    moreContentView.measure( View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED );
    final PopupWindow popupWindow = new PopupWindow( this );
    popupWindow.setBackgroundDrawable( new ColorDrawable( getResources().getColor( R.color.red_dark_washedout ) ) );
    popupWindow.setContentView( moreContentView );
    popupWindow.showAsDropDown( mActionbarMore );
    popupWindow.setTouchable( true );
    popupWindow.setFocusable( true );
    popupWindow.setOutsideTouchable( true );
    popupWindow.setOnDismissListener( new OnDismissListener() {
      public void onDismiss() {
        mActionbarMore.setSelected( false );
      }
    } );

    Button settingsButton = (Button) moreContentView.findViewById( R.id.main_more_content_settings );
    settingsButton.setOnClickListener( new OnClickListener() {
      @Override
      public void onClick( View v ) {
        startActivity( new Intent( MainActivity.this, SettingsActivity.class ) );
        popupWindow.dismiss();
      }
    } );

    Button aboutButton = (Button) moreContentView.findViewById( R.id.main_more_content_about );
    aboutButton.setOnClickListener( new OnClickListener() {
      @Override
      public void onClick( View v ) {
        startActivity( new Intent( MainActivity.this, AboutActivity_.class ) );
        popupWindow.dismiss();
      }
    } );

    popupWindow.update( moreContentView.getMeasuredWidth(), moreContentView.getMeasuredHeight() );
  }

  @Override
  public void onTaskFinished( int taskCode, TaskResultCode code, HNFeed result, Object tag ) {
    if (taskCode == TASKCODE_LOAD_FEED) {
      if (code.equals( TaskResultCode.Success ) && mPostsListAdapter != null)
        showFeed( result );
      else if (!code.equals( TaskResultCode.Success ))
        Toast.makeText( this, getString( R.string.error_unable_to_retrieve_feed ), Toast.LENGTH_SHORT ).show();

      mActionbarRefreshProgress.setVisibility( View.GONE );
      mActionbarRefresh.setVisibility( View.VISIBLE );
    } else if (taskCode == TASKCODE_LOAD_MORE_POSTS) {
      if (!code.equals( TaskResultCode.Success ))
        Toast.makeText( this, getString( R.string.error_unable_to_load_more ), Toast.LENGTH_SHORT ).show();

      mFeed.appendLoadMoreFeed( result );
      mPostsListAdapter.notifyDataSetChanged();
    } else if (taskCode == TASKCODE_SEARCH_RESULTS) {
      System.out.println( "SEARCH COMPLETE" );
      showSearchResults( result );
    }

  }

  @Background
  void loadAlreadyReadCache() {
    if (mAlreadyRead == null)
      mAlreadyRead = new HashSet<Integer>();

    SharedPreferences sharedPref = getSharedPreferences( ALREADY_READ_ARTICLES_KEY, Context.MODE_PRIVATE );
    Editor editor = sharedPref.edit();
    Map<String, ?> read = sharedPref.getAll();
    Long now = new Date().getTime();

    for (Map.Entry<String, ?> entry : read.entrySet()) {
      Long readAt = (Long) entry.getValue();
      Long diff = (now - readAt) / (24 * 60 * 60 * 1000);
      if (diff >= 2)
        editor.remove( entry.getKey() );
      else
        mAlreadyRead.add( entry.getKey().hashCode() );
    }
    editor.commit();
  }

  @Background
  void markAsRead( HNPost post ) {
    Long now = new Date().getTime();
    String title = post.getTitle();
    Editor editor = getSharedPreferences( ALREADY_READ_ARTICLES_KEY, Context.MODE_PRIVATE ).edit();
    editor.putLong( title, now );
    editor.commit();

    mAlreadyRead.add( title.hashCode() );
  }

  private void showFeed( HNFeed feed ) {
    System.out.println( "Showing feed with posts: " + feed.getPosts().size() );
    mFeed = feed;
    mPostsListAdapter.notifyDataSetChanged();
    adjustScrollTopPositionIfNecessary( false );
  }

  private void showSearchResults( HNFeed feed ) {
    if (mFeedBackup == null) {
      mFeedBackup = mFeed;
    }
    showFeed( feed );
  }

  private void clearSearchResults() {
    if (mFeedBackup == null) {
      refreshClicked();
    } else {
      showFeed( mFeedBackup );
      mFeedBackup = null;
    }
  }

  private void adjustScrollTopPositionIfNecessary( boolean forceToTop ) {
    if (mPostsList.getFirstVisiblePosition() == 0 || forceToTop) {
      if (mActivityStart) {
        mPostsList.setSelectionFromTop( 1, 0 );
      } else {
        // This hack is needed because smoothScrollToPosition does not align the
        // first post item view with the action bar.
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
          Method m;
          try {
            m = ListView.class.getMethod( "smoothScrollToPositionFromTop", Integer.TYPE, Integer.TYPE );
            m.invoke( mPostsList, 1, 0 );
          } catch (Exception e) {
            mPostsList.setSelectionFromTop( 1, 0 );
          }
        } else {
          mPostsList.setSelectionFromTop( 1, 0 );
        }
      }
    }

  }

  private void loadIntermediateFeedFromStore() {
    new GetLastHNFeedTask().execute( (Void) null );
    long start = System.currentTimeMillis();

    Log.i( "", "Loading intermediate feed took ms:" + (System.currentTimeMillis() - start) );
  }

  class GetLastHNFeedTask extends FileUtil.GetLastHNFeedTask {
    ProgressDialog progress;

    @Override
    protected void onPreExecute() {
      progress = new ProgressDialog( MainActivity.this );
      progress.setMessage( "Loading" );
      progress.show();
    }

    protected void onPostExecute( HNFeed result ) {
      if (progress != null && progress.isShowing())
        progress.dismiss();

      if (result != null && result.getUserAcquiredFor() != null
          && result.getUserAcquiredFor().equals( Settings.getUserName( App.getInstance() ) ))
        showFeed( result );

      mActivityStart = false;
    }
  }

  private void startFeedLoading() {
    HNFeedTaskMainFeed.startOrReattach( this, this, TASKCODE_LOAD_FEED );
    mActionbarRefresh.setImageResource( R.drawable.refresh );

    mActionbarRefreshProgress.setVisibility( View.VISIBLE );
    mActionbarRefresh.setVisibility( View.GONE );
  }

  private boolean refreshFontSizes() {
    final String fontSize = Settings.getFontSize( this );
    if ((mCurrentFontSize == null) || (!mCurrentFontSize.equals( fontSize ))) {
      mCurrentFontSize = fontSize;
      if (fontSize.equals( getString( R.string.pref_fontsize_small ) )) {
        mFontSizeTitle = 15;
        mFontSizeDetails = 11;
      } else if (fontSize.equals( getString( R.string.pref_fontsize_normal ) )) {
        mFontSizeTitle = 18;
        mFontSizeDetails = 12;
      } else {
        mFontSizeTitle = 22;
        mFontSizeDetails = 15;
      }
      return true;
    } else {
      return false;
    }
  }

  private void vote( String voteURL, HNPost post ) {
    HNVoteTask.start( voteURL, MainActivity.this, new VoteTaskFinishedHandler(), TASKCODE_VOTE, post );
  }

  @Override
  protected void onRestoreInstanceState( Bundle state ) {
    super.onRestoreInstanceState( state );
    mListState = state.getParcelable( LIST_STATE );
  }

  @Override
  protected void onSaveInstanceState( Bundle state ) {
    super.onSaveInstanceState( state );
    mListState = mPostsList.onSaveInstanceState();
    state.putParcelable( LIST_STATE, mListState );
  }

  class VoteTaskFinishedHandler implements ITaskFinishedHandler<Boolean> {
    @Override
    public void onTaskFinished( int taskCode, com.manuelmaly.hn.task.ITaskFinishedHandler.TaskResultCode code,
        Boolean result, Object tag ) {
      if (taskCode == TASKCODE_VOTE) {
        if (result != null && result.booleanValue()) {
          Toast.makeText( MainActivity.this, R.string.vote_success, Toast.LENGTH_SHORT ).show();
          HNPost post = (HNPost) tag;
          if (post != null)
            mUpvotedPosts.add( post );
        } else
          Toast.makeText( MainActivity.this, R.string.vote_error, Toast.LENGTH_LONG ).show();
      }
    }
  }

  @Override
  public boolean onPrepareOptionsMenu( Menu menu ) {
    moreClicked();
    return false;
  }

  class PostsAdapter extends BaseAdapter {

    private static final int VIEWTYPE_POST = 0;
    private static final int VIEWTYPE_LOADMORE = 1;
    private static final int VIEWTYPE_SEARCH = 2;

    @Override
    public int getCount() {
      int posts = mFeed.getPosts().size();
      if (posts == 0)
        return 1; // for the search cell...
      else
        // + 2 for the hidden search cell and the load more cell.
        return posts + 2;
    }

    @Override
    public HNPost getItem( int position ) {
      if (getItemViewType( position ) == VIEWTYPE_POST)
        return mFeed.getPosts().get( position - 1 ); // because of the search
                                                     // field
      else
        return null;
    }

    @Override
    public long getItemId( int position ) {
      // Item ID not needed here:
      return 0;
    }

    @Override
    public int getItemViewType( int position ) {
      if (position == 0) {
        return VIEWTYPE_SEARCH;
      } else if (position > 0 && position <= mFeed.getPosts().size()) {
        return VIEWTYPE_POST;
      } else {
        return VIEWTYPE_LOADMORE;
      }
    }

    @Override
    public int getViewTypeCount() {
      return 3;
    }

    @Override
    public View getView( final int position, View convertView, ViewGroup parent ) {
      switch (getItemViewType( position )) {
      case VIEWTYPE_POST:
        if (convertView == null) {
          convertView = (LinearLayout) mInflater.inflate( R.layout.main_list_item, null );
          PostViewHolder holder = new PostViewHolder();
          holder.titleView = (TextView) convertView.findViewById( R.id.main_list_item_title );
          holder.urlView = (TextView) convertView.findViewById( R.id.main_list_item_url );
          holder.textContainer = (LinearLayout) convertView.findViewById( R.id.main_list_item_textcontainer );
          holder.commentsButton = (Button) convertView.findViewById( R.id.main_list_item_comments_button );
          holder.commentsButton.setTypeface( FontHelper.getComfortaa( MainActivity.this, false ) );
          holder.pointsView = (TextView) convertView.findViewById( R.id.main_list_item_points );
          holder.pointsView.setTypeface( FontHelper.getComfortaa( MainActivity.this, true ) );
          convertView.setTag( holder );
        }

        final HNPost item = getItem( position );
        PostViewHolder holder = (PostViewHolder) convertView.getTag();
        holder.titleView.setTextSize( TypedValue.COMPLEX_UNIT_DIP, mFontSizeTitle );
        holder.titleView.setText( item.getTitle() );
        holder.titleView.setTextColor( isRead( item ) ? mTitleReadColor : mTitleColor );
        holder.urlView.setTextSize( TypedValue.COMPLEX_UNIT_DIP, mFontSizeDetails );
        holder.urlView.setText( item.getURLDomain() );
        holder.pointsView.setTextSize( TypedValue.COMPLEX_UNIT_DIP, mFontSizeDetails );
        if (item.getPoints() != BaseHTMLParser.UNDEFINED)
          holder.pointsView.setText( item.getPoints() + "" );
        else
          holder.pointsView.setText( "-" );

        holder.commentsButton.setTextSize( TypedValue.COMPLEX_UNIT_DIP, mFontSizeTitle );
        if (item.getCommentsCount() != BaseHTMLParser.UNDEFINED) {
          holder.commentsButton.setVisibility( View.VISIBLE );
          holder.commentsButton.setText( item.getCommentsCount() + "" );
        } else
          holder.commentsButton.setVisibility( View.INVISIBLE );
        holder.commentsButton.setOnClickListener( new OnClickListener() {
          public void onClick( View v ) {
            Intent i = new Intent( MainActivity.this, CommentsActivity_.class );
            i.putExtra( CommentsActivity.EXTRA_HNPOST, getItem( position ) );
            startActivity( i );
          }
        } );
        holder.textContainer.setOnClickListener( new OnClickListener() {
          public void onClick( View v ) {
            markAsRead( item );

            if (Settings.getHtmlViewer( MainActivity.this ).equals( getString( R.string.pref_htmlviewer_browser ) ))
              openURLInBrowser( getArticleViewURL( getItem( position ) ), MainActivity.this );
            else
              openPostInApp( getItem( position ), null, MainActivity.this );
          }
        } );
        holder.textContainer.setOnLongClickListener( new OnLongClickListener() {
          public boolean onLongClick( View v ) {
            final HNPost post = getItem( position );

            AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
            LongPressMenuListAdapter adapter = new LongPressMenuListAdapter( post );
            builder.setAdapter( adapter, adapter ).show();
            return true;
          }
        } );
        break;

      case VIEWTYPE_LOADMORE:
        // I don't use the preloaded convertView here because it's
        // only one cell
        convertView = (FrameLayout) mInflater.inflate( R.layout.main_list_item_loadmore, null );
        final TextView textView = (TextView) convertView.findViewById( R.id.main_list_item_loadmore_text );
        textView.setTypeface( FontHelper.getComfortaa( MainActivity.this, true ) );
        final ImageView imageView = (ImageView) convertView.findViewById( R.id.main_list_item_loadmore_loadingimage );
        if (HNFeedTaskLoadMore.isRunning( MainActivity.this, TASKCODE_LOAD_MORE_POSTS )) {
          textView.setVisibility( View.INVISIBLE );
          imageView.setVisibility( View.VISIBLE );
          convertView.setClickable( false );
        }

        final View convertViewFinal = convertView;
        convertView.setOnClickListener( new OnClickListener() {
          public void onClick( View v ) {
            textView.setVisibility( View.INVISIBLE );
            imageView.setVisibility( View.VISIBLE );
            convertViewFinal.setClickable( false );
            HNFeedTaskLoadMore.start( MainActivity.this, MainActivity.this, mFeed, TASKCODE_LOAD_MORE_POSTS );
          }
        } );
        break;
      case VIEWTYPE_SEARCH:
        if (convertView == null) {
          convertView = (FrameLayout) mInflater.inflate( R.layout.main_list_item_search, null );
          mSearchField = (EditText) convertView.findViewById( R.id.search_edt_txt );
          mSearchField.setOnEditorActionListener( new OnEditorActionListener() {

            @Override
            public boolean onEditorAction( TextView v, int actionId, KeyEvent event ) {
              if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = mSearchField.getText().toString();
                if (query.length() > 0)
                  HNSearchTask.startOrReattach( query, MainActivity.this, MainActivity.this, TASKCODE_SEARCH_RESULTS );
                else
                  adjustScrollTopPositionIfNecessary( true );
              }
              return false;
            }
          } );

          // necessary, because the textwatcher has a bug and sometimes
          // reports a change with an empty string on backspace, even
          // if the change is not resulting in an empty string.
          // http://stackoverflow.com/questions/14766422/textwatcher-aftertextchanged-has-incorrect-string-after-backspace
          final Handler handler = new Handler();
          final Runnable r = new Runnable() {
            @Override
            public void run() {
              if (mSearchField.getText().toString().matches( "" )) {
                clearSearchResults();
              }
            }
          };
          mSearchField.addTextChangedListener( new TextWatcher() {

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count ) {
              handler.postDelayed( r, 100 );
            }

            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after ) { }

            @Override
            public void afterTextChanged( Editable s ) { }
          } );
        }

        break;
      default:
        break;
      }

      return convertView;
    }

    private boolean isRead( HNPost post ) {
      return mAlreadyRead.contains( post.getTitle().hashCode() );
    }
  }

  private class LongPressMenuListAdapter implements ListAdapter, DialogInterface.OnClickListener {

    HNPost mPost;
    boolean mIsLoggedIn;
    boolean mUpVotingEnabled;
    ArrayList<CharSequence> mItems;

    public LongPressMenuListAdapter( HNPost post ) {
      mPost = post;
      mIsLoggedIn = Settings.isUserLoggedIn( MainActivity.this );
      mUpVotingEnabled = !mIsLoggedIn
          || (mPost.getUpvoteURL( Settings.getUserName( MainActivity.this ) ) != null && !mUpvotedPosts
              .contains( mPost ));

      mItems = new ArrayList<CharSequence>();
      if (mUpVotingEnabled)
        mItems.add( getString( R.string.upvote ) );
      else
        mItems.add( getString( R.string.already_upvoted ) );
      mItems.addAll( Arrays.asList( getString( R.string.pref_htmlprovider_original_url ),
          getString( R.string.pref_htmlprovider_viewtext ), getString( R.string.pref_htmlprovider_google ),
          getString( R.string.pref_htmlprovider_instapaper ), getString( R.string.external_browser ) ) );
    }

    @Override
    public int getCount() {
      return mItems.size();
    }

    @Override
    public CharSequence getItem( int position ) {
      return mItems.get( position );
    }

    @Override
    public long getItemId( int position ) {
      return 0;
    }

    @Override
    public int getItemViewType( int position ) {
      return 0;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
      TextView view = (TextView) mInflater.inflate( android.R.layout.simple_list_item_1, null );
      view.setText( getItem( position ) );
      if (!mUpVotingEnabled && position == 0)
        view.setTextColor( getResources().getColor( android.R.color.darker_gray ) );
      return view;
    }

    @Override
    public int getViewTypeCount() {
      return 1;
    }

    @Override
    public boolean hasStableIds() {
      return false;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public void registerDataSetObserver( DataSetObserver observer ) {
    }

    @Override
    public void unregisterDataSetObserver( DataSetObserver observer ) {
    }

    @Override
    public boolean areAllItemsEnabled() {
      return false;
    }

    @Override
    public boolean isEnabled( int position ) {
      if (!mUpVotingEnabled && position == 4)
        return false;
      return true;
    }

    @Override
    public void onClick( DialogInterface dialog, int item ) {
      switch (item) {
      case 0:
        if (!mIsLoggedIn)
          Toast.makeText( MainActivity.this, R.string.please_log_in, Toast.LENGTH_LONG ).show();
        else if (mUpVotingEnabled)
          vote( mPost.getUpvoteURL( Settings.getUserName( MainActivity.this ) ), mPost );
        break;
      case 1:
      case 2:
      case 3:
      case 4:
        openPostInApp( mPost, getItem( item ).toString(), MainActivity.this );
        break;
      case 5:
        openURLInBrowser( getArticleViewURL( mPost ), MainActivity.this );
        break;
      default:
        break;
      }
    }

  }

  private String getArticleViewURL( HNPost post ) {
    return ArticleReaderActivity.getArticleViewURL( post, Settings.getHtmlProvider( this ), this );
  }

  public static void openURLInBrowser( String url, Activity a ) {
    Intent browserIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
    a.startActivity( browserIntent );
  }

  public static void openPostInApp( HNPost post, String overrideHtmlProvider, Activity a ) {
    Intent i = new Intent( a, ArticleReaderActivity_.class );
    i.putExtra( ArticleReaderActivity.EXTRA_HNPOST, post );
    if (overrideHtmlProvider != null)
      i.putExtra( ArticleReaderActivity.EXTRA_HTMLPROVIDER_OVERRIDE, overrideHtmlProvider );
    a.startActivity( i );
  }

  static class PostViewHolder {
    TextView titleView;
    TextView urlView;
    TextView pointsView;
    TextView commentsCountView;
    LinearLayout textContainer;
    Button commentsButton;
  }

}
