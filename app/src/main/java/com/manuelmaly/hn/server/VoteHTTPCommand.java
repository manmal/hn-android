package com.manuelmaly.hn.server;

import android.content.Context;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;

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
