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

import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiFingerprintCallback;
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest;

public class FingerprintCallback extends Callback {
  private final OneginiFingerprintCallback fingerprintCallback;

  public FingerprintCallback(final OneginiMobileAuthenticationRequest mobileAuthenticationRequest,
                             final OneginiFingerprintCallback fingerprintCallback) {
    super(mobileAuthenticationRequest, Method.FINGERPRINT);
    this.fingerprintCallback = fingerprintCallback;
  }

  public OneginiFingerprintCallback getFingerprintCallback() {
    return fingerprintCallback;
  }
}
