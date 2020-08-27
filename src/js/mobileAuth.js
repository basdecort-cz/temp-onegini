/*
 * Copyright (c) 2017 Onegini B.V.
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

module.exports = (function () {
  var utils = require('./utils');
  var push = require('./pushMobileAuth');
  var otp = require('./otpMobileAuth');

  function isUserEnrolled(options, successCb, failureCb) {
    options = utils.getOptionsWithDefaults(options, {}, 'profileId');
    if (!options || !options.profileId) {
      throw new TypeError("Onegini: missing 'profileId' argument for user.mobileAuth.isUserEnrolled");
    }

    return utils.promiseOrCallbackExec('OneginiMobileAuthClient', 'isEnrolled', options, successCb, failureCb);
  }

  function enroll(successCb, failureCb) {
    return utils.promiseOrCallbackExec('OneginiMobileAuthClient', 'enroll', [], successCb, failureCb);
  }

  return {
    isUserEnrolled: isUserEnrolled,
    enroll: enroll,
    push : push,
    otp: otp
  }
})();
