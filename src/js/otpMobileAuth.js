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

  function OtpMobileAuthHandler(options, client, action) {
    var self = this;
    this.callbacks = {};

    this.callbackActions = {
      accept: function () {
        var options = {
          accept: true
        };
        utils.promiseOrCallbackExec(client, 'replyToChallenge', options, self.callbacks.onSuccess, self.callbacks.onError);
      },

      deny: function () {
        var options = {
          accept: false
        };
        utils.promiseOrCallbackExec(client, 'replyToChallenge', options, self.callbacks.onSuccess, self.callbacks.onError);
      }
    };

    function callSuccessCallback(options) {
      var event = options.pluginEvent;
      delete options.pluginEvent;

      if (self.callbacks[event]) {
        self.callbacks[event](self.callbackActions, options);
      }
    }

    function callErrorCallback(err) {
      if (self.callbacks.onError) {
        self.callbacks.onError(err);
      } else {
        console.warn('Onegini: An Error occurred but no error callback was registered');
        console.error('Onegini: ', err);
      }
    }

    utils.callbackExec(client, action, options, callSuccessCallback, callErrorCallback)
  }

  OtpMobileAuthHandler.prototype.onConfirmationRequest = function (cb) {
    this.callbacks.onConfirmationRequest = cb;
    return this;
  };

  OtpMobileAuthHandler.prototype.onError = function (cb) {
    this.callbacks.onError = cb;
    return this;
  };

  OtpMobileAuthHandler.prototype.onSuccess = function (cb) {
    this.callbacks.onSuccess = cb;
    return this;
  };

  function handleRequest(options) {
    options = utils.getOptionsWithDefaults(options, {}, 'otp');
    if (!options || !options.otp) {
      throw new TypeError("Onegini: missing 'otp' argument for otp.handleRequest");
    }

    return new OtpMobileAuthHandler(options, 'OneginiOtpMobileAuthRequestClient', 'start')
  }

  return {
    handleRequest: handleRequest
  }
})();
