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

package com.onegini.mobile.sdk.cordova.util;

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_AUTHENTICATOR_ID;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_AUTHENTICATOR_IS_PREFERRED;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_AUTHENTICATOR_IS_REGISTERED;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_AUTHENTICATOR_NAME;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_AUTHENTICATOR_TYPE;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_PROFILE_ID;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.onegini.mobile.sdk.android.model.OneginiAuthenticator;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class AuthenticatorUtil {

  private final static String AUTHENTICATOR_TYPE_PIN = "PIN";
  private final static String AUTHENTICATOR_TYPE_FINGERPRINT = "Fingerprint";

  public static JSONArray authenticatorSetToJSONArray(final Set<OneginiAuthenticator> authenticatorSet) throws JSONException {
    JSONArray authenticatorJSONArray = new JSONArray();
    for (final OneginiAuthenticator authenticator : authenticatorSet) {
      final JSONObject authenticatorJSON = authenticatorToJSONObject(authenticator);
      authenticatorJSONArray.put(authenticatorJSON);
    }

    return authenticatorJSONArray;
  }

  public static JSONObject authenticatorToJSONObject(final OneginiAuthenticator authenticator) throws JSONException {
    final JSONObject authenticatorJSON = new JSONObject();
    authenticatorJSON.put(PARAM_AUTHENTICATOR_TYPE, authenticatorTypeToString(authenticator.getType()));
    authenticatorJSON.put(PARAM_AUTHENTICATOR_ID, authenticator.getId());
    authenticatorJSON.put(PARAM_AUTHENTICATOR_IS_REGISTERED, authenticator.isRegistered());
    authenticatorJSON.put(PARAM_AUTHENTICATOR_IS_PREFERRED, authenticator.isPreferred());
    authenticatorJSON.put(PARAM_AUTHENTICATOR_NAME, authenticator.getName());
    return authenticatorJSON;
  }

  public static String authenticatorTypeToString(final int authenticatorType) {
    switch (authenticatorType) {
      case 0:
        return AUTHENTICATOR_TYPE_PIN;
      case 1:
        return AUTHENTICATOR_TYPE_FINGERPRINT;
      default:
        return null;
    }
  }

  public static UserProfile getUserProfileFromArguments(final JSONArray args, final Set<UserProfile> registeredUserProfiles) throws JSONException {
    String profileId = args.getJSONObject(0).getString(PARAM_PROFILE_ID);
    return UserProfileUtil.findUserProfileById(profileId, registeredUserProfiles);
  }
}
