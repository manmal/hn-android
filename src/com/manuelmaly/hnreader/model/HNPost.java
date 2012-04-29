package com.manuelmaly.hnreader.model;

public class HNPost {
    
    private String mURL;
    private String mTitle;
    private String mAuthor;
    private int mCommentsCount;
    private int mPoints;
    
    public HNPost(String url, String title, String author, int commentsCount, int points) {
        super();
        this.mURL = url;
        this.mTitle = title;
        this.mAuthor = author;
        this.mCommentsCount = commentsCount;
        this.mPoints = points;
    }

    public String getURL() {
        return mURL;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public int getCommentsCount() {
        return mCommentsCount;
    }

    public int getPoints() {
        return mPoints;
    }
    
}
