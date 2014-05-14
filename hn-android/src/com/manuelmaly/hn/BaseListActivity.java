package com.manuelmaly.hn;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.manuelmaly.hn.util.FontHelper;

public class BaseListActivity extends ActionBarActivity {

    private TextView mLoadingView;

    protected TextView getEmptyTextView(ViewGroup parent) {
        if(mLoadingView == null) {
            ViewGroup root = (ViewGroup) getLayoutInflater().
                    inflate(R.layout.panel_loading, parent, true);
            mLoadingView = (TextView) root.findViewById(android.R.id.empty);
            mLoadingView.setVisibility(View.GONE);
            mLoadingView.setTypeface(FontHelper.getComfortaa(this, true));
        }
        return mLoadingView;
    }
}