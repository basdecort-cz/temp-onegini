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

package com.onegini.mobile.sdk.cordova.mobileAuthentication;

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.EVENT_CONFIRMATION_REQUEST;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.EVENT_FINGERPRINT_REQUEST;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.EVENT_PIN_REQUEST;

import org.apache.cordova.CallbackContext;

import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest;

public class Callback {

  private final OneginiMobileAuthenticationRequest mobileAuthenticationRequest;
  private final Method method;
  private CallbackContext challengeResponseCallbackContext;

  public Callback(final OneginiMobileAuthenticationRequest mobileAuthenticationRequest, final Method method) {
    this.mobileAuthenticationRequest = mobileAuthenticationRequest;
    this.method = method;
  }

  public OneginiMobileAuthenticationRequest getMobileAuthenticationRequest() {
    return mobileAuthenticationRequest;
  }

  public Method getMethod() {
    return method;
  }

  public CallbackContext getChallengeResponseCallbackContext() {
    return challengeResponseCallbackContext;
  }

  public void setChallengeResponseCallbackContext(final CallbackContext resultCallbackContext) {
    this.challengeResponseCallbackContext = resultCallbackContext;
  }

  public String getAuthenticationRequestEventName() {
    final String eventName;

    switch (method) {
      case CONFIRMATION:
        eventName = EVENT_CONFIRMATION_REQUEST;
        break;
      case PIN:
        eventName = EVENT_PIN_REQUEST;
        break;
      case FINGERPRINT:
        eventName = EVENT_FINGERPRINT_REQUEST;
        break;
      default:
        eventName = null;
        break;
    }

    return eventName;
  }

  public enum Method {
    CONFIRMATION,
    PIN,
    FINGERPRINT
  }

}
