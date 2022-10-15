package com.manuelmaly.hn.server;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Handles HTML response of a {@link HttpClient}.
 * @author manuelmaly
 */
public class HTMLResponseHandler implements ResponseHandler<String> {

    private IAPICommand<String> mCommand;
    
    public HTMLResponseHandler(IAPICommand<String> command, HttpClient client) {
        mCommand = command;
    }
    
    @Override
    public String handleResponse(HttpResponse response)
            throws ClientProtocolException, IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.getEntity().writeTo(out);
        final StatusLine statusLine = response.getStatusLine();
        final String responseString = out.toString();
        out.close();
        int statusCode = statusLine.getStatusCode();

        mCommand.responseHandlingFinished(responseString, statusCode);
        return null;
    }

}
