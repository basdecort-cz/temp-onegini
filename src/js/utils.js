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
  var exec = cordova.require('cordova/exec');

  function isArray(obj) {
    return Object.prototype.toString.call(obj) === '[object Array]';
  }

  function promiseOrCallbackExec(serviceName, methodName, args, successCb, failureCb) {
    args = sanitizeCordovaArgs(args);

    if (successCb) {
      return exec(successCb, failureCb, serviceName, methodName, args);
    }
    else {
      return new Promise(function (resolve, reject) {
        exec(resolve, reject, serviceName, methodName, args);
      });
    }
  }

  function callbackExec(serviceName, methodName, args, successCb, failureCb) {
    if (!successCb) {
      throw new TypeError("Onegini: missing argument for method. '" + methodName + "' requires a Success Callback");
    }

    args = sanitizeCordovaArgs(args);
    return exec(successCb, failureCb, serviceName, methodName, args);
  }

  function onceExec(serviceName, methodName, args) {
    args = sanitizeCordovaArgs(args);
    return exec(null, null, serviceName, methodName, args);
  }

  function getOptionsWithDefaults(options, defaults, firstArg) {
    options = options || {};

    if (typeof(options) !== 'object' && firstArg) {
      var value = options;
      options = {};
      options[firstArg] = value;
    }

    for (var key in defaults) {
      options[key] = options[key] || defaults[key];
    }

    return options;
  }

  function sanitizeCordovaArgs(args) {
    if (!args) {
      args = [];
    }
    else if (!isArray(args)) {
      args = [args];
    }

    return args;
  }

  return {
    isArray: isArray,
    promiseOrCallbackExec: promiseOrCallbackExec,
    callbackExec: callbackExec,
    onceExec: onceExec,
    getOptionsWithDefaults: getOptionsWithDefaults
  };
})();