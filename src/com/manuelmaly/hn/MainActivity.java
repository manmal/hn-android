package com.manuelmaly.hn;

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
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.ViewById;
import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.model.HNPost;
import com.manuelmaly.hn.parser.BaseHTMLParser;
import com.manuelmaly.hn.reuse.ImageViewFader;
import com.manuelmaly.hn.reuse.ViewRotator;
import com.manuelmaly.hn.server.IAPICommand;
import com.manuelmaly.hn.task.HNFeedTaskBase;
import com.manuelmaly.hn.task.HNFeedTaskLoadMore;
import com.manuelmaly.hn.task.HNFeedTaskMainFeed;
import com.manuelmaly.hn.task.ITaskFinishedHandler;
import com.manuelmaly.hn.util.FileUtil;
import com.manuelmaly.hn.util.FontHelper;
import com.manuelmaly.hn.util.Run;
import com.manuelmaly.hn.R;

@EActivity(R.layout.main)
public class MainActivity extends Activity implements ITaskFinishedHandler<HNFeed> {

    @SystemService
    LayoutInflater mInflater;

    @ViewById(R.id.main_list)
    ListView mPostsList;

    @ViewById(R.id.actionbar)
    FrameLayout mActionbarLayout;

    @ViewById(R.id.actionbar_title)
    TextView mActionbarTitleView;

    @ViewById(R.id.actionbar_refresh)
    ImageView mRefreshImageView;

    HNFeed mFeed;
    PostsAdapter mPostsListAdapter;
    
    private static final int TASKCODE_LOAD_FEED = 10;
    private static final int TASKCODE_LOAD_MORE_POSTS = 20;

    @AfterViews
    public void init() {
        mFeed = new HNFeed(new ArrayList<HNPost>(), null);
        mPostsListAdapter = new PostsAdapter();
        mPostsList.setAdapter(mPostsListAdapter);

        mRefreshImageView.setImageDrawable(getResources().getDrawable(R.drawable.refresh));

        mActionbarTitleView.setTypeface(FontHelper.getComfortaa(this, true));

        mActionbarLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mPostsList.smoothScrollToPosition(0);
            }
        });

        mRefreshImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (HNFeedTaskMainFeed.isRunning(getApplicationContext()))
                    HNFeedTaskMainFeed.stopCurrent(getApplicationContext());
                else
                    startFeedLoading();
            }
        });

        loadIntermediateFeedFromStore();
        startFeedLoading();
    }

    @Override
    public void onTaskFinished(int taskCode, TaskResultCode code, HNFeed result) {
        if (taskCode == TASKCODE_LOAD_FEED) {
            if (code.equals(TaskResultCode.Success) && mPostsListAdapter != null)
                showFeed(result);
            updateStatusIndicatorOnLoadingFinished(code);
        } else if (taskCode == TASKCODE_LOAD_MORE_POSTS) {
            mFeed.appendLoadMoreFeed(result);
            mPostsListAdapter.notifyDataSetChanged();
        }
        
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
                    ImageViewFader.startFadeOverToImage(mRefreshImageView, R.drawable.refresh, 300, MainActivity.this);
                }
            }, 2000);
        }

    }

    private void startFeedLoading() {
        HNFeedTaskMainFeed.startOrReattach(this, this, TASKCODE_LOAD_FEED);
        updateStatusIndicatorOnLoadingStarted();
    }
    
    private void startMorePostsLoading() {
        HNFeedTaskLoadMore.start(this, this, mFeed, TASKCODE_LOAD_MORE_POSTS);
    }

    class PostsAdapter extends BaseAdapter {

        private static final int VIEWTYPE_POST = 0;
        private static final int VIEWTYPE_LOADMORE = 1;

        @Override
        public int getCount() {
            return mFeed.getPosts().size() + 1; // +1 for the "Load more" cell
                                                // at the
                                                // bottom
        }

        @Override
        public HNPost getItem(int position) {
            if (getItemViewType(position) == VIEWTYPE_POST)
                return mFeed.getPosts().get(position);
            else
                return null;
        }

        @Override
        public long getItemId(int position) {
            // Item ID not needed here:
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < mFeed.getPosts().size())
                return VIEWTYPE_POST;
            else
                return VIEWTYPE_LOADMORE;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            switch (getItemViewType(position)) {
                case VIEWTYPE_POST:
                    if (convertView == null) {
                        convertView = (LinearLayout) mInflater.inflate(R.layout.main_list_item, null);
                        PostViewHolder holder = new PostViewHolder();
                        holder.titleView = (TextView) convertView.findViewById(R.id.main_list_item_title);
                        holder.urlView = (TextView) convertView.findViewById(R.id.main_list_item_url);
                        holder.textContainer = (LinearLayout) convertView
                            .findViewById(R.id.main_list_item_textcontainer);
                        holder.commentsButton = (Button) convertView.findViewById(R.id.main_list_item_comments_button);
                        holder.commentsButton.setTypeface(FontHelper.getComfortaa(MainActivity.this, false));
                        holder.pointsView = (TextView) convertView.findViewById(R.id.main_list_item_points);
                        holder.pointsView.setTypeface(FontHelper.getComfortaa(MainActivity.this, true));
                        convertView.setTag(holder);
                    }

                    HNPost item = getItem(position);
                    PostViewHolder holder = (PostViewHolder) convertView.getTag();
                    holder.titleView.setText(item.getTitle());
                    holder.urlView.setText(item.getURLDomain());
                    if (item.getPoints() != BaseHTMLParser.UNDEFINED)
                        holder.pointsView.setText(item.getPoints() + "");
                    else
                        holder.pointsView.setText("-");
                    if (item.getCommentsCount() != BaseHTMLParser.UNDEFINED) {
                        holder.commentsButton.setVisibility(View.VISIBLE);
                        holder.commentsButton.setText(item.getCommentsCount() + "");
                    } else
                        holder.commentsButton.setVisibility(View.INVISIBLE);
                    holder.commentsButton.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Intent i = new Intent(MainActivity.this, CommentsActivity_.class);
                            i.putExtra(CommentsActivity.EXTRA_HNPOST, getItem(position));
                            startActivity(i);
                        }
                    });
                    holder.textContainer.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Intent i = new Intent(MainActivity.this, ArticleReaderActivity_.class);
                            i.putExtra(ArticleReaderActivity.EXTRA_HNPOST, getItem(position));
                            startActivity(i);
                        }
                    });
                    break;

                case VIEWTYPE_LOADMORE:
                    // I don't use the preloaded convertView here because it's only one cell
                    convertView = (FrameLayout) mInflater.inflate(R.layout.main_list_item_loadmore, null);
                    final TextView textView = (TextView) convertView.findViewById(R.id.main_list_item_loadmore_text);
                    textView.setTypeface(FontHelper.getComfortaa(MainActivity.this, true));
                    final ImageView imageView = (ImageView) convertView.findViewById(R.id.main_list_item_loadmore_loadingimage);
                    if (HNFeedTaskLoadMore.isRunning(MainActivity.this, TASKCODE_LOAD_MORE_POSTS)) {
                        textView.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        convertView.setClickable(false);
                    }
                    
                    final View convertViewFinal = convertView;
                    convertView.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            textView.setVisibility(View.INVISIBLE);
                            imageView.setVisibility(View.VISIBLE);
                            convertViewFinal.setClickable(false);
                            startMorePostsLoading();
                        }
                    });
                    break;
                default:
                    break;
            }

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
            ImageView iView = new ImageView(MainActivity.this);
            iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            iView.setBackgroundColor(Color.TRANSPARENT);
            return iView;
        }

    }

}