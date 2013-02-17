package com.manuelmaly.hn.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;

public abstract class NoResponseCommand extends BaseHTTPCommand<Boolean> {

    public NoResponseCommand(String url, String queryParams, RequestType type, boolean notifyFinishedBroadcast,
        String notificationBroadcastIntentID, Context applicationContext, CookieStore cookieStore) {
        super(url, queryParams, type, notifyFinishedBroadcast, notificationBroadcastIntentID, applicationContext, 60000, 60000);
        setCookieStore(cookieStore);
    }

    @Override
    protected HttpUriRequest setRequestData(HttpUriRequest request) {
        request.setHeader(ACCEPT_HEADER, HTML_MIME);
        return request;
    }

    @Override
    protected ResponseHandler<Boolean> getResponseHandler(HttpClient client) {
        return new ResponseHandler<Boolean>() {
            
            @Override
            public Boolean handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                int statusCode = response.getStatusLine().getStatusCode();
                boolean result = (statusCode >= 200 && statusCode < 400);
                
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                
                result &= validateResponseContent(out.toString());
                
                NoResponseCommand.this.responseHandlingFinished(result, statusCode);
                return null;
            }
        };
    }
    
    abstract boolean validateResponseContent(String content);

}
