package com.manuelmaly.hn.model;

import java.io.Serializable;
import java.util.ArrayList;

public class HNCommentTreeNode implements Serializable {

    private static final long serialVersionUID = 1089928137259687565L;
    private HNComment mComment;
    private ArrayList<HNCommentTreeNode> mChildren;
    private HNCommentTreeNode mParent;
    private boolean mIsExpanded;

    public HNCommentTreeNode(HNComment comment) {
        mComment = comment;
        mChildren = new ArrayList<HNCommentTreeNode>();
        mIsExpanded = true;
    }

    public void addChild(HNCommentTreeNode child) {
        mChildren.add(child);
        child.setParent(this);
    }

    public void setParent(HNCommentTreeNode parent) {
        mParent = parent;
    }

    public HNComment getComment() {
        return mComment;
    }

    public HNCommentTreeNode getParent() {
        return mParent;
    }

    public HNCommentTreeNode getRootNode() {
        HNCommentTreeNode mRootNode;
        if (mParent == null) {
            mRootNode = this;
        } else {
            HNCommentTreeNode mCandidateRootNode = this;
            while (mCandidateRootNode.getParent() != null) {
                mCandidateRootNode = mCandidateRootNode.getParent();
            }
            mRootNode = mCandidateRootNode;
        }

        return mRootNode;
    }

    public ArrayList<HNCommentTreeNode> getChildren() {
        return mChildren;
    }
    
    public ArrayList<HNComment> getVisibleComments() {
        ArrayList<HNComment> visibleComments = new ArrayList<HNComment>();
        visibleComments.add(getComment());
        if (isExpanded()) {
            for (HNCommentTreeNode child : mChildren)
                visibleComments.addAll(child.getVisibleComments());
        }
        return visibleComments;
    }
    
    public boolean hasChildren() {
        return mChildren != null && mChildren.size() > 0;
    }
    
    public boolean isExpanded() {
        return mIsExpanded;
    }
    
    public void toggleExpanded() {
        mIsExpanded = !mIsExpanded;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mComment == null) ? 0 : mComment.hashCode());
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
        HNCommentTreeNode other = (HNCommentTreeNode) obj;
        if (mComment == null) {
            if (other.mComment != null)
                return false;
        } else if (!mComment.equals(other.mComment))
            return false;
        return true;
    }
    
    

}
