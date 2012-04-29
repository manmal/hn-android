package com.manuelmaly.hnreader.server;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;

public class HTMLDownloadCommand extends BaseHTTPCommand<String> {
    

    public HTMLDownloadCommand(String url, String queryParams, RequestType type, String notificationBroadcastIntentID,
        Context applicationContext) {
        super(url, queryParams, type, notificationBroadcastIntentID, applicationContext, 60000, 60000);
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

}
