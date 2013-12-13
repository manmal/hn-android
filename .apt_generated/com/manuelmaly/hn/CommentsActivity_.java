//
// DO NOT EDIT THIS FILE, IT HAS BEEN GENERATED USING AndroidAnnotations.
//


package com.manuelmaly.hn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.googlecode.androidannotations.api.SdkVersionHelper;
import com.manuelmaly.hn.R.id;
import com.manuelmaly.hn.R.layout;

public final class CommentsActivity_
    extends CommentsActivity
{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(layout.comments_activity);
    }

    private void init_(Bundle savedInstanceState) {
        mInflater = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    }

    private void afterSetContentView_() {
        mActionbarTitle = ((Button) findViewById(id.actionbar_title_button));
        mActionbarContainer = ((FrameLayout) findViewById(id.actionbar));
        mActionbarBack = ((ImageView) findViewById(id.actionbar_back));
        mActionbarRefresh = ((ImageView) findViewById(id.actionbar_refresh));
        mActionbarRefreshProgress = ((ProgressBar) findViewById(id.actionbar_refresh_progress));
        mRootView = ((LinearLayout) findViewById(id.comments_root));
        mActionbarShare = ((ImageView) findViewById(id.actionbar_share));
        mCommentsList = ((ListView) findViewById(id.comments_list));
        mActionbarRefreshContainer = ((LinearLayout) findViewById(id.actionbar_refresh_container));
        init();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        afterSetContentView_();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        afterSetContentView_();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        afterSetContentView_();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (((SdkVersionHelper.getSdkInt()< 5)&&(keyCode == KeyEvent.KEYCODE_BACK))&&(event.getRepeatCount() == 0)) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    public static CommentsActivity_.IntentBuilder_ intent(Context context) {
        return new CommentsActivity_.IntentBuilder_(context);
    }

    public static class IntentBuilder_ {

        private Context context_;
        private final Intent intent_;

        public IntentBuilder_(Context context) {
            context_ = context;
            intent_ = new Intent(context, CommentsActivity_.class);
        }

        public Intent get() {
            return intent_;
        }

        public CommentsActivity_.IntentBuilder_ flags(int flags) {
            intent_.setFlags(flags);
            return this;
        }

        public void start() {
            context_.startActivity(intent_);
        }

    }

}
