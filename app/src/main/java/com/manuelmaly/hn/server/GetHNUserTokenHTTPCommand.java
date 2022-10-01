package com.manuelmaly.hn.server;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.client.params.HttpClientParams;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetHNUserTokenHTTPCommand extends BaseHTTPCommand<String> {

  public GetHNUserTokenHTTPCommand(String url, HashMap<String, String> queryParams, RequestType type, boolean notifyFinishedBroadcast,
      String notificationBroadcastIntentID, Context applicationContext, Map<String, String> body) {
    super(url, queryParams, type, notifyFinishedBroadcast, notificationBroadcastIntentID, applicationContext, 60000, 60000,
        body);
  }

  @Override
  protected void modifyHttpClient(DefaultHttpClient client) {
    super.modifyHttpClient(client);
    HttpClientParams.setRedirecting(client.getParams(), false);
  }

  @Override
  protected HttpUriRequest setRequestData(HttpUriRequest request) {

    List<NameValuePair> params = new ArrayList<NameValuePair>(2);
    Map<String, String> body = getBody();
    if (body != null) {
      for (String key : body.keySet()) {
        params.add(new BasicNameValuePair(key, body.get(key)));
      }
    }

    try {
      ((HttpPost) request).setEntity((new UrlEncodedFormEntity(params, "UTF-8")));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    return request;
  }

  @Override
  protected ResponseHandler<String> getResponseHandler(HttpClient client) {
    return new GetHNUserTokenResponseHandler(this, client);
  }

}
