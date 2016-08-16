package com.manuelmaly.hn.server;

import android.content.Context;

import org.apache.http.client.CookieStore;

import java.util.HashMap;

public class HNVoteCommand extends NoResponseCommand {

    public HNVoteCommand(String url, HashMap<String, String> queryParams, com.manuelmaly.hn.server.IAPICommand.RequestType type,
        boolean notifyFinishedBroadcast, String notificationBroadcastIntentID, Context applicationContext,
        CookieStore cookieStore) {
        super(url, queryParams, type, notifyFinishedBroadcast, notificationBroadcastIntentID, applicationContext, cookieStore);
    }

    @Override
    boolean validateResponseContent(String content) {
        if (!content.contains("You have to be logged in to vote."))
            return true;
        return false;
    }

}
