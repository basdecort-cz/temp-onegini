/*
 * Copyright (c) 2016 Onegini B.V.
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
  function CustomEvent(event, params) {
    var customEvent, originalPrevent;

    params = params || {
        bubbles: false,
        cancelable: false,
        detail: undefined
      };

    customEvent = document.createEvent('CustomEvent');
    customEvent.initCustomEvent(event, params.bubbles, params.cancelable, params.detail);
    originalPrevent = customEvent.preventDefault;
    customEvent.preventDefault = function () {
      originalPrevent.call(this);
      try {
        Object.defineProperty(this, 'defaultPrevented', {
          get: function () {
            return true;
          }
        });
      }
      catch (e) {
        this.defaultPrevented = true;
      }
    };

    return customEvent;
  }

  CustomEvent.prototype = window.Event.prototype;

  return CustomEvent;
})();
