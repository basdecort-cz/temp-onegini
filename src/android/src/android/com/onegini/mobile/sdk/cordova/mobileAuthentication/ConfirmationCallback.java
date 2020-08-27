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

package com.onegini.mobile.sdk.cordova.mobileAuthentication;

import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiAcceptDenyCallback;
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest;

public class ConfirmationCallback extends Callback {
  private final OneginiAcceptDenyCallback acceptDenyCallback;

  public ConfirmationCallback(final OneginiMobileAuthenticationRequest mobileAuthenticationRequest,
                              final OneginiAcceptDenyCallback acceptDenyCallback) {
    super(mobileAuthenticationRequest, Callback.Method.CONFIRMATION);
    this.acceptDenyCallback = acceptDenyCallback;
  }

  public OneginiAcceptDenyCallback getAcceptDenyCallback() {
    return acceptDenyCallback;
  }

}
