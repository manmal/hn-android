package com.manuelmaly.hn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.ViewById;
import com.manuelmaly.hn.login.*;
import com.manuelmaly.hn.model.HNComment;
import com.manuelmaly.hn.model.HNPost;
import com.manuelmaly.hn.model.HNPostComments;
import com.manuelmaly.hn.reuse.LinkifiedTextView;
import com.manuelmaly.hn.task.HNPostCommentsTask;
import com.manuelmaly.hn.task.HNVoteTask;
import com.manuelmaly.hn.task.ITaskFinishedHandler;
import com.manuelmaly.hn.util.DisplayHelper;
import com.manuelmaly.hn.util.FileUtil;
import com.manuelmaly.hn.util.FontHelper;

@EActivity(R.layout.comments_activity)
public class CommentsActivity extends BaseListActivity implements ITaskFinishedHandler<HNPostComments> {

    public static final String EXTRA_HNPOST = "HNPOST";
    private static final int TASKCODE_VOTE = 100;

    private static final int ACTIVITY_LOGIN = 136;

    @ViewById(R.id.comments_list)
    ListView mCommentsList;

    @ViewById(R.id.actionbar)
    FrameLayout mActionbarContainer;

    @ViewById(R.id.actionbar_title_button)
    Button mActionbarTitle;

    @ViewById(R.id.actionbar_refresh)
    ImageView mActionbarRefresh;
    
    @ViewById(R.id.actionbar_refresh_container)
    LinearLayout mActionbarRefreshContainer;
    
    @ViewById(R.id.actionbar_refresh_progress)
    ProgressBar mActionbarRefreshProgress;

    @ViewById(R.id.actionbar_share)
    ImageView mActionbarShare;

    @ViewById(R.id.actionbar_back)
    ImageView mActionbarBack;

    @ViewById(R.id.comments_root)
    LinearLayout mRootView;

    @SystemService
    LayoutInflater mInflater;

    LinearLayout mCommentHeader;
    TextView mCommentHeaderText;
    TextView mEmptyView;

    HNPost mPost;
    HNPostComments mComments;
    CommentsAdapter mCommentsListAdapter;
    boolean mHaveLoadedPosts = false;

    String mCurrentFontSize = null;
    int mFontSizeText;
    int mFontSizeMetadata;
    int mCommentLevelIndentPx;

    HashSet<HNComment> mUpvotedComments;
    
    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;

    HNComment mPendingVote;
    HashSet<HNComment> mVotedComments;

    @AfterViews
    public void init() {
        mPost = (HNPost) getIntent().getSerializableExtra(EXTRA_HNPOST);
        if (mPost == null || mPost.getPostID() == null) {
            Toast.makeText(this, "The belonging post has not been loaded", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mCommentLevelIndentPx = Math.min(DisplayHelper.getScreenHeight(this), DisplayHelper.getScreenWidth(this)) / 30;

        initCommentsHeader();
        mComments = new HNPostComments();
        mVotedComments = new HashSet<HNComment>();
        mCommentsListAdapter = new CommentsAdapter();
        mCommentHeaderText.setVisibility(View.GONE);
        mEmptyView = getEmptyTextView(mRootView);
        mCommentsList.setEmptyView(mEmptyView);
        mCommentsList.addHeaderView(mCommentHeader, null, false);
        mCommentsList.setAdapter(mCommentsListAdapter);

        mActionbarContainer.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mCommentsList.smoothScrollToPosition(0);
            }
        });

        mActionbarTitle.setTypeface(FontHelper.getComfortaa(this, true));
        mActionbarTitle.setText(getString(R.string.comments));
        mActionbarTitle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (Settings.getHtmlViewer(CommentsActivity.this).equals(
                    getString(R.string.pref_htmlviewer_browser))) {
                    String articleURL = ArticleReaderActivity.getArticleViewURL(mPost,
                        Settings.getHtmlProvider(CommentsActivity.this), CommentsActivity.this);
                    MainActivity.openURLInBrowser(articleURL, CommentsActivity.this);
                } else {
                    Intent i = new Intent(CommentsActivity.this, ArticleReaderActivity_.class);
                    i.putExtra(CommentsActivity.EXTRA_HNPOST, mPost);
                    if (getIntent().getStringExtra(ArticleReaderActivity.EXTRA_HTMLPROVIDER_OVERRIDE) != null)
                        i.putExtra(ArticleReaderActivity.EXTRA_HTMLPROVIDER_OVERRIDE,
                            getIntent().getStringExtra(ArticleReaderActivity.EXTRA_HTMLPROVIDER_OVERRIDE));
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        });

        mActionbarBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        mActionbarShare.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, mPost.getTitle() + " | Hacker News");
                i.putExtra(Intent.EXTRA_TEXT, "https://news.ycombinator.com/item?id=" + mPost.getPostID());
                startActivity(Intent.createChooser(i, getString(R.string.share_comments_url)));
            }
        });

        mActionbarRefresh.setImageDrawable(getResources().getDrawable(R.drawable.refresh));
        mActionbarRefreshContainer.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (HNPostCommentsTask.isRunning(mPost.getPostID()))
                    HNPostCommentsTask.stopCurrent(mPost.getPostID());
                else
                    startFeedLoading();
            }
        });

        mActionbarRefreshProgress.setVisibility(View.GONE);

        loadIntermediateCommentsFromStore();
        startFeedLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // refresh if font size changed
        if (refreshFontSizes())
        	mCommentsListAdapter.notifyDataSetChanged();

        // restore vertical scrolling position if applicable
        if (mListState != null)
            mCommentsList.onRestoreInstanceState(mListState);
        mListState = null;
    }

    @Override
    public void onTaskFinished(int taskCode, TaskResultCode code, HNPostComments result, Object tag) {
        if (code.equals(TaskResultCode.Success) && mCommentsListAdapter != null)
            showComments(result);
        else if (!code.equals(TaskResultCode.Success))
            Toast.makeText(this, getString(R.string.
                    error_unable_to_retrieve_comments), Toast.LENGTH_SHORT).show();
        updateEmptyView();
        updateStatusIndicatorOnLoadingFinished(code);
    }

    private void showComments(HNPostComments comments) {
        if (comments.getHeaderHtml() != null && mCommentHeaderText.getVisibility() != View.VISIBLE) {
            mCommentHeaderText.setVisibility(View.VISIBLE);
            // We trim it here to get rid of pesky newlines that come from
            // closing <p> tags
            mCommentHeaderText.setText(Html.fromHtml(comments.getHeaderHtml()).toString().trim());

            // Linkify.ALL does some highlighting where we don't want it
            // (i.e if you just put certain tlds in) so we use this custom regex.
            Linkify.addLinks(mCommentHeaderText, Linkify.WEB_URLS); //
        }

        mComments = comments;

        mCommentsListAdapter.notifyDataSetChanged();
    }

    private void loadIntermediateCommentsFromStore() {
        new GetLastHNPostCommentsTask().execute(mPost.getPostID());
    }

    class GetLastHNPostCommentsTask extends FileUtil.GetLastHNPostCommentsTask {
        protected void onPostExecute(HNPostComments result) {
            if (result != null && result.getUserAcquiredFor().equals(Settings
                    .getUserName(CommentsActivity.this)))
                showComments(result);
            else {
                updateEmptyView();
            }
        }
    }

    private void updateStatusIndicatorOnLoadingStarted() {
        mActionbarRefreshProgress.setVisibility(View.VISIBLE);
        mActionbarRefresh.setVisibility(View.GONE);
    }

    private void updateStatusIndicatorOnLoadingFinished(TaskResultCode code) {
    	mActionbarRefreshProgress.setVisibility(View.GONE);
    	mActionbarRefresh.setVisibility(View.VISIBLE);
    }

    private void startFeedLoading() {
        mHaveLoadedPosts = false;
        HNPostCommentsTask.startOrReattach(this, this, mPost.getPostID(), 0);
        updateStatusIndicatorOnLoadingStarted();
    }

    private boolean refreshFontSizes() {
        final String fontSize = Settings.getFontSize(this);
        if ((mCurrentFontSize == null) || (!mCurrentFontSize.equals(fontSize))) {
        	mCurrentFontSize = fontSize;
	        if (fontSize.equals(getString(R.string.pref_fontsize_small))) {
	            mFontSizeText = 14;
	            mFontSizeMetadata = 12;
	        } else if (fontSize.equals(getString(R.string.pref_fontsize_normal))) {
	            mFontSizeText = 16;
	            mFontSizeMetadata = 14;
	        } else {
	            mFontSizeText = 20;
	            mFontSizeMetadata = 18;
	        }
	        return true;
        }

        return false;
    }
    
    private void vote(String voteURL, HNComment comment) {
        HNVoteTask.start(voteURL, this, new VoteTaskFinishedHandler(), TASKCODE_VOTE, comment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        mListState = state.getParcelable(LIST_STATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        mListState = mCommentsList.onSaveInstanceState();
        state.putParcelable(LIST_STATE, mListState);
    }

    private void initCommentsHeader() {
        // Don't worry about reallocating this stuff it has already been called
        if (mCommentHeader == null) {
            mCommentHeader = new LinearLayout(this);
            mCommentHeader.setOrientation(LinearLayout.VERTICAL);
            mCommentHeaderText = new TextView(this);

            // Division by 2 just gave the right feel, I'm unsure how well it
            // will work across platforms
            mCommentHeaderText.setPadding(mCommentLevelIndentPx, mCommentLevelIndentPx/2, mCommentLevelIndentPx/2, mCommentLevelIndentPx/2);
            mCommentHeader.addView(mCommentHeaderText);
            mCommentHeaderText.setTextColor(getResources()
                    .getColor(R.color.gray_comments_information));
            View v = new View(this);
            v.setBackgroundColor(getResources().getColor(R.color.gray_comments_divider));
            v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
            mCommentHeader.addView(v);
        }
    }

    private void updateEmptyView() {
        if (mHaveLoadedPosts)
            mEmptyView.setText(getString(R.string.no_comments));

        mHaveLoadedPosts = true;
    }

    private class LongPressMenuListAdapter implements ListAdapter, DialogInterface.OnClickListener {

        HNComment mComment;
        boolean mIsLoggedIn;
        boolean mUpVotingEnabled;
        boolean mDownVotingEnabled;
        ArrayList<CharSequence> mItems;

        public LongPressMenuListAdapter(HNComment comment) {
            mComment = comment;
            mIsLoggedIn = Settings.isUserLoggedIn(CommentsActivity.this);
            mUpVotingEnabled = !mIsLoggedIn
                || (mComment.getUpvoteUrl(Settings.getUserName(CommentsActivity.this)) != null && !mVotedComments.contains(mComment));
            mDownVotingEnabled = mIsLoggedIn
                && (mComment.getDownvoteUrl(Settings.getUserName(CommentsActivity.this)) != null && !mVotedComments.contains(mComments));

            mItems = new ArrayList<CharSequence>();
            
            // Figure out why this is false
            if (mUpVotingEnabled)
                mItems.add(getString(R.string.upvote));
            if (mDownVotingEnabled)
                mItems.add(getString(R.string.downvote));

            if(!mUpVotingEnabled && !mDownVotingEnabled)
                mItems.add(getString(R.string.already_voted_on));

            if (comment.getTreeNode().isExpanded())
                mItems.add(getString(R.string.collapse_comment));
            else
                mItems.add(getString(R.string.expand_comment));
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public CharSequence getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) mInflater.inflate(android.R.layout.simple_list_item_1, null);
            view.setText(getItem(position));
            if (!mUpVotingEnabled && position == 0)
                view.setTextColor(getResources().getColor(android.R.color.darker_gray));
            return view;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            // Top item will always be "upvote" or "already upvoted"
            // So, if upvoting is not enabled, this must be already upvoted
            // In that case we want to disable it
            if (!mUpVotingEnabled && position == 0)
                return false;
            return true;
        }

        @Override
        public void onClick(DialogInterface dialog, int item) {
            String clickedText = getItem(item).toString();
            // If the clicked text is "upvote", then we want to upvote if
            // the user is logged in.  If the user is not logged in then
            // we want to tell the user to login
            if (clickedText.equals(getApplicationContext().getString(R.string.upvote))) {
                if (!mIsLoggedIn) {
                    setCommentToUpvote(mComment);
                    startActivityForResult(new Intent(getApplicationContext(), LoginActivity_.class),
                            ACTIVITY_LOGIN);
                }
                else
                    vote(mComment.getUpvoteUrl(Settings.getUserName(CommentsActivity.this)), mComment);
            } else if (clickedText.equals(getApplicationContext().getString(R.string.downvote))) {
                // We don't need to test if the user is logged in here because
                // They won't have a dowvnote url to see if they aren't logged in
                vote(mComment.getDownvoteUrl(Settings.getUserName(CommentsActivity.this)), mComment);
            } else {
                mComments.toggleCommentExpanded(mComment);
                mCommentsListAdapter.notifyDataSetChanged();
            }
        }

    }

    class VoteTaskFinishedHandler implements ITaskFinishedHandler<Boolean> {
        @Override
        public void onTaskFinished(int taskCode, com.manuelmaly.hn.task.ITaskFinishedHandler.TaskResultCode code,
            Boolean result, Object tag) {
            if (taskCode == TASKCODE_VOTE) {
                if (result != null && result.booleanValue()) {
                    Toast.makeText(CommentsActivity.this, R.string.vote_success, Toast.LENGTH_SHORT).show();
                    HNComment comment = (HNComment)tag;
                    if (comment != null)
                        mVotedComments.add(comment);
                } else
                    Toast.makeText(CommentsActivity.this, R.string.vote_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    class CommentsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mComments.getComments().size();
        }

        @Override
        public HNComment getItem(int position) {
            return mComments.getComments().get(position);
        }

        @Override
        public long getItemId(int position) {
            // Item ID not needed here:
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = (FrameLayout) mInflater.inflate(R.layout.comments_list_item, null);
                CommentViewHolder holder = new CommentViewHolder();
                holder.rootView = convertView;
                holder.textView = (LinkifiedTextView) convertView.findViewById(R.id.comments_list_item_text);
                holder.spacersContainer = (LinearLayout) convertView
                    .findViewById(R.id.comments_list_item_spacerscontainer);
                holder.authorView = (TextView) convertView.findViewById(R.id.comments_list_item_author);
                holder.timeAgoView = (TextView) convertView.findViewById(R.id.comments_list_item_timeago);
                holder.expandView = (ImageView) convertView.findViewById(R.id.comments_list_item_expand);
                convertView.setTag(holder);
            }
            HNComment comment = getItem(position);
            CommentViewHolder holder = (CommentViewHolder) convertView.getTag();
            holder.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (getItem(position).getTreeNode().hasChildren()) {
                        mComments.toggleCommentExpanded(getItem(position));
                        mCommentsListAdapter.notifyDataSetChanged();
                    }
                }
            });
            holder.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    final HNComment comment = getItem(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CommentsActivity.this);
                    LongPressMenuListAdapter adapter = new LongPressMenuListAdapter(comment);
                    builder.setAdapter(adapter, adapter).show();
                    return true;
                }
            });

            holder.setComment(comment, mCommentLevelIndentPx, CommentsActivity.this, mFontSizeText, mFontSizeMetadata);

            return convertView;
        }

    }

    static class CommentViewHolder {
        View rootView;
        LinkifiedTextView textView;
        TextView authorView;
        TextView timeAgoView;
        ImageView expandView;
        LinearLayout spacersContainer;

        public void setComment(HNComment comment, int commentLevelIndentPx, Context c, int commentTextSize,
            int metadataTextSize) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, commentTextSize);
            textView.setText(Html.fromHtml(comment.getText()));
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            authorView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, metadataTextSize);
            timeAgoView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, metadataTextSize);
            if (!TextUtils.isEmpty(comment.getAuthor())) {
                authorView.setText(comment.getAuthor());
                timeAgoView.setText(", " + comment.getTimeAgo());
            }
            else {
                authorView.setText(c.getString(R.string.deleted));
                // We set this here so that convertView doesn't reuse the old
                // timeAgoView value
                timeAgoView.setText("");
            }
            expandView.setVisibility(comment.getTreeNode().isExpanded() ? View.INVISIBLE : View.VISIBLE);
            spacersContainer.removeAllViews();
            for (int i = 0; i < comment.getCommentLevel(); i++) {
                View spacer = new View(c);
                spacer.setLayoutParams(new android.widget.LinearLayout.LayoutParams(commentLevelIndentPx,
                    LayoutParams.MATCH_PARENT));
                int spacerAlpha = Math.max(70 - i * 10, 10);
                spacer.setBackgroundColor(Color.argb(spacerAlpha, 0, 0, 0));
                spacersContainer.addView(spacer, i);
            }
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            rootView.setOnClickListener(onClickListener);
            textView.setOnClickListener(onClickListener);
        }

        public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
            rootView.setOnLongClickListener(onLongClickListener);
            textView.setOnLongClickListener(onLongClickListener);
        }
    }
 
    protected void setCommentToUpvote(HNComment comment) {
        mPendingVote = comment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case ACTIVITY_LOGIN:
            if (resultCode == RESULT_OK) {
                if (mPendingVote != null) {
                    mComments = new HNPostComments();
                    mCommentsListAdapter.notifyDataSetChanged();
                    startFeedLoading();
                    Toast.makeText(this, getString(R.string.login_success_reloading), Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.error_login_to_vote), Toast.LENGTH_LONG).show();
            }
        }
    }

}
