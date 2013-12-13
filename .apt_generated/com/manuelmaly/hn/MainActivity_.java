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
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.googlecode.androidannotations.api.SdkVersionHelper;
import com.manuelmaly.hn.R.id;
import com.manuelmaly.hn.R.layout;

public final class MainActivity_
    extends MainActivity
{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(layout.main);
    }

    private void init_(Bundle savedInstanceState) {
        mInflater = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    }

    private void afterSetContentView_() {
        mActionbarTitle = ((TextView) findViewById(id.actionbar_title));
        mPostsList = ((ListView) findViewById(id.main_list));
        mActionbarMore = ((ImageView) findViewById(id.actionbar_more));
        mActionbarRefreshProgress = ((ProgressBar) findViewById(id.actionbar_refresh_progress));
        mActionbarRefresh = ((ImageView) findViewById(id.actionbar_refresh));
        mActionbarRefreshContainer = ((LinearLayout) findViewById(id.actionbar_refresh_container));
        mRootView = ((LinearLayout) findViewById(id.main_root));
        {
            View view = findViewById(id.actionbar);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    public void onClick(View view) {
                        actionBarClicked();
                    }

                }
                );
            }
        }
        {
            View view = findViewById(id.actionbar_more);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    public void onClick(View view) {
                        moreClicked();
                    }

                }
                );
            }
        }
        {
            View view = findViewById(id.actionbar_refresh_container);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    public void onClick(View view) {
                        refreshClicked();
                    }

                }
                );
            }
        }
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

    public static MainActivity_.IntentBuilder_ intent(Context context) {
        return new MainActivity_.IntentBuilder_(context);
    }

    public static class IntentBuilder_ {

        private Context context_;
        private final Intent intent_;

        public IntentBuilder_(Context context) {
            context_ = context;
            intent_ = new Intent(context, MainActivity_.class);
        }

        public Intent get() {
            return intent_;
        }

        public MainActivity_.IntentBuilder_ flags(int flags) {
            intent_.setFlags(flags);
            return this;
        }

        public void start() {
            context_.startActivity(intent_);
        }

    }

}
