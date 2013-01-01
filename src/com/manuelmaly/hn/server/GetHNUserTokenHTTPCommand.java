package com.manuelmaly.hn.server;

import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

public class GetHNUserTokenHTTPCommand extends BaseHTTPCommand<String> {

    public GetHNUserTokenHTTPCommand(String url, String queryParams, RequestType type, boolean notifyFinishedBroadcast,
        String notificationBroadcastIntentID, Context applicationContext) {
        super(url, queryParams, type, notifyFinishedBroadcast, notificationBroadcastIntentID, applicationContext, 60000, 60000);
    }

    @Override
    protected void modifyHttpClient(DefaultHttpClient client) {
        super.modifyHttpClient(client);
        HttpClientParams.setRedirecting(client.getParams(), false);
    }
    
    @Override
    protected HttpUriRequest setRequestData(HttpUriRequest request) {
        return request;
    }

    @Override
    protected ResponseHandler<String> getResponseHandler() {
        return new GetHNUserTokenResponseHandler(this);
    }

    @Override
    protected CookieStore getCookieStore() {
        return null;
    }

}
