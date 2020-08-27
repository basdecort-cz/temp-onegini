package com.onegini.mobile.sdk.cordova.util;


import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.IDENTITY_PROVIDER_ID;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.IDENTITY_PROVIDER_NAME;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider;

public class IdentityProvidersUtil {

  private IdentityProvidersUtil() {
  }

  public static JSONArray identityProvidersSetToJsonArray(final Set<OneginiIdentityProvider> identityProviders) throws JSONException {
    final JSONArray result = new JSONArray();
    for (final OneginiIdentityProvider identityProvider : identityProviders) {
      final JSONObject item = parseIdentityProviderToJsonObject(identityProvider);
      result.put(item);
    }
    return result;
  }

  private static JSONObject parseIdentityProviderToJsonObject(final OneginiIdentityProvider identityProvider) throws JSONException {
    final JSONObject result = new JSONObject();
    result.put(IDENTITY_PROVIDER_ID, identityProvider.getId());
    result.put(IDENTITY_PROVIDER_NAME, identityProvider.getName());
    return result;
  }
}
