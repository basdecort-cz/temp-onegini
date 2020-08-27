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

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_NO_SUCH_AUTHENTICATOR;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_NO_USER_AUTHENTICATED;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_PLUGIN_INTERNAL_ERROR;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_PROFILE_NOT_REGISTERED;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_NO_SUCH_AUTHENTICATOR;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_NO_USER_AUTHENTICATED;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_PLUGIN_INTERNAL_ERROR;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_PROFILE_NOT_REGISTERED;

import java.util.Set;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.onegini.mobile.sdk.android.model.OneginiAuthenticator;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import com.onegini.mobile.sdk.cordova.OneginiSDK;
import com.onegini.mobile.sdk.cordova.util.ActionArgumentsUtil;
import com.onegini.mobile.sdk.cordova.util.AuthenticatorUtil;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;

@SuppressWarnings("unused")
public class AuthenticatorsClient extends CordovaPlugin {
  private static final String ACTION_GET_REGISTERED_AUTHENTICATORS = "getRegistered";
  private static final String ACTION_GET_ALL_AUTHENTICATORS = "getAll";
  private static final String ACTION_GET_NOT_REGISTERED_AUTHENTICATORS = "getNotRegistered";
  private static final String ACTION_GET_PREFERRED_AUTHENTICATOR = "getPreferred";
  private static final String ACTION_SET_PREFERRED_AUTHENTICATOR = "setPreferred";

  @Override
  public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_GET_REGISTERED_AUTHENTICATORS.equals(action)) {
      getAuthenticators(args, callbackContext, action);
      return true;
    } else if (ACTION_GET_ALL_AUTHENTICATORS.equals(action)) {
      getAuthenticators(args, callbackContext, action);
      return true;
    } else if (ACTION_GET_NOT_REGISTERED_AUTHENTICATORS.equals(action)) {
      getAuthenticators(args, callbackContext, action);
      return true;
    } else if (ACTION_GET_PREFERRED_AUTHENTICATOR.equals(action)) {
      getPreferredAuthenticator(callbackContext);
      return true;
    } else if (ACTION_SET_PREFERRED_AUTHENTICATOR.equals(action)) {
      setPreferredAuthenticator(args, callbackContext);
      return true;
    }

    return false;
  }

  private void getAuthenticators(final JSONArray args, final CallbackContext callbackContext, final String action) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        final Set<UserProfile> registeredUserProfiles = getOneginiClient().getUserClient().getUserProfiles();
        final UserProfile userProfile;
        try {
          userProfile = AuthenticatorUtil.getUserProfileFromArguments(args, registeredUserProfiles);
        } catch (JSONException e) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withPluginError(ERROR_DESCRIPTION_PLUGIN_INTERNAL_ERROR, ERROR_CODE_PLUGIN_INTERNAL_ERROR)
              .build());

          return;
        }

        if (userProfile == null) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withPluginError(ERROR_DESCRIPTION_PROFILE_NOT_REGISTERED, ERROR_CODE_PROFILE_NOT_REGISTERED)
              .build());

          return;
        }

        final Set<OneginiAuthenticator> authenticatorSet;
        if (ACTION_GET_REGISTERED_AUTHENTICATORS.equals(action)) {
          authenticatorSet = getOneginiClient().getUserClient().getRegisteredAuthenticators(userProfile);
        } else if (ACTION_GET_NOT_REGISTERED_AUTHENTICATORS.equals(action)) {
          authenticatorSet = getOneginiClient().getUserClient().getNotRegisteredAuthenticators(userProfile);
        } else if (ACTION_GET_ALL_AUTHENTICATORS.equals(action)) {
          authenticatorSet = getOneginiClient().getUserClient().getAllAuthenticators(userProfile);
        } else {
          return;
        }

        final JSONArray authenticatorJSONArray;
        try {
          authenticatorJSONArray = AuthenticatorUtil.authenticatorSetToJSONArray(authenticatorSet);
        } catch (JSONException e) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withPluginError(ERROR_DESCRIPTION_PLUGIN_INTERNAL_ERROR, ERROR_CODE_PLUGIN_INTERNAL_ERROR)
              .build());

          return;
        }

        callbackContext.success(authenticatorJSONArray);
      }
    });
  }

  private void getPreferredAuthenticator(final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        final UserProfile userProfile = getOneginiClient().getUserClient().getAuthenticatedUserProfile();
        if (userProfile == null) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withPluginError(ERROR_DESCRIPTION_NO_USER_AUTHENTICATED, ERROR_CODE_NO_USER_AUTHENTICATED)
              .build());

          return;
        }

        final OneginiAuthenticator authenticator = getOneginiClient().getUserClient().getPreferredAuthenticator();

        final JSONObject authenticatorJSON;
        try {
          authenticatorJSON = AuthenticatorUtil.authenticatorToJSONObject(authenticator);
        } catch (JSONException e) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withPluginError(ERROR_DESCRIPTION_PLUGIN_INTERNAL_ERROR, ERROR_CODE_PLUGIN_INTERNAL_ERROR)
              .build());

          return;
        }

        callbackContext.success(authenticatorJSON);
      }
    });
  }

  private void setPreferredAuthenticator(final JSONArray args, final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        final UserProfile userProfile = getOneginiClient().getUserClient().getAuthenticatedUserProfile();
        if (userProfile == null) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withPluginError(ERROR_DESCRIPTION_NO_USER_AUTHENTICATED, ERROR_CODE_NO_USER_AUTHENTICATED)
              .build());

          return;
        }

        final Set<OneginiAuthenticator> authenticatorSet = getOneginiClient().getUserClient().getRegisteredAuthenticators(userProfile);

        OneginiAuthenticator authenticator;
        try {
          authenticator = ActionArgumentsUtil.getAuthenticatorFromArguments(args, authenticatorSet);
        } catch (JSONException e) {
          authenticator = null;
        }

        if (authenticator == null) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withPluginError(ERROR_DESCRIPTION_NO_SUCH_AUTHENTICATOR, ERROR_CODE_NO_SUCH_AUTHENTICATOR)
              .build());

          return;
        }

        getOneginiClient().getUserClient().setPreferredAuthenticator(authenticator);

        callbackContext.sendPluginResult(new PluginResultBuilder()
            .withSuccess()
            .build());
      }
    });
  }

  private com.onegini.mobile.sdk.android.client.OneginiClient getOneginiClient() {
    return OneginiSDK.getInstance().getOneginiClient(cordova.getActivity().getApplicationContext());
  }
}
