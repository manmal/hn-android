package com.manuelmaly.hnreader;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.ViewById;
import com.manuelmaly.hnreader.model.HNFeed;
import com.manuelmaly.hnreader.model.HNPost;
import com.manuelmaly.hnreader.reuse.ImageViewFader;
import com.manuelmaly.hnreader.reuse.ViewRotator;
import com.manuelmaly.hnreader.server.IAPICommand;
import com.manuelmaly.hnreader.task.HNFeedTask;
import com.manuelmaly.hnreader.task.ITaskFinishedHandler;
import com.manuelmaly.hnreader.util.FileUtil;
import com.manuelmaly.hnreader.util.FontHelper;
import com.manuelmaly.hnreader.util.Run;

@EActivity(R.layout.main)
public class HNReaderActivity extends Activity implements ITaskFinishedHandler<HNFeed> {

    @SystemService
    LayoutInflater mInflater;

    @ViewById(R.id.main_list)
    ListView mPostsList;

    @ViewById(R.id.main_banner)
    FrameLayout mBannerLayout;

    @ViewById(R.id.main_banner_title)
    TextView mBannerTitleView;

    @ViewById(R.id.main_refresh)
    ImageView mRefreshImageView;

    HNFeed mFeed;
    PostsAdapter mPostsListAdapter;

    Animation mRefreshButtonInAnimation;
    Animation mRefreshButtonOutAnimation;

    @AfterViews
    public void init() {
        mFeed = new HNFeed(new ArrayList<HNPost>());
        mPostsListAdapter = new PostsAdapter();
        mPostsList.setAdapter(mPostsListAdapter);

        mRefreshButtonInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        mRefreshButtonOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        mRefreshImageView = (ImageView) findViewById(R.id.main_refresh);
        mRefreshImageView.setImageDrawable(getResources().getDrawable(R.drawable.refresh));

        mBannerTitleView.setTypeface(FontHelper.getComfortaa(this, true));

        mBannerLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mPostsList.smoothScrollToPosition(0);
            }
        });

        mRefreshImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (HNFeedTask.isRunning(getApplicationContext()))
                    HNFeedTask.stopCurrent(getApplicationContext());
                else
                    startFeedLoading();
            }
        });

        loadIntermediateFeedFromStore();
        startFeedLoading();
    }

    @Override
    public void onTaskFinished(TaskResultCode code, HNFeed result) {
        if (code.equals(TaskResultCode.Success) && mPostsListAdapter != null)
            showFeed(result);
        updateStatusIndicatorOnLoadingFinished(code);
    }

    private void showFeed(HNFeed feed) {
        mFeed = feed;
        mPostsListAdapter.notifyDataSetChanged();
    }

    private void loadIntermediateFeedFromStore() {
        long start = System.currentTimeMillis();
        HNFeed feedFromStore = FileUtil.getLastHNFeed();
        if (feedFromStore == null) {
            // TODO: display "Loading..." instead
        } else
            showFeed(feedFromStore);
        Log.i("", "Loading intermediate feed took ms:" + (System.currentTimeMillis() - start));
    }

    private void updateStatusIndicatorOnLoadingStarted() {
        mRefreshImageView.setImageResource(R.drawable.refresh);
        ViewRotator.stopRotating(mRefreshImageView);
        ViewRotator.startRotating(mRefreshImageView);
    }

    private void updateStatusIndicatorOnLoadingFinished(TaskResultCode code) {
        ViewRotator.stopRotating(mRefreshImageView);
        if (code.equals(TaskResultCode.Success)) {
            ImageViewFader.startFadeOverToImage(mRefreshImageView, R.drawable.refresh_ok, 100, this);
            Run.delayed(new Runnable() {
                public void run() {
                    ImageViewFader.startFadeOverToImage(mRefreshImageView, R.drawable.refresh, 300,
                        HNReaderActivity.this);
                }
            }, 2000);
        }

    }

    private void startFeedLoading() {
        HNFeedTask.startOrReattach(this, this);
        updateStatusIndicatorOnLoadingStarted();
    }

    class PostsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFeed.getPosts().size();
        }

        @Override
        public HNPost getItem(int position) {
            return mFeed.getPosts().get(position);
        }

        @Override
        public long getItemId(int position) {
            // Item ID not needed here:
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = (LinearLayout) mInflater.inflate(R.layout.main_list_item, null);
                PostViewHolder holder = new PostViewHolder();
                holder.titleView = (TextView) convertView.findViewById(R.id.main_list_item_title);
                holder.urlView = (TextView) convertView.findViewById(R.id.main_list_item_url);
                holder.textContainer = (LinearLayout) convertView.findViewById(R.id.main_list_item_textcontainer);
                holder.commentsButton = (Button) convertView.findViewById(R.id.main_list_item_comments_button);
                holder.commentsButton.setTypeface(FontHelper.getComfortaa(HNReaderActivity.this, false));
                holder.pointsView = (TextView) convertView.findViewById(R.id.main_list_item_points);
                holder.pointsView.setTypeface(FontHelper.getComfortaa(HNReaderActivity.this, true));
                convertView.setTag(holder);
            }
            HNPost item = getItem(position);
            PostViewHolder holder = (PostViewHolder) convertView.getTag();
            holder.titleView.setText(item.getTitle());
            holder.urlView.setText(item.getURLDomain());
            holder.pointsView.setText(item.getPoints() + "");
            holder.commentsButton.setText(item.getCommentsCount() + "");
            holder.commentsButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(HNReaderActivity.this, CommentsActivity_.class);
                    i.putExtra(CommentsActivity.EXTRA_HNPOST, getItem(position));
                    startActivity(i);
                }
            });
            holder.textContainer.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(HNReaderActivity.this, ArticleReaderActivity_.class);
                    i.putExtra(ArticleReaderActivity.EXTRA_HNPOST, getItem(position));
                    startActivity(i);
                }
            });
            return convertView;
        }

    }

    static class PostViewHolder {
        TextView titleView;
        TextView urlView;
        TextView pointsView;
        TextView commentsCountView;
        LinearLayout textContainer;
        Button commentsButton;
    }

    class RefreshButtonViewFactory implements ViewFactory {

        @Override
        public View makeView() {
            ImageView iView = new ImageView(HNReaderActivity.this);
            iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            iView.setBackgroundColor(Color.TRANSPARENT);
            return iView;
        }

    }

}