package com.manuelmaly.hn.server;

import android.content.Context;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.HashMap;

public class StringDownloadCommand extends BaseHTTPCommand<String> {

    public StringDownloadCommand(String url, HashMap<String, String> queryParams, RequestType type, boolean notifyFinishedBroadcast,
        String notificationBroadcastIntentID, Context applicationContext, CookieStore cookieStore) {
        super(url, queryParams, type, notifyFinishedBroadcast, notificationBroadcastIntentID, applicationContext, 60000, 60000,
            null);
        setCookieStore(cookieStore);
    }

    @Override
    protected HttpUriRequest setRequestData(HttpUriRequest request) {
        request.setHeader(ACCEPT_HEADER, HTML_MIME);
        return request;
    }

    @Override
    protected ResponseHandler<String> getResponseHandler(HttpClient client) {
        return new HTMLResponseHandler(this, client);
    }

}
