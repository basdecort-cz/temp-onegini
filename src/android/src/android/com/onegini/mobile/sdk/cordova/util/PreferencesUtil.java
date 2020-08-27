package com.onegini.mobile.sdk.cordova.util;

import org.apache.cordova.CordovaPreferences;

public class PreferencesUtil {

  private static final String PREF_KEY_WEBVIEW = "oneginiwebview";
  private static final String PREF_WEBVIEW_DISABLED = "disabled";
  private static final String PREF_WEBVIEW_EXTERNAL = "external";

  private final CordovaPreferences preferences;

  public PreferencesUtil(final CordovaPreferences preferences) {
    this.preferences = preferences;
  }

  public boolean isWebViewDisabled() {
    return preferences.getString(PREF_KEY_WEBVIEW, PREF_WEBVIEW_EXTERNAL).equals(PREF_WEBVIEW_DISABLED);
  }
}
