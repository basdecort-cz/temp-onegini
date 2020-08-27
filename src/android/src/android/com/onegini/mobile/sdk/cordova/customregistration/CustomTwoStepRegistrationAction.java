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

package com.onegini.mobile.sdk.cordova.customregistration;

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.EVENT_ON_CUSTOM_REGISTRATION_COMPLETE_REQUEST;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.EVENT_ON_CUSTOM_REGISTRATION_INIT_REQUEST;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomTwoStepRegistrationAction;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback;
import com.onegini.mobile.sdk.android.model.entity.CustomInfo;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;

public class CustomTwoStepRegistrationAction implements OneginiCustomTwoStepRegistrationAction, CustomRegistrationAction {

  private CallbackContext callbackContext;
  private OneginiCustomRegistrationCallback callback;
  private String identityProviderId;

  public CustomTwoStepRegistrationAction(final String identityProviderId) {
    this.identityProviderId = identityProviderId;
  }

  @Override
  public void setCallbackContext(final CallbackContext callbackContext) {
    this.callbackContext = callbackContext;
  }

  @Override
  public OneginiCustomRegistrationCallback getCallback() {
    return callback;
  }

  @Override
  public void initRegistration(final OneginiCustomRegistrationCallback oneginiCustomRegistrationCallback, final CustomInfo customInfo) {
    callback = oneginiCustomRegistrationCallback;
    final PluginResult result = new PluginResultBuilder()
        .shouldKeepCallback()
        .withSuccess()
        .withCustomInfo(customInfo)
        .withIdentityProviderId(identityProviderId)
        .withEvent(EVENT_ON_CUSTOM_REGISTRATION_INIT_REQUEST)
        .build();
    callbackContext.sendPluginResult(result);
  }

  @Override
  public void finishRegistration(final OneginiCustomRegistrationCallback oneginiCustomRegistrationCallback, final CustomInfo customInfo) {
    callback = oneginiCustomRegistrationCallback;
    final PluginResult result = new PluginResultBuilder()
        .shouldKeepCallback()
        .withSuccess()
        .withCustomInfo(customInfo)
        .withIdentityProviderId(identityProviderId)
        .withEvent(EVENT_ON_CUSTOM_REGISTRATION_COMPLETE_REQUEST)
        .build();
    callbackContext.sendPluginResult(result);
  }
}
