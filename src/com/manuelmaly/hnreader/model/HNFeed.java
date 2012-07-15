package com.manuelmaly.hnreader.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HNFeed implements Serializable {

    private static final long serialVersionUID = -7957577448455303642L;
    private List<HNPost> mPosts;
    
    public HNFeed() {
        mPosts = new ArrayList<HNPost>();
    }
    
    public HNFeed(List<HNPost> posts) {
        this.mPosts = posts;
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
    
}
