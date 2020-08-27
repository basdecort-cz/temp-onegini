/*
 * Copyright (c) 2017 Onegini B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onegini.mobile.sdk.cordova.tests;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.onegini.mobile.sdk.cordova.handler.BrowserRegistrationRequestHandler;

public class TestUtils extends CordovaPlugin {

  private static final String ACTION_GET_REDIRECT_URL = "getRedirectUrl";
  private static final String ACTION_SET_USER_ID = "setUserId";
  private static final String ACTION_SET_PREFERENCE = "setPreference";
  private static final String PARAM_USER_ID = "userId";

  @Override
  public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_SET_USER_ID.equals(action)) {
      setUserId(args, callbackContext);
      return true;
    } else if (ACTION_SET_PREFERENCE.equals(action)) {
      setPreference(args);
      return true;
    } else if (ACTION_GET_REDIRECT_URL.equals(action)) {
      getRedirectUrl(args, callbackContext);
      return true;
    }

    return false;
  }

  private void setUserId(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    final String userId = args.getJSONObject(0).getString(PARAM_USER_ID);
    BrowserRegistrationRequestHandler.setUserId(userId);

    callbackContext.success();
  }

  private void setPreference(final JSONArray args) throws JSONException{
    final String key = args.getString(0);
    final String value = args.getString(1);

    preferences.set(key, value);
  }

  private void getRedirectUrl(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    final URL url;

    try {
      url = new URL(args.getJSONObject(0).getString("url"));
    } catch (final MalformedURLException e) {
      e.printStackTrace();
      callbackContext.error("Could not parse url");
      return;
    }

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        final String redirectUrl;
        final HttpsURLConnection urlConnection;

        try {
          urlConnection = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
          e.printStackTrace();
          callbackContext.error("Error while opening URL connection");
          return;
        }

        urlConnection.setInstanceFollowRedirects(false);

        try {
          urlConnection.connect();
        } catch (IOException e) {
          e.printStackTrace();
          callbackContext.error("IOException");
          return;
        }

        redirectUrl = urlConnection.getHeaderField("Location");
        callbackContext.success(redirectUrl);
      }
    });
  }
}
