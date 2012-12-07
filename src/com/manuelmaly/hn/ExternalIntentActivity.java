package com.manuelmaly.hn;

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
            // TODO: Handle id parameter and SOMEHOW retrieve the article url
            // - e.g. by parsing it with the comments and feed it back into the HNPost object
            // after that. But then, user cannot switch to the article before comments have loaded.
            //String postId = uri.getQueryParameter("id");
            i = new Intent(this, MainActivity_.class);
        }

        if (i != null)
            startActivity(i);
        else
            Toast.makeText(this, "This seems not to be a valid Hacker News item!", Toast.LENGTH_LONG).show();

        finish();
    }
}
