package com.manuelmaly.hn.util;

import android.os.AsyncTask;
import android.util.Log;

import com.manuelmaly.hn.App;
import com.manuelmaly.hn.model.HNCommentTreeNode;
import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.model.HNPostComments;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class FileUtil {

    private static final String LAST_HNFEED_FILENAME = "lastHNFeed";
    private static final String LAST_HNPOSTCOMMENTS_FILENAME_PREFIX = "lastHNPostComments";
    private static final String TAG = "FileUtil";

    public abstract static class GetLastHNFeedTask extends AsyncTask<Void, Void, HNFeed> {
        @Override
        protected HNFeed doInBackground(Void... params) {
            return getLastHNFeed();
        }
    }

    /*
     * Returns null if no last feed was found or could not be parsed.
     */
    private static HNFeed getLastHNFeed() {
        ObjectInputStream obj = null;
        try {
            obj = new ObjectInputStream(new FileInputStream(getLastHNFeedFilePath()));
            Object rawHNFeed = obj.readObject();
            if (rawHNFeed instanceof HNFeed)
                return (HNFeed) rawHNFeed;
        } catch (Exception e) {
            Log.e(TAG, "Could not get last HNFeed from file :(", e);
        } finally {
            if (obj != null) {
                try {
                    obj.close();
                } catch (IOException e) {
                    Log.e(TAG, "Couldn't close last NH feed file :(", e);
                }
            }
        }
        return null;
    }

    public static void setLastHNFeed(final HNFeed hnFeed) {
        Run.inBackground(new Runnable() {
            public void run() {
                ObjectOutputStream os = null;
                try {
                    os = new ObjectOutputStream(new FileOutputStream(getLastHNFeedFilePath()));
                    os.writeObject(hnFeed);
                } catch (Exception e) {
                    Log.e(TAG, "Could not save last HNFeed to file :(", e);
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Couldn't close last NH feed file :(", e);
                        }
                    }
                }
            }
        });
    }

    private static String getLastHNFeedFilePath() {
        File dataDir = App.getInstance().getFilesDir();
        return dataDir.getAbsolutePath() + File.pathSeparator + LAST_HNFEED_FILENAME;
    }

    public abstract static class GetLastHNPostCommentsTask extends AsyncTask<String, Void, HNPostComments> {
        @Override
        protected HNPostComments doInBackground(String... postIDs) {
            if (postIDs != null && postIDs.length > 0)
                return getLastHNPostComments(postIDs[0]);
            return null;
        }
    }

    /*
     * Returns null if no last comments file was found or could not be parsed.
     */
    private static HNPostComments getLastHNPostComments(String postID) {
        ObjectInputStream obj = null;
        try {
            obj = new ObjectInputStream(new FileInputStream(getLastHNPostCommentsPath(postID)));
            Object rawHNComments = obj.readObject();
            if (rawHNComments instanceof HNPostComments)
                return (HNPostComments) rawHNComments;
        } catch (Exception e) {
            Log.e(TAG, "Could not get last HNPostComments from file :(", e);
        } finally {
            if (obj != null) {
                try {
                    obj.close();
                } catch (IOException e) {
                    Log.e(TAG, "Couldn't close last NH comments file :(", e);
                }
            }
        }
        return null;
    }

    public static void setLastHNPostComments(final HNPostComments comments, final String postID) {
        Run.inBackground(new Runnable() {
            public void run() {
                ObjectOutputStream os = null;
                try {
                  int nodesCount = countNodes(comments.getTreeNodes());
                  if (nodesCount > 150) {
                    return;
                  }
                  os = new ObjectOutputStream(new FileOutputStream(getLastHNPostCommentsPath(postID)));
                  os.writeObject(comments);
                } catch (Exception e) {
                    Log.e(TAG, "Could not save last HNPostComments to file :(", e);
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Couldn't close last NH comments file :(", e);
                        }
                    }
                }
            }
        });
    }

    private static String getLastHNPostCommentsPath(String postID) {
        File dataDir = App.getInstance().getFilesDir();
        return dataDir.getAbsolutePath() + "/" + LAST_HNPOSTCOMMENTS_FILENAME_PREFIX + "_" + postID;
    }

  private static int countNodes(List<HNCommentTreeNode> nodes) {
    int sum = 0;

    if (nodes != null) {
      sum += nodes.size();

      for (HNCommentTreeNode n : nodes) {
        sum += countNodes(n.getChildren());
      }
    }

    return sum;
  }

}
