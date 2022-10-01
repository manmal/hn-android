package com.manuelmaly.hn.server;

import android.content.Context;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.client.params.HttpClientParams;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import java.util.HashMap;

public class VoteHTTPCommand extends BaseHTTPCommand<String> {

    public VoteHTTPCommand(String url, HashMap<String, String> queryParams, RequestType type, boolean notifyFinishedBroadcast,
        String notificationBroadcastIntentID, Context applicationContext) {
        super(url, queryParams, type, notifyFinishedBroadcast, notificationBroadcastIntentID, applicationContext, 60000, 60000,
            null);
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
    protected ResponseHandler<String> getResponseHandler(HttpClient client) {
        return new GetHNUserTokenResponseHandler(this, client);
    }



}
