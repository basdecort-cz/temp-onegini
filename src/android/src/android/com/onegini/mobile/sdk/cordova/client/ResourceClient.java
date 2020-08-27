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

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_ILLEGAL_ARGUMENT;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_IO_EXCEPTION;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_PLUGIN_INTERNAL_ERROR;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_INVALID_FETCH_AUTH_METHOD;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.TAG;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static org.apache.cordova.PluginResult.Status.ERROR;
import static org.apache.cordova.PluginResult.Status.OK;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import com.onegini.mobile.sdk.cordova.OneginiSDK;
import com.onegini.mobile.sdk.cordova.model.AuthMethod;
import com.onegini.mobile.sdk.cordova.util.ActionArgumentsUtil;
import com.onegini.mobile.sdk.cordova.util.OkHttpResponseUtil;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("unused")
public class ResourceClient extends CordovaPlugin {

  private static final String ACTION_FETCH = "fetch";

  private static final int RESULT_HEADER_LENGTH = 4;
  private static final String PARAM_STATUS = "status";
  private static final String PARAM_STATUS_TEXT = "statusText";
  private static final String PARAM_HEADERS = "headers";
  private static final String PARAM_AUTH_METHOD = "auth";

  @Override
  public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_FETCH.equals(action)) {
      fetch(args, callbackContext);
      return true;
    }

    return false;
  }

  private void fetch(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    final JSONObject options = args.getJSONObject(0);
    final AuthMethod authMethod = AuthMethod.fromString(options.getString(PARAM_AUTH_METHOD));

    if (authMethod == null) {
      callbackContext.sendPluginResult(new PluginResultBuilder()
          .withPluginError(ERROR_DESCRIPTION_INVALID_FETCH_AUTH_METHOD, ERROR_CODE_ILLEGAL_ARGUMENT)
          .build());

      return;
    }

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        final Request request;

        try {
          final String resourceBaseUrl = getOneginiClient().getConfigModel().getResourceBaseUrl();
          request = ActionArgumentsUtil.getRequestFromArguments(options, resourceBaseUrl);
        } catch (InvalidParameterException e) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withPluginError(e.getMessage(), ERROR_CODE_PLUGIN_INTERNAL_ERROR)
              .build());

          return;
        } catch (IllegalArgumentException e) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withPluginError(e.getMessage(), ERROR_CODE_ILLEGAL_ARGUMENT)
              .build());

          return;
        } catch (JSONException e) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withPluginError(e.getMessage(), ERROR_CODE_ILLEGAL_ARGUMENT)
              .build());
          return;
        }

        final OkHttpClient okClient;
        final Response response;

        switch (authMethod) {
          case ANONYMOUS:
            okClient = getOneginiClient().getDeviceClient().getAnonymousResourceOkHttpClient();
            break;
          case IMPLICIT:
            okClient = getOneginiClient().getUserClient().getImplicitResourceOkHttpClient();
            break;
          case USER:
          default:
            okClient = getOneginiClient().getUserClient().getResourceOkHttpClient();
            break;
        }

        try {
          response = okClient.newCall(request).execute();
        } catch (IOException e) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withPluginError(e.getMessage(), ERROR_CODE_IO_EXCEPTION)
              .build());
          return;
        }

        callbackContext.sendPluginResult(pluginResultFromOkHttpResponse(response));
      }
    });
  }

  private PluginResult pluginResultFromOkHttpResponse(final Response response) {
    final ByteBuffer payloadBuffer;
    final PluginResult.Status resultStatus;
    final JSONObject httpMetadataJSON;
    final byte[] httpMetaData;
    final byte[] httpBodyData;

    resultStatus = response.isSuccessful() ? OK : ERROR;

    try {
      httpMetadataJSON = okHttpResponseMetadataAsJSON(response);
    } catch (Exception e) {
      return new PluginResultBuilder()
          .withPluginError(e.getMessage(), ERROR_CODE_PLUGIN_INTERNAL_ERROR)
          .build();
    }

    httpBodyData = OkHttpResponseUtil.getBodyBytesFromResponse(response);
    httpMetaData = httpMetadataJSON.toString().getBytes();

    payloadBuffer = ByteBuffer.allocate(RESULT_HEADER_LENGTH + httpMetaData.length + httpBodyData.length)
        .order(LITTLE_ENDIAN)
        .putInt(httpMetaData.length)
        .put(httpMetaData)
        .put(httpBodyData);

    return new PluginResult(resultStatus, payloadBuffer.array());
  }

  private JSONObject okHttpResponseMetadataAsJSON(final Response response) {
    final JSONObject responseJSON = new JSONObject();
    final int statusCode = response.code();
    final String statusText = response.message();
    final JSONObject headers = OkHttpResponseUtil.getJsonHeadersFromResponse(response);

    try {
      responseJSON.put(PARAM_STATUS, statusCode);
      responseJSON.put(PARAM_STATUS_TEXT, statusText);
      responseJSON.put(PARAM_HEADERS, headers);
    } catch (JSONException e) {
      Log.e(TAG, "Could not parse http response to JSON object");
    }

    return responseJSON;
  }

  private com.onegini.mobile.sdk.android.client.OneginiClient getOneginiClient() {
    return OneginiSDK.getInstance().getOneginiClient(cordova.getActivity().getApplicationContext());
  }
}
