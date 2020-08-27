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

import org.apache.cordova.CallbackContext;

import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithPushEnrollmentHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthWithPushEnrollmentError;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;

public class MobileAuthWithPushEnrollmentHandler implements OneginiMobileAuthWithPushEnrollmentHandler {

  private CallbackContext callbackContext;

  public MobileAuthWithPushEnrollmentHandler(final CallbackContext callbackContext) {
    this.callbackContext = callbackContext;
  }

  @Override
  public void onSuccess() {
    callbackContext.sendPluginResult(new PluginResultBuilder()
        .withSuccess()
        .build());
  }

  @Override
  public void onError(final OneginiMobileAuthWithPushEnrollmentError error) {
    callbackContext.sendPluginResult(new PluginResultBuilder()
        .withOneginiError(error)
        .build());
  }
}
