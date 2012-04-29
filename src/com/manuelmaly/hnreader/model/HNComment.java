package com.manuelmaly.hnreader.model;

public class HNComment {
    
    private String mTimeAgo; // do not want to parse this :P
    private String mAuthor;
    private String mCommentLink;
    private String mText;
    private boolean mDownvoted;
    
    public HNComment(String timeAgo, String author, String commentLink, String text, boolean downvoted) {
        super();
        this.mTimeAgo = timeAgo;
        this.mAuthor = author;
        this.mCommentLink = commentLink;
        this.mText = text;
        this.mDownvoted = downvoted;
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

}
