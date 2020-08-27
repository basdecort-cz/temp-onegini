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

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.EVENT_FINGERPRINT_CAPTURED;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.EVENT_FINGERPRINT_FAILED;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.EVENT_FINGERPRINT_REQUEST;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import com.onegini.mobile.sdk.android.handlers.request.OneginiFingerprintAuthenticationRequestHandler;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiFingerprintCallback;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;

public class FingerprintAuthenticationRequestHandler implements OneginiFingerprintAuthenticationRequestHandler {
  private static FingerprintAuthenticationRequestHandler instance;
  private CallbackContext startAuthenticationCallbackContext;
  private OneginiFingerprintCallback fingerprintCallback;

  protected FingerprintAuthenticationRequestHandler() {

  }

  public static FingerprintAuthenticationRequestHandler getInstance() {
    if (instance == null) {
      instance = new FingerprintAuthenticationRequestHandler();
    }

    return instance;
  }

  public void setStartAuthenticationCallbackContext(final CallbackContext startAuthenticationCallbackContext) {
    this.startAuthenticationCallbackContext = startAuthenticationCallbackContext;
  }

  public OneginiFingerprintCallback getFingerprintCallback() {
    return fingerprintCallback;
  }

  @Override
  public void startAuthentication(final UserProfile userProfile, final OneginiFingerprintCallback fingerprintCallback) {
    this.fingerprintCallback = fingerprintCallback;

    final PluginResult pluginResult = new PluginResultBuilder()
        .withSuccess()
        .shouldKeepCallback()
        .withEvent(EVENT_FINGERPRINT_REQUEST)
        .build();

    sendStartAuthenticationResult(pluginResult);
  }

  @Override
  public void onNextAuthenticationAttempt() {
    final PluginResult pluginResult = new PluginResultBuilder()
        .withSuccess()
        .shouldKeepCallback()
        .withEvent(EVENT_FINGERPRINT_FAILED)
        .build();

    sendStartAuthenticationResult(pluginResult);
  }

  @Override
  public void onFingerprintCaptured() {
    final PluginResult pluginResult = new PluginResultBuilder()
        .withSuccess()
        .shouldKeepCallback()
        .withEvent(EVENT_FINGERPRINT_CAPTURED)
        .build();

    sendStartAuthenticationResult(pluginResult);
  }

  @Override
  public void finishAuthentication() {
    fingerprintCallback = null;
  }

  private void sendStartAuthenticationResult(final PluginResult pluginResult) {
    if (startAuthenticationCallbackContext != null && !startAuthenticationCallbackContext.isFinished()) {
      startAuthenticationCallbackContext.sendPluginResult(pluginResult);
    }
  }
}
