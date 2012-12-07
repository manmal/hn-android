package com.manuelmaly.hn;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.ViewById;
import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.model.HNPost;
import com.manuelmaly.hn.parser.BaseHTMLParser;
import com.manuelmaly.hn.reuse.ImageViewFader;
import com.manuelmaly.hn.reuse.ViewRotator;
import com.manuelmaly.hn.task.HNFeedTaskLoadMore;
import com.manuelmaly.hn.task.HNFeedTaskMainFeed;
import com.manuelmaly.hn.task.ITaskFinishedHandler;
import com.manuelmaly.hn.util.FileUtil;
import com.manuelmaly.hn.util.FontHelper;
import com.manuelmaly.hn.util.Run;

@EActivity(R.layout.main)
public class MainActivity extends Activity implements ITaskFinishedHandler<HNFeed> {

    @ViewById(R.id.main_list)
    ListView mPostsList;

    @ViewById(R.id.main_empty_view)
    TextView mEmptyListPlaceholder;

    @ViewById(R.id.actionbar_title)
    TextView mActionbarTitle;

    @ViewById(R.id.actionbar_refresh)
    ImageView mActionbarRefresh;

    @ViewById(R.id.actionbar_more)
    ImageView mActionbarMore;

    @SystemService
    LayoutInflater mInflater;

    HNFeed mFeed;
    PostsAdapter mPostsListAdapter;

    int mFontSizeTitle;
    int mFontSizeDetails;

    private static final int TASKCODE_LOAD_FEED = 10;
    private static final int TASKCODE_LOAD_MORE_POSTS = 20;

    @AfterViews
    public void init() {
        mFeed = new HNFeed(new ArrayList<HNPost>(), null);
        mPostsListAdapter = new PostsAdapter();
        mPostsList.setAdapter(mPostsListAdapter);
        mPostsList.setEmptyView(mEmptyListPlaceholder);
        mActionbarRefresh.setImageDrawable(getResources().getDrawable(R.drawable.refresh));
        mActionbarTitle.setTypeface(FontHelper.getComfortaa(this, true));
        mEmptyListPlaceholder.setTypeface(FontHelper.getComfortaa(this, true));

        loadIntermediateFeedFromStore();
        startFeedLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // refresh because font size could have changed:
        refreshFontSizes();
        mPostsListAdapter.notifyDataSetChanged();
    }

    @Click(R.id.actionbar)
    void actionBarClicked() {
        mPostsList.smoothScrollToPosition(0);
    }

    @Click(R.id.actionbar_refresh)
    void refreshClicked() {
        if (HNFeedTaskMainFeed.isRunning(getApplicationContext()))
            HNFeedTaskMainFeed.stopCurrent(getApplicationContext());
        else
            startFeedLoading();
    }

    @Click(R.id.actionbar_more)
    void moreClicked() {
        mActionbarMore.setSelected(true);
        LinearLayout moreContentView = (LinearLayout) mInflater.inflate(R.layout.main_more_content, null);

        moreContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_dark_washedout)));
        popupWindow.setContentView(moreContentView);
        popupWindow.showAsDropDown(mActionbarMore);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new OnDismissListener() {
            public void onDismiss() {
                mActionbarMore.setSelected(false);
            }
        });

        Button settingsButton = (Button) moreContentView.findViewById(R.id.main_more_content_settings);
        settingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                popupWindow.dismiss();
            }
        });

        Button aboutButton = (Button) moreContentView.findViewById(R.id.main_more_content_about);
        aboutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutActivity_.class));
                popupWindow.dismiss();
            }
        });

        popupWindow.update(moreContentView.getMeasuredWidth(), moreContentView.getMeasuredHeight());
    }

    @Override
    public void onTaskFinished(int taskCode, TaskResultCode code, HNFeed result) {
        if (taskCode == TASKCODE_LOAD_FEED) {
            if (code.equals(TaskResultCode.Success) && mPostsListAdapter != null)
                showFeed(result);

            ViewRotator.stopRotating(mActionbarRefresh);
            if (code.equals(TaskResultCode.Success)) {
                ImageViewFader.startFadeOverToImage(mActionbarRefresh, R.drawable.refresh_ok, 100, this);
                Run.delayed(new Runnable() {
                    public void run() {
                        ImageViewFader.startFadeOverToImage(mActionbarRefresh, R.drawable.refresh, 300,
                            MainActivity.this);
                    }
                }, 2000);
            }

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

    private void startFeedLoading() {
        HNFeedTaskMainFeed.startOrReattach(this, this, TASKCODE_LOAD_FEED);
        mActionbarRefresh.setImageResource(R.drawable.refresh);
        ViewRotator.stopRotating(mActionbarRefresh);
        ViewRotator.startRotating(mActionbarRefresh);
    }

    private void refreshFontSizes() {
        String fontSize = SettingsActivity.getFontSize(this);
        if (fontSize.equals(getString(R.string.pref_fontsize_small))) {
            mFontSizeTitle = 15;
            mFontSizeDetails = 11;
        } else if (fontSize.equals(getString(R.string.pref_fontsize_normal))) {
            mFontSizeTitle = 18;
            mFontSizeDetails = 12;
        } else {
            mFontSizeTitle = 22;
            mFontSizeDetails = 15;
        }
    }

    class PostsAdapter extends BaseAdapter {

        private static final int VIEWTYPE_POST = 0;
        private static final int VIEWTYPE_LOADMORE = 1;

        @Override
        public int getCount() {
            int posts = mFeed.getPosts().size();
            if (posts == 0)
                return 0;
            else
                return posts + (mFeed.isLoadedMore() ? 0 : 1);
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
                    holder.titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mFontSizeTitle);
                    holder.titleView.setText(item.getTitle());
                    holder.urlView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mFontSizeDetails);
                    holder.urlView.setText(item.getURLDomain());
                    holder.pointsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mFontSizeDetails);
                    if (item.getPoints() != BaseHTMLParser.UNDEFINED)
                        holder.pointsView.setText(item.getPoints() + "");
                    else
                        holder.pointsView.setText("-");

                    holder.commentsButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mFontSizeTitle);
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
                            if (SettingsActivity.getHtmlViewer(MainActivity.this).equals(
                                getString(R.string.pref_htmlviewer_browser)))
                                openURLInBrowser(getArticleViewURL(getItem(position)), MainActivity.this);
                            else
                                openPostInApp(getItem(position), null, MainActivity.this);
                        }
                    });
                    holder.textContainer.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            final CharSequence[] items = {getString(R.string.pref_htmlprovider_original_url),
                                getString(R.string.pref_htmlprovider_viewtext),
                                getString(R.string.pref_htmlprovider_google), "External Browser"};

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle(R.string.open_with);
                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    if (item == 3)
                                        openURLInBrowser(getArticleViewURL(getItem(position)), MainActivity.this);
                                    else
                                        openPostInApp(getItem(position), items[item].toString(), MainActivity.this);
                                }
                            }).show();
                            return true;
                        }
                    });
                    break;

                case VIEWTYPE_LOADMORE:
                    // I don't use the preloaded convertView here because it's
                    // only one cell
                    convertView = (FrameLayout) mInflater.inflate(R.layout.main_list_item_loadmore, null);
                    final TextView textView = (TextView) convertView.findViewById(R.id.main_list_item_loadmore_text);
                    textView.setTypeface(FontHelper.getComfortaa(MainActivity.this, true));
                    final ImageView imageView = (ImageView) convertView
                        .findViewById(R.id.main_list_item_loadmore_loadingimage);
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
                            HNFeedTaskLoadMore.start(MainActivity.this, MainActivity.this, mFeed,
                                TASKCODE_LOAD_MORE_POSTS);
                        }
                    });
                    break;
                default:
                    break;
            }

            return convertView;
        }
    }

    private String getArticleViewURL(HNPost post) {
        return ArticleReaderActivity.getArticleViewURL(post, SettingsActivity.getHtmlProvider(this), this);
    }

    public static void openURLInBrowser(String url, Activity a) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        a.startActivity(browserIntent);
    }

    public static void openPostInApp(HNPost post, String overrideHtmlProvider, Activity a) {
        Intent i = new Intent(a, ArticleReaderActivity_.class);
        i.putExtra(ArticleReaderActivity.EXTRA_HNPOST, post);
        if (overrideHtmlProvider != null)
            i.putExtra(ArticleReaderActivity.EXTRA_HTMLPROVIDER_OVERRIDE, overrideHtmlProvider);
        a.startActivity(i);
    }

    static class PostViewHolder {
        TextView titleView;
        TextView urlView;
        TextView pointsView;
        TextView commentsCountView;
        LinearLayout textContainer;
        Button commentsButton;
    }

}