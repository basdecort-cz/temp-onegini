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

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_CREATE_PIN_NO_REGISTRATION_IN_PROGRESS;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_ILLEGAL_ARGUMENT;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_NO_SUCH_IDENTITY_PROVIDER;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_CREATE_PIN_NO_REGISTRATION_IN_PROGRESS;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_ILLEGAL_ARGUMENT_PROFILE;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_INVALID_IDENTITY_PROVIDER_ID;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_PLUGIN_INTERNAL_ERROR;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_PROFILE_ID;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_URL;

import java.util.Set;

import com.onegini.mobile.sdk.cordova.util.*;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;
import com.onegini.mobile.sdk.android.client.OneginiClient;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiBrowserRegistrationCallback;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback;
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import com.onegini.mobile.sdk.cordova.OneginiSDK;
import com.onegini.mobile.sdk.cordova.customregistration.CustomIdentityProvider;
import com.onegini.mobile.sdk.cordova.customregistration.CustomIdentityProviderModel;
import com.onegini.mobile.sdk.cordova.customregistration.CustomRegistrationAction;
import com.onegini.mobile.sdk.cordova.handler.BrowserRegistrationRequestHandler;
import com.onegini.mobile.sdk.cordova.handler.CreatePinRequestHandler;
import com.onegini.mobile.sdk.cordova.handler.RegistrationHandler;
import com.onegini.mobile.sdk.cordova.util.ActionArgumentsUtil;
import com.onegini.mobile.sdk.cordova.util.ApplicationConfigurationParser;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;
import com.onegini.mobile.sdk.cordova.util.UserProfileUtil;

@SuppressWarnings("unused")
public class UserRegistrationClient extends CordovaPlugin {

  private static final String ACTION_RESPOND_TO_CUSTOM_REGISTRATION_INIT_REQUEST = "respondToCustomRegistrationInitRequest";
  private static final String ACTION_RESPOND_TO_CUSTOM_REGISTRATION_COMPLETE_REQUEST = "respondToCustomRegistrationCompleteRequest";

  private static final String ACTION_START = "start";
  private static final String ACTION_CREATE_PIN = "createPin";
  private static final String ACTION_GET_USER_PROFILES = "getUserProfiles";
  private static final String ACTION_IS_USER_REGISTERED = "isUserRegistered";
  private static final String ACTION_CANCEL_FLOW = "cancelFlow";
  private static final String ACTION_RESPOND_TO_REGISTRATION_REQUEST = "respondToRegistrationRequest";

  private RegistrationHandler registrationHandler;
  private PreferencesUtil preferencesUtil;

  @Override
  public void initialize(final CordovaInterface cordova, final CordovaWebView webView) {
    super.initialize(cordova, webView);
    preferencesUtil = new PreferencesUtil(preferences);
  }

  @Override
  public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_START.equals(action)) {
      startRegistration(args, callbackContext);
      return true;
    } else if (ACTION_CREATE_PIN.equals(action)) {
      createPin(args, callbackContext);
      return true;
    } else if (ACTION_GET_USER_PROFILES.equals(action)) {
      getUserProfiles(callbackContext);
      return true;
    } else if (ACTION_IS_USER_REGISTERED.equals(action)) {
      isUserRegistered(args, callbackContext);
    } else if (ACTION_CANCEL_FLOW.equals(action)) {
      cancelFlow();
      return true;
    } else if (ACTION_RESPOND_TO_REGISTRATION_REQUEST.equals(action)) {
      respondToRegistrationRequest(args, callbackContext);
      return true;
    } else if (ACTION_RESPOND_TO_CUSTOM_REGISTRATION_INIT_REQUEST.equals(action)) {
      performCustomRegistrationStep(args);
      return true;
    } else if (ACTION_RESPOND_TO_CUSTOM_REGISTRATION_COMPLETE_REQUEST.equals(action)) {
      performCustomRegistrationStep(args);
      return true;
    }
    return false;
  }

  private void performCustomRegistrationStep(final JSONArray args) throws JSONException {
    final String identityProviderId = getIdentityProviderId(args);
    final CustomIdentityProviderModel customIdentityProviderModel =
        new CustomIdentityProviderModel(new ApplicationConfigurationParser(cordova.getActivity().getApplicationContext()));
    final OneginiCustomRegistrationCallback customRegistrationCallback =
        ((CustomRegistrationAction) customIdentityProviderModel.get(identityProviderId).getRegistrationAction()).getCallback();
    if (isStepAcceptedByTheUser(args)) {
      customRegistrationCallback.returnSuccess(getData(args));
    } else {
      customRegistrationCallback.returnError(new Exception("Registration canceled"));
    }
  }

  private String getIdentityProviderId(final JSONArray args) throws JSONException {
    return args.getJSONObject(0).getString("identityProviderId");
  }

  private boolean isStepAcceptedByTheUser(final JSONArray args) throws JSONException {
    return args.getJSONObject(0).getBoolean("accept");
  }

  private String getData(final JSONArray args) {
    try {
      return args.getJSONObject(0).getString("data");
    } catch (JSONException e) {
      return null;
    }
  }

  private void startRegistration(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    final String[] scopes = ActionArgumentsUtil.getScopesFromArguments(args);
    final String identityProviderId = ActionArgumentsUtil.getIdentityProviderIdFromArguments(args);

    BrowserRegistrationRequestHandler.setBrowserRegistrationRequestCallbackContext(callbackContext);
    BrowserRegistrationRequestHandler.setShouldOpenBrowser(shouldOpenBrowserForRegistration());
    CreatePinRequestHandler.getInstance().setOnStartPinCreationCallback(callbackContext);

    final CustomIdentityProvider customIdentityProvider = new CustomIdentityProviderModel(
        new ApplicationConfigurationParser(cordova.getActivity().getApplicationContext())).get(identityProviderId);
    if (customIdentityProvider != null) {
      ((CustomRegistrationAction)customIdentityProvider.getRegistrationAction()).setCallbackContext(callbackContext);
    }

    registrationHandler = new RegistrationHandler(callbackContext);

    cordova.getThreadPool().execute(new Runnable() {
      public void run() {
        try {
          final OneginiIdentityProvider identityProvider = IdentityProvidersClient.getIdentityProviderById(identityProviderId);
          getOneginiClient().getUserClient().registerUser(identityProvider, scopes, registrationHandler);
        } catch (JSONException e) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withError()
              .withPluginError(ERROR_DESCRIPTION_INVALID_IDENTITY_PROVIDER_ID, ERROR_CODE_NO_SUCH_IDENTITY_PROVIDER)
              .build());
        }
      }
    });
  }

  private void createPin(final JSONArray args, final CallbackContext createPinCallbackContext) throws JSONException {
    final String pin = ActionArgumentsUtil.getPinFromArguments(args);
    OneginiPinCallback pinCallback = CreatePinRequestHandler.getInstance().getOneginiPinCallback();
    CreatePinRequestHandler.getInstance().setOnNextPinCreationAttemptCallback(createPinCallbackContext);

    if (pinCallback == null) {
      final PluginResult pluginResult = new PluginResultBuilder()
          .withPluginError(ERROR_DESCRIPTION_CREATE_PIN_NO_REGISTRATION_IN_PROGRESS, ERROR_CODE_CREATE_PIN_NO_REGISTRATION_IN_PROGRESS)
          .build();
      createPinCallbackContext.sendPluginResult(pluginResult);
    } else {
      registrationHandler.setCallbackContext(createPinCallbackContext);
      pinCallback.acceptAuthenticationRequest(pin.toCharArray());
    }
  }

  private void isUserRegistered(final JSONArray args, final CallbackContext callbackContext) {
    final Set<UserProfile> userProfiles = getOneginiClient().getUserClient().getUserProfiles();
    final String userProfileId;
    final PluginResult pluginResult;
    final boolean userIsRegistered;

    try {
      userProfileId = args.getJSONObject(0).getString(PARAM_PROFILE_ID);
    } catch (JSONException e) {
      callbackContext.sendPluginResult(new PluginResultBuilder()
          .withError()
          .withPluginError(ERROR_DESCRIPTION_ILLEGAL_ARGUMENT_PROFILE, ERROR_CODE_ILLEGAL_ARGUMENT)
          .build());

      return;
    }

    userIsRegistered = UserProfileUtil.findUserProfileById(userProfileId, userProfiles) != null;
    pluginResult = new PluginResult(PluginResult.Status.OK, userIsRegistered);
    callbackContext.sendPluginResult(pluginResult);
  }

  private void getUserProfiles(final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      public void run() {
        final Set<UserProfile> userProfiles = getOneginiClient().getUserClient().getUserProfiles();
        final JSONArray resultPayload;

        try {
          resultPayload = UserProfileUtil.profileSetToJSONArray(userProfiles);
        } catch (JSONException e) {
          callbackContext.error(ERROR_DESCRIPTION_PLUGIN_INTERNAL_ERROR + " : " + e.getMessage());
          return;
        }

        callbackContext.success(resultPayload);
      }
    });
  }

  private void respondToRegistrationRequest(final JSONArray args, final CallbackContext callbackContext) {
    final Uri uri;

    try {
      uri = Uri.parse(args.getJSONObject(0).getString(PARAM_URL));
    } catch (JSONException e) {
      callbackContext.sendPluginResult(new PluginResultBuilder()
          .withPluginError("Could not parse the given URL: " + e.getMessage(), ERROR_CODE_ILLEGAL_ARGUMENT)
          .build());

      return;
    }

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        BrowserRegistrationRequestHandler.handleRegistrationCallback(uri);
      }
    });
  }

  private boolean shouldOpenBrowserForRegistration() {
    return !preferencesUtil.isWebViewDisabled();
  }

  private void cancelFlow() {
    final OneginiBrowserRegistrationCallback callback = BrowserRegistrationRequestHandler.getCallback();

    if (callback == null) {
      return;
    }

    callback.denyRegistration();
  }

  private OneginiClient getOneginiClient() {
    return OneginiSDK.getInstance().getOneginiClient(cordova.getActivity().getApplicationContext());
  }
}
