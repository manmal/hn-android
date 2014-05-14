package com.manuelmaly.hn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HNPostComments implements Serializable {

    private static final long serialVersionUID = -2305617988079011364L;
    private List<HNCommentTreeNode> mTreeNodes;
    private transient List<HNComment> mCommentsCache;
    private boolean mIsTreeDirty;
    private String mHeaderHtml;
    private String mUserAcquiredFor;

    public HNPostComments() {
        mTreeNodes = new ArrayList<HNCommentTreeNode>();
    }

    public HNPostComments(List<HNComment> comments) {
        this(comments, null, "");
    }

    public HNPostComments(List<HNComment> comments, String headerHtml, String userAcquiredFor) {
        mTreeNodes = new ArrayList<HNCommentTreeNode>();
        for (HNComment comment : comments) {
            if (comment.getCommentLevel() == 0)
                mTreeNodes.add(makeTreeNode(comment, comments));
        }
        mHeaderHtml = headerHtml;
        mUserAcquiredFor = userAcquiredFor;
    }

    public List<HNCommentTreeNode> getTreeNodes() {
        return mTreeNodes;
    }

    public List<HNComment> getComments() {
        if (mCommentsCache == null || mIsTreeDirty) {
            mCommentsCache = new ArrayList<HNComment>();
            
            if (mTreeNodes == null)
                mTreeNodes = new ArrayList<HNCommentTreeNode>();
            
            for (HNCommentTreeNode node : mTreeNodes)
                mCommentsCache.addAll(node.getVisibleComments());
            mIsTreeDirty = false;
        }
        return mCommentsCache;
    }

    public String getHeaderHtml() {
        return mHeaderHtml;
    }

    public String getUserAcquiredFor() {
        return mUserAcquiredFor;
    }

    private HNCommentTreeNode makeTreeNode(HNComment comment, List<HNComment> allComments) {
        HNCommentTreeNode node = new HNCommentTreeNode(comment);
        int nodeLevel = comment.getCommentLevel();
        int nodeIndex = allComments.indexOf(comment);
        comment.setTreeNode(node);
        for (int i = nodeIndex + 1; i < allComments.size(); i++) {
            HNComment childComment = allComments.get(i);
            if (childComment.getCommentLevel() > nodeLevel + 1)
                continue;
            if (childComment.getCommentLevel() <= nodeLevel)
                break;
            node.addChild(makeTreeNode(childComment, allComments));
        }
        return node;
    }

    public void toggleCommentExpanded(HNComment comment) {
        if (comment == null)
            return;

        if (comment.getTreeNode() != null) {
            comment.getTreeNode().toggleExpanded();
            mIsTreeDirty = true;
        }
    }
}
