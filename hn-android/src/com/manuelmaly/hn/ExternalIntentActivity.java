package com.manuelmaly.hn;

import com.manuelmaly.hn.model.HNPost;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class ExternalIntentActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Uri uri = getIntent().getData();
    String uriString = uri.toString().replaceAll("/$", "");

    Intent i = null;
    if (uriString.endsWith("news.ycombinator.com")) { // Front page
      i = new Intent(this, MainActivity_.class);
    } else if (uriString.contains("item")) { // Comment
      String postId = uri.getQueryParameter("id");
      HNPost postToOpen = new HNPost(uriString, null, null, null, postId, 0, 0, null);
      i = new Intent(this, CommentsActivity_.class);
      i.putExtra(CommentsActivity.EXTRA_HNPOST, postToOpen);
    }

    if (i != null) {

      startActivity(i);

    } else {
      Toast.makeText(this, "This seems not to be a valid Hacker News item!", Toast.LENGTH_LONG).show();
    }

    finish();
  }
}
