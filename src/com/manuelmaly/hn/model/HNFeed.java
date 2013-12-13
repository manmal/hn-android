package com.manuelmaly.hn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HNFeed implements Serializable {

    private static final long serialVersionUID = -7957577448455303642L;
    private List<HNPost> mPosts;
    private String mNextPageURL;
    private String mUserAcquiredFor; // this dictates if the upvote URLs are correct
    private boolean mLoadedMore; // currently, we can perform only one "load-more" action reliably
    
    public HNFeed() {
        mPosts = new ArrayList<HNPost>();
    }

    public HNFeed(List<HNPost> posts, String nextPageURL, String userAcquiredFor) {
        mPosts = posts;
        mNextPageURL = nextPageURL;
        mUserAcquiredFor = userAcquiredFor;
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
        
        mLoadedMore = true;
        for (HNPost candidate : feed.getPosts())
            if (!mPosts.contains(candidate))
                mPosts.add(candidate);
        mNextPageURL = feed.getNextPageURL();
    }
    
    public boolean isLoadedMore() {
        return mLoadedMore;
    }
    
    public String getUserAcquiredFor() {
        return mUserAcquiredFor;
    }
    
    public void search(String text) {
    	if(text == "")
         return;
    	else{
    		int i =0;
    		while(i<mPosts.size())
    		{
    			if(mPosts.get(i) == null)
    				break;
    			else if(!mPosts.get(i).getTitle().toLowerCase().contains(text.toLowerCase()))
    				mPosts.remove(i);
    			else
    				i++;
    		}
    	}
    	
    }
}
