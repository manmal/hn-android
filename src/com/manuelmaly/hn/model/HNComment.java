package com.manuelmaly.hn.model;

import java.io.Serializable;

import android.text.Spanned;

public class HNComment implements Serializable {

    private static final long serialVersionUID = 1286983917054008714L;
    private String mTimeAgo; // do not want to parse this :P
    private String mAuthor;
    private String mCommentLink;
    private String mText;
    private int mCommentLevel;
    private boolean mDownvoted;
    private Spanned mTextHTMLSpanCache;

    public HNComment(String timeAgo, String author, String commentLink, String text, int commentLevel, boolean downvoted) {
        super();
        mTimeAgo = timeAgo;
        mAuthor = author;
        mCommentLink = commentLink;
        mText = text;
        mCommentLevel = commentLevel;
        mDownvoted = downvoted;
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
    
    public Spanned getTextHTMLSpanCache() {
        return mTextHTMLSpanCache;
    }
    
    public void setTextHTMLSpanCache(Spanned HTMLSpanCache) {
        mTextHTMLSpanCache = HTMLSpanCache;
    }
}
