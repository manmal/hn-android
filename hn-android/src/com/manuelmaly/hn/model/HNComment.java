package com.manuelmaly.hn.model;

import java.io.Serializable;

public class HNComment implements Serializable {

    private static final long serialVersionUID = 1286983917054008714L;
    private String mTimeAgo; // do not want to parse this :P
    private String mAuthor;
    private String mCommentLink;
    private String mText;
    private String mUpvoteUrl;
    private String mDownvoteUrl;
    private int mCommentLevel;
    private boolean mDownvoted;
    private HNCommentTreeNode mTreeNode;

    public HNComment(String timeAgo, String author, String commentLink, String text, int commentLevel, boolean downvoted, String upvoteUrl,
                String downvoteUrl) {
        super();
        mTimeAgo = timeAgo;
        mAuthor = author;
        mCommentLink = commentLink;
        mText = text;
        mCommentLevel = commentLevel;
        mDownvoted = downvoted;
        mUpvoteUrl = upvoteUrl;
        mDownvoteUrl = downvoteUrl;
    }

    public String getTimeAgo() {
        return mTimeAgo;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getCommentLink() {
        return mCommentLink;
    }

    public String getText() {
        return mText;
    }

    public boolean isDownvoted() {
        return mDownvoted;
    }

    public int getCommentLevel() {
        return mCommentLevel;
    }
    
    public String getUpvoteUrl(String currentUserName) {
        if (mUpvoteUrl == null || !mUpvoteUrl.contains(currentUserName))
            return null;
        return mUpvoteUrl;
    }

    public String getDownvoteUrl(String currentUserName) {
        if (mDownvoteUrl == null || !mDownvoteUrl.contains(currentUserName))
            return null;
        return mDownvoteUrl;
    }
    
    public HNCommentTreeNode getTreeNode() {
        return mTreeNode;
    }
    
    public void setTreeNode(HNCommentTreeNode treeNode) {
        mTreeNode = treeNode;
    }
    
}
