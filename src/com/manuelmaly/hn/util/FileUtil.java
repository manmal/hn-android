package com.manuelmaly.hn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.util.Log;

import com.manuelmaly.hn.App;
import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.model.HNPostComments;

public class FileUtil {

    private static final String LAST_HNFEED_FILENAME = "lastHNFeed";
    private static final String LAST_HNPOSTCOMMENTS_FILENAME_PREFIX = "lastHNPostComments";
    private static final String TAG = "FileUtil";

    /*
     * Returns null if no last feed was found or could not be parsed.
     */
    public static HNFeed getLastHNFeed() {
        try {
            ObjectInputStream obj = new ObjectInputStream(new FileInputStream(getLastHNFeedFilePath()));
            Object rawHNFeed = obj.readObject();
            if (rawHNFeed instanceof HNFeed)
                return (HNFeed) rawHNFeed;
        } catch (Exception e) {
            Log.e(TAG, "Could not get last HNFeed from file :(", e);
        }
        return null;
    }

    public static void setLastHNFeed(HNFeed hnFeed) {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(getLastHNFeedFilePath()));
            os.writeObject(hnFeed);
        } catch (Exception e) {
            Log.e(TAG, "Could not save last HNFeed to file :(", e);
        }
    }

    private static String getLastHNFeedFilePath() {
        File dataDir = App.getInstance().getFilesDir();
        return dataDir.getAbsolutePath() + File.pathSeparator + LAST_HNFEED_FILENAME;
    }
    
    /*
     * Returns null if no last comments file was found or could not be parsed.
     */
    public static HNPostComments getLastHNPostComments(String postID) {
        try {
            ObjectInputStream obj = new ObjectInputStream(new FileInputStream(getLastHNPostCommentsPath(postID)));
            Object rawHNComments = obj.readObject();
            if (rawHNComments instanceof HNPostComments)
                return (HNPostComments) rawHNComments;
        } catch (Exception e) {
            Log.e(TAG, "Could not get last HNPostComments from file :(", e);
        }
        return null;
    }

    public static void setLastHNPostComments(HNPostComments comments, String postID) {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(getLastHNPostCommentsPath(postID)));
            os.writeObject(comments);
        } catch (Exception e) {
            Log.e(TAG, "Could not save last HNPostComments to file :(", e);
        }
    }

    private static String getLastHNPostCommentsPath(String postID) {
        File dataDir = App.getInstance().getFilesDir();
        return dataDir.getAbsolutePath() + "/" + LAST_HNPOSTCOMMENTS_FILENAME_PREFIX + "_" + postID;
    }

}
