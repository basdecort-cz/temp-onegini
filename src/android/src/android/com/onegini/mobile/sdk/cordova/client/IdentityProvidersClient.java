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


import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_PLUGIN_INTERNAL_ERROR;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_PLUGIN_INTERNAL_ERROR;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider;
import com.onegini.mobile.sdk.cordova.OneginiSDK;
import com.onegini.mobile.sdk.cordova.util.IdentityProvidersUtil;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;

@SuppressWarnings("unused")
public class IdentityProvidersClient extends CordovaPlugin {

  private static final String ACTION_GET_IDENTITY_PROVIDERS = "getIdentityProviders";

  private static final Set<OneginiIdentityProvider> cachedIdentityProviders =
      Collections.<OneginiIdentityProvider>synchronizedSet(new HashSet<OneginiIdentityProvider>());

  @Override
  public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_GET_IDENTITY_PROVIDERS.equals(action)) {
      getIdentityProviders(callbackContext);
      return true;
    }
    return false;
  }

  public static OneginiIdentityProvider getIdentityProviderById(final String identityProviderId) throws JSONException {
    if (identityProviderId == null) {
      return null;
    }
    synchronized (cachedIdentityProviders) {
      for (final OneginiIdentityProvider identityProvider : cachedIdentityProviders) {
        if (identityProviderId.equals(identityProvider.getId())) {
          return identityProvider;
        }
      }
    }
    return null;
  }

  private void getIdentityProviders(final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        try {
            cachedIdentityProviders.clear();
            cachedIdentityProviders.addAll(getOneginiClient().getUserClient().getIdentityProviders());
            final JSONArray parsedIdentityProviders = IdentityProvidersUtil.identityProvidersSetToJsonArray(cachedIdentityProviders);
            callbackContext.success(parsedIdentityProviders);
        } catch (JSONException e) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withPluginError(ERROR_DESCRIPTION_PLUGIN_INTERNAL_ERROR, ERROR_CODE_PLUGIN_INTERNAL_ERROR)
              .build());
        }
      }
    });
  }

  private com.onegini.mobile.sdk.android.client.OneginiClient getOneginiClient() {
    return OneginiSDK.getInstance().getOneginiClient(cordova.getActivity().getApplicationContext());
  }
}
