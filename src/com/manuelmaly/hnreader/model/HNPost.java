package com.manuelmaly.hnreader.model;

import java.io.Serializable;

public class HNPost implements Serializable {
    
    private static final long serialVersionUID = -6764758363164898276L;
    private String mURL;
    private String mTitle;
    private String mAuthor;
    private int mCommentsCount;
    private int mPoints;
    private String mURLDomain;
    private String mPostID; // as found in link to comments
    
    public HNPost(String url, String title, String urlDomain, String author, String postID, int commentsCount, int points) {
        super();
        mURL = url;
        mTitle = title;
        mURLDomain = urlDomain;
        mAuthor = author;
        mPostID = postID;
        mCommentsCount = commentsCount;
        mPoints = points;
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
    
    public String getPostID() {
        return mPostID;
    }

    public int getPoints() {
        return mPoints;
    }
    
    public String getURLDomain() {
        return mURLDomain;
    }
    
}
