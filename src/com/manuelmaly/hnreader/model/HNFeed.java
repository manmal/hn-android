package com.manuelmaly.hnreader.model;

import java.util.ArrayList;
import java.util.List;

public class HNFeed {

    private List<HNPost> mPosts = new ArrayList<HNPost>();
    
    public void addPost(HNPost post) {
        mPosts.add(post);
    }

    public List<HNPost> getPosts() {
        return mPosts;
    }
    
}
