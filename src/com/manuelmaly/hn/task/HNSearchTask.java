package com.manuelmaly.hn.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.manuelmaly.hn.App;
import com.manuelmaly.hn.Settings;
import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.model.HNPost;
import com.manuelmaly.hn.parser.BaseHTMLParser;
import com.manuelmaly.hn.reuse.CancelableRunnable;
import com.manuelmaly.hn.server.IAPICommand;
import com.manuelmaly.hn.server.IAPICommand.RequestType;
import com.manuelmaly.hn.server.StringDownloadCommand;

public class HNSearchTask extends BaseTask<HNFeed> {

  public static final String BROADCAST_INTENT_ID = "HNSearchTask";

  private static final String QUERY_PARAM = "query";
  private static final String PAGE_PARAM = "page";
  private static final String HITS_PER_PAGE_PARAM = "hitsPerPage";
  private static final String TYPE_PARAM = "tagFilters";

  private static final String DEFAULT_HITS_PER_PAGE = "30";

  private static Set<HNSearchTask> mInstances = new HashSet<HNSearchTask>();
  private String mQuery;
  private int mPage;

  public HNSearchTask( int taskCode ) {
    super( BROADCAST_INTENT_ID, taskCode );
  }

  public void setQuery( String query ) {
    mQuery = query;
  }

  public void setPage( int page ) {
    mPage = page;
  }

  public int getPage() {
    return mPage;
  }

  @Override
  public CancelableRunnable getTask() {
    return new CancelableRunnable() {
      StringDownloadCommand mSearchCommand;

      @Override
      public void run() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put( QUERY_PARAM, mQuery );
        parameters.put( HITS_PER_PAGE_PARAM, DEFAULT_HITS_PER_PAGE );
        parameters.put( PAGE_PARAM, getPage() + "" );
        parameters.put( TYPE_PARAM, "story" );
        mSearchCommand = new StringDownloadCommand( "http://hn.algolia.io/api/v1/search", parameters, RequestType.GET,
            false, null, App.getInstance(), null );

        mSearchCommand.run();

        if (mCancelled)
          mErrorCode = IAPICommand.ERROR_CANCELLED_BY_USER;
        else
          mErrorCode = mSearchCommand.getErrorCode();

        if (!mCancelled && mErrorCode == IAPICommand.ERROR_NONE) {
          ArrayList<HNPost> postResults = new ArrayList<HNPost>();
          try {
            JSONObject response = new JSONObject( mSearchCommand.getResponseContent() );
            int pages = response.getInt( "nbPages" );
            int page = response.getInt( "page" );

            if (page == pages) {
              mErrorCode = IAPICommand.ERROR_SERVER_RETURNED_ERROR;
            }

            JSONArray hits = response.getJSONArray( "hits" );
            for (int i = 0; i < hits.length(); i++) {
              JSONObject postResult = hits.getJSONObject( i );
              String url = postResult.getString( "url" );
              String title = postResult.getString( "title" );
              String urlDomain = BaseHTMLParser.getDomainName( url );
              String author = postResult.getString( "author" );
              String postID = postResult.getString( "objectID" );
              int commentsCount = postResult.getInt( "num_comments" );
              int points = postResult.getInt( "points" );
              HNPost post = new HNPost( url, title, urlDomain, author, postID, commentsCount, points, null );
              postResults.add( post );
            }
          } catch (JSONException e) {
            mErrorCode = IAPICommand.ERROR_RESPONSE_PARSE_ERROR;

          } catch (Exception e) {
            mErrorCode = IAPICommand.ERROR_UNKNOWN;
          }

          mResult = new HNFeed( postResults, "", Settings.getUserName( App.getInstance() ) );

        }

      }

      @Override
      public void onCancelled() {
        if (mSearchCommand != null) {
          mSearchCommand.cancel();
        }
      }
    };
  }

  public static void startOrReattach( String query, boolean startPage, Activity activity,
      ITaskFinishedHandler<HNFeed> finishedHandler, int taskCode ) {
    HNSearchTask task = getInstance( taskCode );
    task.setQuery( query );
    task.setOnFinishedHandler( activity, finishedHandler, HNFeed.class );
    task.setPage( startPage ? 0 : task.getPage() + 1 );
    if (!task.isRunning())
      task.startInBackground();
  }

  private static HNSearchTask getInstance( int taskCode ) {
    HNSearchTask instance = null;
    synchronized (HNSearchTask.class) {
      for (HNSearchTask task : mInstances) {
        if (task.mTaskCode == taskCode) {
          instance = task;
        }
      }

      if (instance == null) {
        instance = new HNSearchTask( taskCode );
        mInstances.add( instance );
      }
    }
    return instance;
  }

}
