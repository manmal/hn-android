package com.manuelmaly.hnreader.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;

/**
 * Handles HTML response of a {@link HttpClient}.
 * @author manuelmaly
 */
public class HTMLResponseHandler implements ResponseHandler<String> {

    private IAPICommand<String> mCommand;
    
    public HTMLResponseHandler(IAPICommand<String> command) {
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
