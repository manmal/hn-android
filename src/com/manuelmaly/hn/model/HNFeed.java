package com.manuelmaly.hn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HNFeed implements Serializable {

    private static final long serialVersionUID = -7957577448455303642L;
    private List<HNPost> mPosts;
    private String mNextPageURL;
    
    public HNFeed() {
        mPosts = new ArrayList<HNPost>();
    }
    
    public HNFeed(List<HNPost> posts, String nextPageURL) {
        mPosts = posts;
        mNextPageURL = nextPageURL;
    }

    public void addPost(HNPost post) {
        mPosts.add(post);
    }

    public List<HNPost> getPosts() {
        return mPosts;
    }
    
    public void addPosts(Collection<HNPost> posts) {
        mPosts.addAll(posts);
    }
    
    public String getNextPageURL() {
        return mNextPageURL;
    }
    
    public void setNextPageURL(String mNextPageURL) {
        this.mNextPageURL = mNextPageURL;
    }
    
    public void appendLoadMoreFeed(HNFeed feed) {
        if (feed == null || feed.getPosts() == null)
            return;
        
        mPosts.addAll(feed.getPosts());
        mNextPageURL = feed.getNextPageURL();
    }
    
}
