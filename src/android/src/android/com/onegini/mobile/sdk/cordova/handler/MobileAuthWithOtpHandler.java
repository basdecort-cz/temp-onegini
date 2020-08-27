package com.onegini.mobile.sdk.cordova.handler;

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.EVENT_CONFIRMATION_REQUEST;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.EVENT_SUCCESS;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithOtpHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthWithOtpError;
import com.onegini.mobile.sdk.android.handlers.request.OneginiMobileAuthWithOtpRequestHandler;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiAcceptDenyCallback;
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest;
import com.onegini.mobile.sdk.cordova.mobileAuthentication.Callback;
import com.onegini.mobile.sdk.cordova.mobileAuthentication.ConfirmationCallback;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;

public class MobileAuthWithOtpHandler implements OneginiMobileAuthWithOtpHandler, OneginiMobileAuthWithOtpRequestHandler {

  private static MobileAuthWithOtpHandler instance = null;
  private Callback callback;
  private CallbackContext callbackContext;

  private MobileAuthWithOtpHandler() {
  }

  public static MobileAuthWithOtpHandler getInstance() {
    if (instance == null) {
      instance = new MobileAuthWithOtpHandler();
    }

    return instance;
  }

  public void setCallbackContext(final CallbackContext callbackContext) {
    this.callbackContext = callbackContext;
  }

  public Callback getCallback() {
    return callback;
  }

  // *START* Mobile auth with OTP Request Handler methods

  @Override
  public void startAuthentication(final OneginiMobileAuthenticationRequest request,
                                  final OneginiAcceptDenyCallback acceptDenyCallback) {
    this.callback = new ConfirmationCallback(request, acceptDenyCallback);

    PluginResult pluginResult = new PluginResultBuilder()
        .withSuccess()
        .shouldKeepCallback()
        .withEvent(EVENT_CONFIRMATION_REQUEST)
        .withOneginiMobileAuthenticationRequest(request)
        .build();

    sendPluginResult(pluginResult);
  }

  private void sendPluginResult(final PluginResult pluginResult) {
    if (callbackContext != null && !callbackContext.isFinished()) {
      callbackContext.sendPluginResult(pluginResult);
    }
  }

  @Override
  public void finishAuthentication() {
    callback = null;
  }

  // *END* Mobile auth with OTP Request Handler methods

  // *START* Mobile auth with OTP Handler methods

  @Override
  public void onSuccess() {
    final PluginResult pluginResult = new PluginResultBuilder()
        .withSuccess()
        .withEvent(EVENT_SUCCESS)
        .build();

    sendPluginResult(pluginResult);
    this.callbackContext = null;
  }

  @Override
  public void onError(final OneginiMobileAuthWithOtpError error) {
    final PluginResult pluginResult = new PluginResultBuilder()
        .withError()
        .withOneginiError(error).build();

    sendPluginResult(pluginResult);
    this.callbackContext = null;
  }
}
