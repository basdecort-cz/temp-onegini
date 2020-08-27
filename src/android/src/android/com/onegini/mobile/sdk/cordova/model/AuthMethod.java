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

package com.onegini.mobile.sdk.cordova.model;

import android.text.TextUtils;

public enum AuthMethod {
  USER("Symbol(user)"),
  ANONYMOUS("Symbol(anonymous)"),
  IMPLICIT("Symbol(implicit)");

  private final String method;

  AuthMethod(final String method) {
    this.method = method;
  }

  @Override
  public String toString() {
    return method;
  }

  public static AuthMethod fromString(final String method) {
    for (AuthMethod m : values()) {
      if (TextUtils.equals(method, m.toString())) {
        return m;
      }
    }

    return null;
  }
}
