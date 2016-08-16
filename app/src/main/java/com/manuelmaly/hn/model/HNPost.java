package com.manuelmaly.hn.model;

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
    private String mUpvoteURL;
    
    public HNPost(String url, String title, String urlDomain, String author, String postID, int commentsCount, int points, String upvoteURL) {
        super();
        mURL = url;
        mTitle = title;
        mURLDomain = urlDomain;
        mAuthor = author;
        mPostID = postID;
        mCommentsCount = commentsCount;
        mPoints = points;
        mUpvoteURL = upvoteURL;
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
    
    public String getUpvoteURL(String currentUserName) {
        if (mUpvoteURL == null || !mUpvoteURL.contains("auth=")) // HN changed authentication
            return null;
        return mUpvoteURL;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mAuthor == null) ? 0 : mAuthor.hashCode());
        result = prime * result + ((mPostID == null) ? 0 : mPostID.hashCode());
        result = prime * result + ((mURL == null) ? 0 : mURL.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HNPost other = (HNPost) obj;
        if (mAuthor == null) {
            if (other.mAuthor != null)
                return false;
        } else if (!mAuthor.equals(other.mAuthor))
            return false;
        if (mPostID == null) {
            if (other.mPostID != null)
                return false;
        } else if (!mPostID.equals(other.mPostID))
            return false;
        if (mURL == null) {
            if (other.mURL != null)
                return false;
        } else if (!mURL.equals(other.mURL))
            return false;
        return true;
    }
    
    
    
}
