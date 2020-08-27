/*
 * Copyright (c) 2017-2018 Onegini B.V.
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

package com.onegini.mobile.sdk.cordova.handler;

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.EVENT_ON_REGISTRATION_REQUEST;

import org.apache.cordova.CallbackContext;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.onegini.mobile.sdk.android.handlers.request.OneginiBrowserRegistrationRequestHandler;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiBrowserRegistrationCallback;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;

public class BrowserRegistrationRequestHandler implements OneginiBrowserRegistrationRequestHandler {

  private static final String PARAM_USER_ID = "user_id";

  private static String userId;
  private static OneginiBrowserRegistrationCallback callback;
  private static CallbackContext browserRegistrationRequestCallbackContext;
  private static boolean shouldOpenBrowser;
  private final Context context;

  public BrowserRegistrationRequestHandler(final Context context) {
    this.context = context;
  }

  @SuppressWarnings("unused")
  public static void setUserId(final String userId) {
    BrowserRegistrationRequestHandler.userId = userId;
  }

  public static OneginiBrowserRegistrationCallback getCallback() {
    return callback;
  }

  public static void setBrowserRegistrationRequestCallbackContext(final CallbackContext browserRegistrationRequestCallbackContext) {
    BrowserRegistrationRequestHandler.browserRegistrationRequestCallbackContext = browserRegistrationRequestCallbackContext;
  }

  public static void setShouldOpenBrowser(final boolean shouldOpenBrowser) {
    BrowserRegistrationRequestHandler.shouldOpenBrowser = shouldOpenBrowser;
  }

  public static void handleRegistrationCallback(final Uri uri) {
    if (callback != null) {
      callback.handleRegistrationCallback(uri);
    }
  }

  @Override
  public void startRegistration(Uri uri, final OneginiBrowserRegistrationCallback oneginiBrowserRegistrationCallback) {
    uri = getRegistrationUriWithParameters(uri);
    callback = oneginiBrowserRegistrationCallback;

    sendRegistrationRequestEvent(uri);
    if (shouldOpenBrowser) {
      openBrowserForRegistration(uri);
    }
  }

  private Uri getRegistrationUriWithParameters(final Uri uri) {
    if (userId == null) {
      return uri;
    }

    final Uri.Builder builder = uri.buildUpon();
    builder.appendQueryParameter(PARAM_USER_ID, userId);
    return builder.build();
  }

  private void openBrowserForRegistration(final Uri uri) {
    final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

    context.startActivity(intent);
  }

  private void sendRegistrationRequestEvent(final Uri uri) {
    browserRegistrationRequestCallbackContext.sendPluginResult(new PluginResultBuilder()
        .shouldKeepCallback()
        .withSuccess()
        .withEvent(EVENT_ON_REGISTRATION_REQUEST)
        .withUri(uri)
        .build());

    browserRegistrationRequestCallbackContext = null;
  }

}
