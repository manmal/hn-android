package com.manuelmaly.hn.server;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;

/**
 * Handles HTML response of a request to login to HN and retrieving its user token.
 * @author manuelmaly
 */
public class GetHNUserTokenResponseHandler implements ResponseHandler<String> {

    private IAPICommand<String> mCommand;
    
    public GetHNUserTokenResponseHandler(IAPICommand<String> command, HttpClient client) {
        mCommand = command;
    }
    
    @Override
    public String handleResponse(HttpResponse response)
            throws ClientProtocolException, IOException {

        String responseString = "";
        Header[] headers = response.getHeaders("Set-Cookie");
        if (headers.length > 0) {
            HeaderElement[] cookieElements = headers[0].getElements();
            if (cookieElements.length > 0) {
                responseString = cookieElements[0].getValue();
            }
        };
        
        mCommand.responseHandlingFinished(responseString, response.getStatusLine().getStatusCode());
        return responseString;
    }

}
