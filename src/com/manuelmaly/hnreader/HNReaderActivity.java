package com.manuelmaly.hnreader;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.ViewById;
import com.manuelmaly.hnreader.server.HTMLDownloadCommand;
import com.manuelmaly.hnreader.server.IAPICommand;
import com.manuelmaly.hnreader.server.IAPICommand.RequestType;

@EActivity(R.layout.main)
public class HNReaderActivity extends Activity {

    @SystemService
    LayoutInflater mInflater;

    @ViewById(R.id.main_list)
    ListView mPostsList;

    List<String> mPosts = new ArrayList<String>();
    PostsAdapter mPostsListAdapter;

    @AfterViews
    public void init() {
        for (int i = 0; i < 10; i++)
            mPosts.add("Some topic #" + i);

        mPostsListAdapter = new PostsAdapter();
        mPostsList.setAdapter(mPostsListAdapter);

        String downloadFinishedIntentID = "feed";
        HTMLDownloadCommand htmlDownload = new HTMLDownloadCommand("http://news.ycombinator.com/", "", RequestType.GET,
            downloadFinishedIntentID, getApplicationContext());

        BroadcastReceiver downloadFinishedListener = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getIntExtra(IAPICommand.BROADCAST_INTENT_EXTRA_ERROR, IAPICommand.ERROR_NONE) == IAPICommand.ERROR_NONE) {
                    long start = System.currentTimeMillis();
                    String response = intent.getStringExtra(IAPICommand.BROADCAST_INTENT_EXTRA_RESPONSE);

                    Tidy tidy = new Tidy();
                    tidy.setOnlyErrors(true);
                    tidy.setXmlOut(true);
                    Document doc = tidy.parseDOM(new ByteArrayInputStream(response.getBytes()), System.out);
                    
                    long tidyEnd = System.currentTimeMillis();
                    Log.i("HNReaderActivity", "JTidy took: " + (tidyEnd - start) + "ms");

                    XPath xpath = XPathFactory.newInstance().newXPath();
                    String expression = "//table//tr[3]//table//tr//td[3]//a/text()";
                    try {
                        NodeList nodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
                        
                        long xpathEnd = System.currentTimeMillis();
                        Log.i("HNReaderActivity", "XPATH took: " + (xpathEnd - tidyEnd) + "ms");
                        
                        mPosts = new ArrayList<String>();
                        for (int i = 0; i < nodes.getLength(); i++)
                            mPosts.add(nodes.item(i).getNodeValue());
                        mPostsListAdapter.notifyDataSetChanged();
                    } catch (XPathExpressionException e) {
                        e.printStackTrace();
                    }
                    Log.i("HNReaderActivity", "Parsing took: " + (System.currentTimeMillis() - start) + "ms");
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(downloadFinishedListener,
            new IntentFilter(downloadFinishedIntentID));

        htmlDownload.run();
    }

    class PostsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPosts.size();
        }

        @Override
        public String getItem(int position) {
            return mPosts.get(position);
        }

        @Override
        public long getItemId(int position) {
            // Item ID not needed here:
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cellRoot = mInflater.inflate(R.layout.main_list_item, null);
            TextView title = (TextView) cellRoot.findViewById(R.id.main_list_item_title);
            title.setText(getItem(position));
            return cellRoot;
        }

    }
}