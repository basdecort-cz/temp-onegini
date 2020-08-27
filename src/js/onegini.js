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
  var user = require('./user');
  var device = require('./device');
  var utils = require('./utils');
  var resource = require('./resource');
  var mobileAuth = require('./mobileAuth');

  function start(options, successCb, failureCb) {
    var promise, callbackResult;
    options = utils.getOptionsWithDefaults(options, {
      secureXhr: false
    });

    if (!successCb) {
      promise = new Promise(function (resolve, reject) {
        successCb = resolve;
        failureCb = reject;
      });
    }

    callbackResult = utils.callbackExec('OneginiClient', 'start', [], function (config) {
          if (options.secureXhr === true) {
            resource.init(config.resourceBaseURL);
          }
          successCb();
        },
        failureCb
    );

    return promise || callbackResult;
  }

  function reset(successCb, failureCb) {
    return utils.promiseOrCallbackExec('OneginiClient', 'reset', [], successCb, failureCb);
  }

  return {
    start: start,
    reset: reset,
    user: user,
    device: device,
    resource: resource,
    mobileAuth: mobileAuth,
    get mobileAuthentication() {
      console.log('Warning: onegini.mobileAuthentication is renamed to onegini.mobileAuth since version 3. The onegini.mobileAuthentication endpoint might be removed in the future.');
      return mobileAuth;
    }
  };
})();
