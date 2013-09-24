package com.manuelmaly.hn;

import com.manuelmaly.hn.util.FontHelper;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BaseListActivity extends Activity{

    private TextView mLoadingView;

    protected TextView getLoadingPanel(ViewGroup parent) {
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