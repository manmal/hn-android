package com.manuelmaly.hn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HNPostComments implements Serializable {

    private static final long serialVersionUID = -2305617988079011364L;
private List<HNComment> mComments;
    
    public HNPostComments() {
        mComments = new ArrayList<HNComment>();
    }
    
    public HNPostComments(List<HNComment> comments) {
        mComments = comments;
    }

    public void addComment(HNComment comment) {
        mComments.add(comment);
    }

    public List<HNComment> getComments() {
        return mComments;
    }
    
    public void addComments(List<HNComment> comments) {
        mComments.addAll(comments);
    }
    

}
