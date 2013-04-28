package com.manuelmaly.hn;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import com.manuelmaly.hn.model.HNPost;

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
            HNPost post = new HNPost(null, null, null, null, postId, -1, -1, null);
            i = new Intent(this, CommentsActivity_.class);
            i.putExtra(CommentsActivity_.EXTRA_HNPOST, post);
        }

        if (i != null)
            startActivity(i);
        else
            Toast.makeText(this, "This seems not to be a valid Hacker News item!", Toast.LENGTH_LONG).show();

        finish();
    }
}
