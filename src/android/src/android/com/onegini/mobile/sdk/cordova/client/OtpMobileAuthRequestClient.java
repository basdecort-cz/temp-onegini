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

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_INVALID_MOBILE_AUTHENTICATION_METHOD;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_NO_USER_AUTHENTICATED;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_INVALID_MOBILE_AUTHENTICATION_METHOD;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_NO_USER_AUTHENTICATED;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_ACCEPT;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_OTP;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import com.onegini.mobile.sdk.cordova.OneginiSDK;
import com.onegini.mobile.sdk.cordova.handler.MobileAuthWithOtpHandler;
import com.onegini.mobile.sdk.cordova.mobileAuthentication.Callback;
import com.onegini.mobile.sdk.cordova.mobileAuthentication.ConfirmationCallback;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;

@SuppressWarnings("unused")
public class OtpMobileAuthRequestClient extends CordovaPlugin {

  private static final String ACTION_START = "start";
  private static final String ACTION_REPLY_TO_CHALLENGE = "replyToChallenge";

  @Override
  public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_START.equals(action)) {
      start(args, callbackContext);
      return true;
    } else if (ACTION_REPLY_TO_CHALLENGE.equals(action)) {
      replyToChallenge(args, callbackContext);
      return true;
    }
    return false;
  }

  private void start(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    final UserProfile userProfile = getOneginiClient().getUserClient().getAuthenticatedUserProfile();

    if (userProfile == null) {
      callbackContext.sendPluginResult(new PluginResultBuilder()
          .withPluginError(ERROR_DESCRIPTION_NO_USER_AUTHENTICATED, ERROR_CODE_NO_USER_AUTHENTICATED)
          .build());

      return;
    }

    final String otp = getOtpFromArgs(args);
    MobileAuthWithOtpHandler.getInstance().setCallbackContext(callbackContext);

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        getOneginiClient().getUserClient().handleMobileAuthWithOtp(otp, MobileAuthWithOtpHandler.getInstance());
      }
    });
  }

  private String getOtpFromArgs(final JSONArray args) throws JSONException {
    return args.getJSONObject(0).getString(PARAM_OTP);
  }

  private void replyToChallenge(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    Callback callback = MobileAuthWithOtpHandler.getInstance().getCallback();
    final boolean shouldAccept = args.getJSONObject(0).getBoolean(PARAM_ACCEPT);

    if (callback == null) {
      return;
    }

    if (callback instanceof ConfirmationCallback) {
      final ConfirmationCallback confirmationCallback = (ConfirmationCallback) callback;
      replyToConfirmationChallenge(confirmationCallback, shouldAccept);
    } else {
      callbackContext.sendPluginResult(new PluginResultBuilder()
          .withPluginError(ERROR_DESCRIPTION_INVALID_MOBILE_AUTHENTICATION_METHOD, ERROR_CODE_INVALID_MOBILE_AUTHENTICATION_METHOD)
          .build());
    }
  }

  private void replyToConfirmationChallenge(final ConfirmationCallback confirmationCallback, final boolean shouldAccept) throws JSONException {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        if (shouldAccept) {
          confirmationCallback.getAcceptDenyCallback().acceptAuthenticationRequest();
        } else {
          confirmationCallback.getAcceptDenyCallback().denyAuthenticationRequest();
        }
      }
    });
  }

  private com.onegini.mobile.sdk.android.client.OneginiClient getOneginiClient() {
    return OneginiSDK.getInstance().getOneginiClient(getApplicationContext());
  }

  private Context getApplicationContext() {
    return cordova.getActivity().getApplicationContext();
  }

}
