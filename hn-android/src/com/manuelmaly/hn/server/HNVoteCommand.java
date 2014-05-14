package com.manuelmaly.hn.server;

import java.util.HashMap;

import org.apache.http.client.CookieStore;

import android.content.Context;

public class HNVoteCommand extends NoResponseCommand {

    public HNVoteCommand(String url, HashMap<String, String> queryParams, com.manuelmaly.hn.server.IAPICommand.RequestType type,
        boolean notifyFinishedBroadcast, String notificationBroadcastIntentID, Context applicationContext,
        CookieStore cookieStore) {
        super(url, queryParams, type, notifyFinishedBroadcast, notificationBroadcastIntentID, applicationContext, cookieStore);
    }

    @Override
    boolean validateResponseContent(String content) {
        if (content.equals(""))
            return true;
        return false;
    }

}
