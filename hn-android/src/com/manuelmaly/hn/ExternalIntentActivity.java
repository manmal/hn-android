package com.manuelmaly.hn;

import com.manuelmaly.hn.model.HNPost;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class ExternalIntentActivity extends Activity {

  private Uri getURIFromIntent( Intent intent ) {

    // Only handle the two expected intents
    if( intent.getAction().equals(Intent.ACTION_SEND)) {
      return Uri.parse( intent.getStringExtra(Intent.EXTRA_TEXT) );
    } else if( intent.getAction().equals(Intent.ACTION_VIEW)) {
      return getIntent().getData();
    }
    return null; //otherwise null, to be handled later.
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Handle both intents, get just the URI
    Uri uri = getURIFromIntent( getIntent() );

    // Intent to launch next activity.
    Intent i = null;

    // Decide what to do based on the URI
    if( uri != null ) {
      String uriString = uri.toString().replaceAll("/$", "");

      if (uriString.endsWith("news.ycombinator.com")) { // Front page
        i = new Intent(this, MainActivity_.class);
      } else if (uriString.contains("item")) { // Comment
        String postId = uri.getQueryParameter("id");
        HNPost postToOpen = new HNPost( postId );
        i = new Intent(this, CommentsActivity_.class);
        i.putExtra(CommentsActivity.EXTRA_HNPOST, postToOpen);
      }
    }

    if (i != null) {

      startActivity(i);

    } else {
      Toast.makeText(this, "This seems not to be a valid Hacker News item!", Toast.LENGTH_LONG).show();
    }

    finish();
  }
}
