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

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_PLUGIN_INTERNAL_ERROR;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_PLUGIN_INTERNAL_ERROR;

import java.util.Set;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;

import com.onegini.mobile.sdk.android.handlers.OneginiPendingMobileAuthWithPushRequestsHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiPendingMobileAuthWithPushRequestError;
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthWithPushRequest;
import com.onegini.mobile.sdk.cordova.util.PendingMobileAuthRequestUtil;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;

public class PendingMobileAuthWithPushRequestsHandler implements OneginiPendingMobileAuthWithPushRequestsHandler {

  private CallbackContext callbackContext;

  public PendingMobileAuthWithPushRequestsHandler(final CallbackContext callbackContext) {
    this.callbackContext = callbackContext;
  }

  @Override
  public void onSuccess(final Set<OneginiMobileAuthWithPushRequest> set) {
    try {
      callbackContext.success(PendingMobileAuthRequestUtil.pendingMobileAuthRequestSetToJSONArray(set));
    } catch (JSONException e) {
      callbackContext.sendPluginResult(new PluginResultBuilder()
          .withPluginError(ERROR_DESCRIPTION_PLUGIN_INTERNAL_ERROR, ERROR_CODE_PLUGIN_INTERNAL_ERROR)
          .build());
    }
  }

  @Override
  public void onError(final OneginiPendingMobileAuthWithPushRequestError error) {
    callbackContext.sendPluginResult(new PluginResultBuilder()
        .withOneginiError(error)
        .build());
  }
}
