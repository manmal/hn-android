package com.manuelmaly.hn.server;

import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;

public class StringDownloadCommand extends BaseHTTPCommand<String> {

    public StringDownloadCommand(String url, String queryParams, RequestType type, boolean notifyFinishedBroadcast,
        String notificationBroadcastIntentID, Context applicationContext) {
        super(url, queryParams, type, notifyFinishedBroadcast, notificationBroadcastIntentID, applicationContext, 60000, 60000);
    }

    @Override
    protected HttpUriRequest setRequestData(HttpUriRequest request) {
        request.setHeader(ACCEPT_HEADER, HTML_MIME);
        return request;
    }

    @Override
    protected ResponseHandler<String> getResponseHandler() {
        return new HTMLResponseHandler(this);
    }

    @Override
    protected CookieStore getCookieStore() {
        return null;
    }

}
