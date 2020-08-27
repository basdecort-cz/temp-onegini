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

package com.onegini.mobile.sdk.cordova.client;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.onegini.mobile.sdk.android.client.OneginiClient;
import com.onegini.mobile.sdk.cordova.OneginiSDK;
import com.onegini.mobile.sdk.cordova.handler.PinValidationHandler;
import com.onegini.mobile.sdk.cordova.util.ActionArgumentsUtil;

@SuppressWarnings("unused")
public class UserClient extends CordovaPlugin {

  private static final String ACTION_VALIDATE_PIN_WITH_POLICY = "validatePinWithPolicy";

  @Override
  public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_VALIDATE_PIN_WITH_POLICY.equals(action)) {
      validatePinWithPolicy(args, callbackContext);
      return true;
    }

    return false;
  }

  private void validatePinWithPolicy(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    final String pin = ActionArgumentsUtil.getPinFromArguments(args);
    final PinValidationHandler pinValidationHandler = new PinValidationHandler(callbackContext);

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        getOneginiClient().getUserClient().validatePinWithPolicy(pin.toCharArray(), pinValidationHandler);
      }
    });
  }

  private OneginiClient getOneginiClient() {
    return OneginiSDK.getInstance().getOneginiClient(cordova.getActivity().getApplicationContext());
  }
}
