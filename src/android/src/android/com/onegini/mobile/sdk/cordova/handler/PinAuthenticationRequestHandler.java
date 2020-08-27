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

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_INCORRECT_PIN;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_INCORRECT_PIN;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.EVENT_PIN_REQUEST;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import com.onegini.mobile.sdk.android.handlers.request.OneginiPinAuthenticationRequestHandler;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback;
import com.onegini.mobile.sdk.android.model.entity.AuthenticationAttemptCounter;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;

public class PinAuthenticationRequestHandler implements OneginiPinAuthenticationRequestHandler {

  private static PinAuthenticationRequestHandler instance = null;
  private OneginiPinCallback pinCallback = null;
  private CallbackContext startAuthenticationCallback;

  protected PinAuthenticationRequestHandler() {
  }

  public static PinAuthenticationRequestHandler getInstance() {
    if (instance == null) {
      instance = new PinAuthenticationRequestHandler();
    }

    return instance;
  }

  public OneginiPinCallback getPinCallback() {
    return pinCallback;
  }

  public void setStartAuthenticationCallbackContext(final CallbackContext startAuthenticationCallback) {
    this.startAuthenticationCallback = startAuthenticationCallback;
  }

  @Override
  public void startAuthentication(final UserProfile userProfile, final OneginiPinCallback pinCallback,
                                  final AuthenticationAttemptCounter authenticationAttemptCounter) {
    this.pinCallback = pinCallback;

    final PluginResult pluginResult = new PluginResultBuilder()
        .shouldKeepCallback()
        .withSuccess()
        .withMaxFailureCount(authenticationAttemptCounter.getMaxAttempts())
        .withRemainingFailureCount(authenticationAttemptCounter.getRemainingAttempts())
        .withEvent(EVENT_PIN_REQUEST)
        .build();

    sendStartAuthenticationResult(pluginResult);
  }

  @Override
  public void onNextAuthenticationAttempt(final AuthenticationAttemptCounter authenticationAttemptCounter) {
    final PluginResult pluginResult = new PluginResultBuilder()
        .shouldKeepCallback()
        .withPluginError(ERROR_DESCRIPTION_INCORRECT_PIN, ERROR_CODE_INCORRECT_PIN)
        .withSuccess()
        .withMaxFailureCount(authenticationAttemptCounter.getMaxAttempts())
        .withRemainingFailureCount(authenticationAttemptCounter.getRemainingAttempts())
        .withEvent(EVENT_PIN_REQUEST)
        .build();

    sendStartAuthenticationResult(pluginResult);
  }

  @Override
  public void finishAuthentication() {
    pinCallback = null;
  }

  private void sendStartAuthenticationResult(final PluginResult pluginResult) {
    if (startAuthenticationCallback != null) {
      startAuthenticationCallback.sendPluginResult(pluginResult);
    }
  }

}
