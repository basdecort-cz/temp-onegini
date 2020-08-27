/* jshint jasmine: true */

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

exports.defineAutoTests = function () {

  jasmine.DEFAULT_TIMEOUT_INTERVAL = 30000; //set global test timeout to 30 seconds

  var config = {
    testForMultipleAuthenticators: true,
    testForMobileFingerprintAuthentication: false,
    userId: "devnull-cordovatest-" + Math.random().toString().substr(2, 5),
    pin: "12356"
  };

  var registeredProfileId,
    nrOfUserProfiles,
    noop = function () {
    };

  console.log("User ID for this session: " + config.userId);

  if (!config.testForMultipleAuthenticators) {
    console.warn("Testing for multiple authenticators disabled");
  }

  if (!config.testForMobileFingerprintAuthentication) {
    console.warn("Testing for mobile fingerprint authentication disabled (requires interaction)");
  }

  function sendPushMobileAuthRequest(type) {
    sendMobileAuthRequest(type, config.userId)
  }

  function sendOtpMobileAuthRequest(callback) {
    sendMobileAuthRequest('otp', null, callback);
  }

  function sendMobileAuthRequest(type, user, callback) {
    var xhr = new XMLHttpRequest();

    type = type || "push_cordova";

    xhr.open("POST", "https://onegini-msp-snapshot.test.onegini.io/oauth/api/v2/authenticate/user");
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhr.setRequestHeader("Authorization", "Basic MjNBMDIxQTgyNzFGNDdEODUwRTM2Qjc2NDgwMEQ0NjQ0MDM4RUZDODAzMTFGN0U1QjNDMTE4QTgzNTgwOUMwQTpGMkM4MzYwMDJBODVCNEQxMkU5MzRDREFCNEZFRUMwQzk4QkExRjNEMzM2NzM2RkJCNTMxNzE3MzVGMzZCM0Mx");

    xhr.onreadystatechange = function () {
      if (this.readyState === 4) {
        if (this.status !== 200) {
          console.error("Failed to send mobile authentication request!", JSON.parse(this.responseText));
        }
        else {
          if (callback) {
            callback(JSON.parse(this.responseText));
          }
        }
      }
    };

    var data = "callback_uri=https://www.onegini.com&message=Test&type=" + type;
    if (user) {
      data += "&user_id=" + user;
    }
    xhr.send(data);
  }

  function setUrlHandlerUserId(userId, successCb, failureCb) {
    userId = userId || {};
    if (typeof(userId) !== 'object') {
      var value = userId;
      userId = {};
      userId["userId"] = value;
    }
    userId = [userId];

    cordova.exec(successCb, failureCb, "OneginiTestUtils", "setUserId", userId);
  }

  function setWebViewPreference(preferenceValue) {
    cordova.exec(null, null, 'OneginiTestUtils', 'setPreference', ['OneginiWebView', preferenceValue]);
  }

  /******** onegini *********/

  describe('onegini', function () {
    it("should exist", function () {
      expect(window.onegini).toBeDefined();
    });

    describe('start', function () {
      it("should exist", function () {
        expect(onegini.start).toBeDefined();
      });

      it("should run ok", function (done) {
        onegini.start(
          {
            secureXhr: true
          },
          function () {
            expect(true).toBe(true);
            done();
          },
          function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });

      afterAll(function (done) {
        setUrlHandlerUserId(config.userId,
          function () {
            expect(true).toBe(true);
            done();
          },
          function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });
    });
    describe('reset', function () {
      it("should exist", function () {
        expect(onegini.reset).toBeDefined();
      });

      it("should run ok", function (done) {
        onegini.reset(
          function () {
            expect(true).toBe(true);
            done();
          },
          function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });

      afterAll(function (done) {
        setUrlHandlerUserId(config.userId,
          function () {
            expect(true).toBe(true);
            done();
          },
          function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });
    });
  });

  /******** onegini.user (1/2) *********/

  describe('onegini.user', function () {
    it("should exist", function () {
      expect(onegini.user).toBeDefined();
    });

    describe("validatePinWithPolicy", function () {
      it("should exist", function () {
        expect(onegini.user.validatePinWithPolicy).toBeDefined();
      });

      it("should fail because of an incorrect length", function (done) {
        onegini.user.validatePinWithPolicy(
          {
            pin: "incorrect"
          },
          function (result) {
            expect(result).toBeUndefined();
          },
          function (err) {
            expect(err).toBeDefined();
            expect(err.code).toBe(9014);
            done();
          });
      });

      it("should fail because of repeating numbers", function (done) {
        onegini.user.validatePinWithPolicy(
          {
            pin: "11111"
          },
          function (result) {
            expect(result).toBeUndefined();
          },
          function (err) {
            expect(err).toBeDefined();
            expect(err.code).toBe(9013);
            done();
          });
      });

      it("should succeed if pin is compliant to policy", function (done) {
        onegini.user.validatePinWithPolicy(
          {
            pin: config.pin
          },
          function () {
            expect(true).toBe(true);
            done();
          },
          function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });
    });

    describe("getAuthenticatedUserProfile (1/3)", function () {
      it("should exist", function () {
        expect(onegini.user.getAuthenticatedUserProfile).toBeDefined();
      });

      it("should fail", function (done) {
        onegini.user.getAuthenticatedUserProfile(
          function (result) {
            expect(result).toBeUndefined();
          },
          function (err) {
            expect(err).toBeDefined();
            expect(err.description).toBe("Onegini: No user authenticated.");
            done();
          });
      });
    });

    describe("register", function () {
      it("should exist", function () {
        expect(onegini.user.register).toBeDefined();
      });

      it("should be cancellable", function (done) {
        setWebViewPreference('default');
        onegini.user.register()
          .onCreatePinRequest(function (actions) {
            actions.cancel();
          })
          .onError(function (err) {
            expect(err).toBeDefined();
            expect(err.code).toBe(9006);
            setTimeout(function () {
              done();
            }, 500);
          })
          .onSuccess(function () {
            fail("Registration should have failed, but succeeded");
            done();
          });
      });

      it("should work with callback action through JS", function (done) {
        setWebViewPreference('disabled');
        onegini.user.register()
          .onRegistrationRequest(function (actions, options) {
            cordova.exec(function (registrationUrl) {
              actions.handleRegistrationUrl(registrationUrl)
            }, function (err) {
              expect(err).toBeUndefined();
              fail("Error while getting redirect url");
            }, "OneginiTestUtils", "getRedirectUrl", [options]);
          })
          .onCreatePinRequest(function (actions, options) {
            expect(options.profileId).toBeDefined();
            expect(options.pinLength).toBe(5);
            registeredProfileId = options.profileId;
            actions.createPin(config.pin);
          })
          .onSuccess(function () {
            setTimeout(function () {
              done();
            }, 100)
          })
          .onError(function (err) {
            fail("Registration failed, but should have succeeded");
            expect(err).toBeUndefined();
          });
      });

      it("should work with default WebView", function (done) {
        setWebViewPreference('default');
        onegini.user.register()
          .onCreatePinRequest(function (actions, options) {
            expect(options.profileId).toBeDefined();
            expect(options.pinLength).toBe(5);
            registeredProfileId = options.profileId;
            actions.createPin(config.pin);
          })
          .onSuccess(function () {
            done();
          })
          .onError(function (err) {
            fail("Registration failed, but should have succeeded");
            expect(err).toBeUndefined();
          });
      });

      if (cordova.platformId === "ios") {
        it("should work with SFSafariViewController", function (done) {
          setWebViewPreference('SFSafariViewController');
          onegini.user.register()
            .onCreatePinRequest(function (actions, options) {
              expect(options.profileId).toBeDefined();
              expect(options.pinLength).toBe(5);
              registeredProfileId = options.profileId;
              actions.createPin(config.pin);
            })
            .onSuccess(function () {
              done();
            })
            .onError(function (err) {
              fail("Registration failed, but should have succeeded");
              expect(err).toBeUndefined();
            });
        });
      }
    });

    describe("getAuthenticatedUserProfile (2/3)", function () {
      it("should succeed", function (done) {
        onegini.user.getAuthenticatedUserProfile(
          function (result) {
            expect(result).toBeDefined();
            expect(result.profileId).toEqual(registeredProfileId);
            done();
          },
          function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });
    });

    describe('getUserProfiles (1/2)', function () {
      it("should exist", function () {
        expect(onegini.user.getUserProfiles).toBeDefined();
      });

      it("should not be empty", function (done) {
        onegini.user.getUserProfiles(
          function (result) {
            expect(result).toBeDefined();
            nrOfUserProfiles = result.length;
            expect(result[0]).toBeDefined();
            expect(result[0].profileId).toBeDefined();
            done();
          },
          function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });
    });

    describe('logout', function () {
      it("should exist", function () {
        expect(onegini.user.logout).toBeDefined();
      });

      it("should succeed", function (done) {
        onegini.user.logout(
          function () {
            expect(true).toBe(true);
            done();
          },
          function (err) {
            expect(err).toBeUndefined();
            fail('Logout failed, but should have succeeded');
          });
      });
    });

    describe('implicit authentication', function () {
      describe('user.authenticateImplicitly', function () {
        it('should exist', function () {
          expect(onegini.user.authenticateImplicitly).toBeDefined();
        });

        it('should require a profile ID', function () {
          expect(function () {
            onegini.user.authenticateImplicitly();
          }).toThrow(new TypeError("Onegini: missing 'profileId' argument for user.authenticateImplicitly"));
        });

        it('should succeed', function (done) {
          onegini.user.authenticateImplicitly({
            profileId: registeredProfileId,
            scopes: ['read']
          }, function () {
            expect(true).toBe(true);
            done();
          }, function () {
            fail('Error callback called, but method should have succeeded.');
          });
        });
      });

      describe('user.getImplicitlyAuthenticatedUserProfile', function () {
        it('should exist', function () {
          expect(onegini.user.authenticateImplicitly).toBeDefined();
        });

        it('should return the implicitly authenticated user', function (done) {
          onegini.user.getImplicitlyAuthenticatedUserProfile(function (result) {
            expect(result).toBeDefined();
            expect(result.profileId).toEqual(registeredProfileId);
            done();
          }, function () {
            fail('Error callback called, but method should have succeeded');
          });
        });
      });
    });

    describe("authenticators (1/2)", function () {
      it("should have a getAll method", function () {
        expect(onegini.user.authenticators.getAll).toBeDefined();
      });

      it("should have a getRegistered method", function () {
        expect(onegini.user.authenticators.getRegistered).toBeDefined();
      });

      it("should have a getNotRegistered method", function () {
        expect(onegini.user.authenticators.getNotRegistered).toBeDefined();
      });

      it("should have a getPreferred method", function () {
        expect(onegini.user.authenticators.getPreferred).toBeDefined();
      });

      it("should have a setPreferred method", function () {
        expect(onegini.user.authenticators.setPreferred).toBeDefined();
      });

      it("should have a registerNew method", function () {
        expect(onegini.user.authenticators.registerNew).toBeDefined();
      });

      it("should have a deregister method", function () {
        expect(onegini.user.authenticators.deregister).toBeDefined();
      });

      describe("getAll", function () {
        it("should return an error when profile not registered", function (done) {
          onegini.user.authenticators.getAll(
            {
              profileId: "dummy"
            },
            function (result) {
              expect(result).toBeUndefined();
            },
            function (err) {
              expect(err).toBeDefined();
              expect(err.description).toBe("Onegini: No registered user found.");
              done();
            });
        });
      });

      describe("getRegistered", function () {
        it("should return an error when profile not registered", function (done) {
          onegini.user.authenticators.getRegistered(
            {
              profileId: "dummy"
            },
            function (result) {
              expect(result).toBeUndefined();
            },
            function (err) {
              expect(err).toBeDefined();
              expect(err.description).toBe("Onegini: No registered user found.");
              done();
            });
        });
      });

      describe("getNotRegistered", function () {
        it("should return an error when profile not registered", function (done) {
          onegini.user.authenticators.getNotRegistered(
            {
              profileId: "dummy"
            },
            function (result) {
              expect(result).toBeUndefined();
            },
            function (err) {
              expect(err).toBeDefined();
              expect(err.description).toBe("Onegini: No registered user found.");
              done();
            });
        });
      });

      describe("getPreferred", function () {
        it("should return an error when not logged in", function (done) {
          onegini.user.authenticators.getPreferred(
            function (result) {
              expect(result).toBeUndefined();
            },
            function (err) {
              expect(err).toBeDefined();
              expect(err.description).toBe("Onegini: No user authenticated.");
              done();
            });
        });
      });

      describe("setPreferred", function () {
        it("should return an error when not logged in", function (done) {
          onegini.user.authenticators.setPreferred({
              authenticatorType: "PIN"
            },
            function () {
              fail("Success callbacks was called, but method should have failed");
            },
            function (err) {
              expect(err).toBeDefined();
              expect(err.description).toBe("Onegini: No user authenticated.");
              done();
            });
        });
      });

      describe("registerNew", function () {
        it("should exist", function () {
          expect(onegini.user.authenticators.registerNew).toBeDefined();
        });

        it("should require an authenticatorType argument", function () {
          expect(function () {
            onegini.user.authenticators.registerNew()
          }).toThrow(new TypeError("Onegini: missing 'authenticatorType' argument for authenticators.registerNew"));
        });

        it("should return an error when not logged in", function (done) {
          onegini.user.authenticators.registerNew('dummy')
            .onError(function (err) {
              expect(err).toBeDefined();
              expect(err.description).toBe("Onegini: No user authenticated.");
              done();
            })
        });
      });

      describe("deregister", function () {
        it("should require an authenticatorType", function () {
          expect(function () {
            onegini.user.authenticators.deregister()
          }).toThrow(new TypeError("Onegini: missing 'authenticatorType' argument for authenticators.deregister"));
        });

        it("should return an error when not logged in", function (done) {
          onegini.user.authenticators.deregister(
            {
              authenticatorType: 1
            },
            function () {
              fail("Success callback was called, but method should have failed");
            },
            function (err) {
              expect(err).toBeDefined();
              expect(err.description).toBe("Onegini: No user authenticated.");
              done();
            });
        });
      });
    });

    describe("mobileAuth (1/3)", function () {
      it("mobileAuth should equal mobileAuthentication", function () {
        expect(onegini.mobileAuth === onegini.mobileAuthentication).toBe(true);
      });
      describe("isUserEnrolled", function () {
        it("should exist", function () {
          expect(onegini.mobileAuth.isUserEnrolled).toBeDefined();
        });

        it("'profileId' argument mandatory", function () {
          expect(function () {
            onegini.mobileAuth.isUserEnrolled({},
              function () {
              }, function () {
              });
          }).toThrow(new TypeError("Onegini: missing 'profileId' argument for user.mobileAuth.isUserEnrolled"));
        });

        it("should return not enrolled", function (done) {
          onegini.mobileAuth.isUserEnrolled({
              profileId: registeredProfileId
            },
            function (result) {
              expect(result).toBe(false);
              done();
            },
            function (err) {
              expect(err).toBeUndefined();
              fail('Error callback called, but method should have succeeded');
            });
        });
      });

      describe("enroll", function () {
        it("should exist", function () {
          expect(onegini.mobileAuth.enroll).toBeDefined();
        });

        it("should return an error when not logged in", function (done) {
          onegini.mobileAuth.enroll(
            function (result) {
              expect(result).toBeUndefined();
            },
            function (err) {
              expect(err).toBeDefined();
              expect(err.description).toBe("Onegini: No user authenticated.");
              done();
            });
        });
      });

      describe("push", function () {
        it("should exist", function () {
          expect(onegini.mobileAuth.push).toBeDefined();
        });

        describe("isUserEnrolled", function () {
          it("should exist", function () {
            expect(onegini.mobileAuth.push.isUserEnrolled).toBeDefined();
          });

          it("'profileId' argument mandatory", function () {
            expect(function () {
              onegini.mobileAuth.push.isUserEnrolled({}, function () {
              }, function () {
              });
            }).toThrow(new TypeError("Onegini: missing 'profileId' argument for user.mobileAuth.push.isUserEnrolled"));
          });

          it("should return not enrolled", function (done) {
            onegini.mobileAuth.push.isUserEnrolled({
                profileId: registeredProfileId
              },
              function (result) {
                expect(result).toBe(false);
                done();
              },
              function (err) {
                expect(err).toBeUndefined();
                fail('Error callback called, but method should have succeeded');
              });
          });
        });

        describe("enroll", function () {
          it("should exist", function () {
            expect(onegini.mobileAuth.push.enroll).toBeDefined();
          });

          it("should return an error when not logged in", function (done) {
            onegini.mobileAuth.push.enroll(
              function (result) {
                expect(result).toBeUndefined();
              },
              function (err) {
                expect(err).toBeDefined();
                expect(err.description).toBe("Onegini: No user authenticated.");
                done();
              });
          });
        });
        describe("getPendingMobileAuth", function () {
          it("should exist", function () {
            expect(onegini.mobileAuth.push.getPendingRequests).toBeDefined();
          });
          it("should return pending mobile auth requests", function (done) {
            onegini.mobileAuth.push.getPendingRequests(
              function (result) {
                expect(result).toBeDefined();
                done();
              },
              function (err) {
                expect(err).toBeUndefined();
                fail('Error callback called, but method should have succeeded');
              });
            });
          });
      });
    });

    describe("authenticate", function () {
      it("should exist", function () {
        expect(onegini.user.authenticate).toBeDefined();
      });

      it("should require a profile ID", function () {
        expect(function () {
          onegini.user.authenticate();
        }).toThrow(new TypeError("Onegini: missing 'profileId' argument for user.authenticate"));
      });

      it("should be cancellable", function (done) {
        onegini.user.authenticate(registeredProfileId)
          .onPinRequest(function (actions, options) {
            actions.cancel();
          })
          .onError(function (err) {
            expect(err).toBeDefined();
            expect(err.code).toBe(9006);
            done();
          })
          .onSuccess(function () {
            fail("Authentication should have failed, but succeeded");
            done();
          });
      });

      it("should require an authenticatorType if a second argument is given", function () {
        expect(function () {
          onegini.user.authenticate({ profileId: registeredProfileId }, { foo: 'bar' })
        }).toThrow(new TypeError("Onegini: missing 'authenticatorType' argument for user.authenticate"));
      });

      it("should succeed with pin authentication", function (done) {
        onegini.user.authenticate(registeredProfileId)
          .onPinRequest(function (actions, options) {
            expect(actions).toBeDefined();
            expect(actions.providePin).toBeDefined();
            expect(options).toBeDefined();

            if (options.remainingFailureCount === options.maxFailureCount - 1) {
              expect(options.code).toBe(8012);
              expect(options.description).toBeDefined();
              actions.providePin(config.pin);
            }
            else {
              expect(options.remainingFailureCount).toBeDefined();
              expect(options.maxFailureCount).toBeDefined();
              expect(options.remainingFailureCount).toBe(3);
              expect(options.maxFailureCount).toBe(3);
              actions.providePin('incorrect');
            }
          })
          .onSuccess(function () {
            done();
          })
          .onError(function (err) {
            expect(err).toBeUndefined();
            fail("User authentication failed, but should have succeeded");
          });
      });


    });

    describe("mobileAuth (2/3)", function () {
      describe("enroll", function () {
        it("Should succeed", function (done) {
          onegini.mobileAuth.enroll(
            function () {
              expect(true).toBe(true);
              done();
            },
            function (err) {
              expect(err).toBeUndefined();
              fail("Error callback was called, but method should have succeeded");
            });
        });
      });

      describe("isUserEnrolled", function () {
        it("should return enrolled", function (done) {
          onegini.mobileAuth.isUserEnrolled({
              profileId: registeredProfileId
            },
            function (result) {
              expect(result).toBe(true);
              done();
            },
            function (err) {
              expect(err).toBeUndefined();
              fail('Error callback called, but method should have succeeded');
            });
        });
      });

      describe("mobileAuth.otp", function () {
        it("should exist", function () {
          expect(onegini.mobileAuth.otp).toBeDefined();
        });

        describe("handleRequest", function () {
          it("should exist", function () {
            expect(onegini.mobileAuth.otp.handleRequest).toBeDefined();
          });

          it("should accept an OTP confirmation request", function (done) {
            sendOtpMobileAuthRequest(function (data) {
              var otp = data.otp;

              onegini.mobileAuth.otp.handleRequest({
                otp: otp
              })
                .onConfirmationRequest(function (actions, request) {
                  expect(request.type).toBeDefined();
                  expect(request.message).toBeDefined();
                  expect(request.profileId).toBeDefined();
                  actions.accept();
                })
                .onError(function () {
                  fail("OTP mobile authentication request failed, but should have succeeded");
                })
                .onSuccess(function () {
                  done();
                });
            });
          });

          it("should reject an OTP confirmation request", function (done) {
            sendOtpMobileAuthRequest(function (data) {
              var otp = data.otp;

              onegini.mobileAuth.otp.handleRequest({
                otp: otp
              })
                .onConfirmationRequest(function (actions, request) {
                  expect(request.type).toBeDefined();
                  expect(request.message).toBeDefined();
                  expect(request.profileId).toBeDefined();
                  actions.deny();
                })
                .onError(function () {
                  done();
                })
                .onSuccess(function () {
                  fail("OTP mobile authentication request succeeded, but should have failed");
                });
            });
          });
        });
      });

      describe("mobileAuth.push (2/3)", function () {
        describe("enroll", function () {
          it("Should succeed", function (done) {
            onegini.mobileAuth.push.enroll(
              function () {
                expect(true).toBe(true);
                done();
              },
              function (err) {
                expect(err).toBeUndefined();
                fail("Error callback was called, but method should have succeeded");
              });
          });
        });

        describe("isUserEnrolled", function () {
          it("should return enrolled", function (done) {
            onegini.mobileAuth.push.isUserEnrolled({
                profileId: registeredProfileId
              },
              function (result) {
                expect(result).toBe(true);
                done();
              },
              function (err) {
                expect(err).toBeUndefined();
                fail('Error callback called, but method should have succeeded');
              });
          });
        });
      });

      describe("on", function () {
        it('should exist', function () {
          expect(onegini.mobileAuth.push.on).toBeDefined();
        });

        it('should accept a push confirmation request', function (done) {
          onegini.mobileAuth.push.on("confirmation")
            .onConfirmationRequest(function (actions, request) {
              expect(request.type).toBeDefined();
              expect(request.message).toBeDefined();
              expect(request.profileId).toBeDefined();
              actions.accept();
            })
            .onError(function () {
              fail("Mobile authentication request failed, but should have succeeded");
            })
            .onSuccess(function () {
              done();
            });

          sendPushMobileAuthRequest();
        });

        it('should reject a push confirmation request', function (done) {
          onegini.mobileAuth.push.on("confirmation")
            .onConfirmationRequest(function (actions, request) {
              expect(request.type).toBeDefined();
              expect(request.message).toBeDefined();
              expect(request.profileId).toBeDefined();
              actions.deny();
            })
            .onSuccess(function () {
              fail("Mobile authentication request succeeded, but should have failed");
            })
            .onError(function () {
              done();
            });

          sendPushMobileAuthRequest();
        });

        it('should be able to handle multiple requests', function (done) {
          var counter = 0;

          onegini.mobileAuth.push.on("confirmation")
            .onConfirmationRequest(function (actions, request) {
              expect(request.type).toBeDefined();
              expect(request.message).toBeDefined();
              expect(request.profileId).toBeDefined();
              actions.accept();
            })
            .onError(function () {
              fail('Mobile authentication request failed, but should have succeeded');
            })
            .onSuccess(function () {
              counter++;
              if (counter === 2) {
                done();
              }
            });

          sendPushMobileAuthRequest();
          sendPushMobileAuthRequest();
        });

        it('should accept a mobile pin request', function (done) {
          onegini.mobileAuth.push.on("pin")
            .onPinRequest(function (actions, request) {
              expect(request.type).toBeDefined();
              expect(request.message).toBeDefined();
              expect(request.profileId).toBeDefined();
              expect(request.maxFailureCount).toBeDefined();
              expect(request.remainingFailureCount).toBeDefined();

              if (request.remainingFailureCount === request.maxFailureCount - 1) {
                expect(request.code).toBe(8012);
                expect(request.description).toBeDefined();
                actions.accept(config.pin);
              } else {
                expect(request.code).toBeUndefined();
                expect(request.description).toBeUndefined();
                actions.accept('invalid');
              }
            })
            .onError(function () {
              fail('Mobile authentication request failed, but should have succeeded');
            })
            .onSuccess(function () {
              done();
            });

          sendPushMobileAuthRequest("push_with_pin_cordova");
        }, 10000);

        it('should reject a mobile pin request', function (done) {
          onegini.mobileAuth.push.on("pin")
            .onPinRequest(function (actions, request) {
              expect(request.type).toBeDefined();
              expect(request.message).toBeDefined();
              expect(request.profileId).toBeDefined();
              expect(request.maxFailureCount).toBeDefined();
              expect(request.remainingFailureCount).toBeDefined();
              actions.deny();
            })
            .onError(function () {
              done();
            })
            .onSuccess(function () {
              fail('Mobile authentication request succeeded, but should have failed');
            });

          sendPushMobileAuthRequest("push_with_pin_cordova");
        });
      });
    });

    describe("authenticators (2/3)", function () {
      describe("setPreferred", function () {
        it("Should fail with a non-existing authenticator", function (done) {
          onegini.user.authenticators.setPreferred({
              authenticatorType: "invalid"
            }, function () {
              expect(true).toBe(true);
              fail("Success callback called, but method should have failed.");
            },
            function (err) {
              expect(err).toBeDefined();
              expect(err.description).toBe("Onegini: No such authenticator found");
              done();
            });
        });
      });

      describe("deregister", function () {
        it("Should fail with a non-existing authenticator", function (done) {
          onegini.user.authenticators.deregister(
            {
              authenticatorType: "invalid"
            },
            function (result) {
              expect(result).toBeUndefined();
            },
            function (err) {
              expect(err).toBeDefined();
              expect(err.description).toBe("Onegini: No such authenticator found");
              done();
            });
        });
      });

      describe("getAll", function () {
        it("should contain PIN and fingerprint authenticator (if available)", function (done) {
          var foundPin = false,
            foundFingerprint = false,
            shouldFindPin = true,
            shouldFindFingerprint = config.testForMultipleAuthenticators;


          onegini.user.authenticators.getAll(
            {
              profileId: registeredProfileId
            },
            function (result) {
              expect(result).toBeDefined();

              for (var r in result) {
                var authenticator = result[r];
                expect(authenticator.authenticatorType).toBeDefined();
                expect(authenticator.authenticatorId).toBeDefined();
                expect(authenticator.isPreferred).toBeDefined();
                expect(authenticator.isRegistered).toBeDefined();
                expect(authenticator.name).toBeDefined();
                if (authenticator.authenticatorType === "PIN") {
                  foundPin = true;
                }
                else if (authenticator.authenticatorType === "Fingerprint") {
                  foundFingerprint = true;
                }
              }

              expect(foundPin).toBe(shouldFindPin);
              expect(foundFingerprint).toBe(shouldFindFingerprint);
              done();
            },
            function (err) {
              expect(err).toBeUndefined();
              fail("Method failed, but should have succeeded");
            }
          );
        });
      });

      describe('getRegistered', function () {
        it("should contain a PIN authenticator", function (done) {
          onegini.user.authenticators.getRegistered(
            {
              profileId: registeredProfileId
            },
            function (result) {
              expect(result).toBeDefined();
              var nrOfAuthenticators = result.length;
              expect(nrOfAuthenticators).toBeGreaterThan(0);

              for (var r in result) {
                var authenticator = result[r];
                expect(authenticator.authenticatorType).toBeDefined();
                expect(authenticator.authenticatorId).toBeDefined();
                expect(authenticator.isPreferred).toBe(true);
                expect(authenticator.isRegistered).toBeDefined();
                expect(authenticator.name).toBeDefined();
                if (authenticator.authenticatorType === "PIN") {
                  done();
                  return;
                }
              }
              fail("Expected PIN Authenticator not found");
              done();
            },
            function (err) {
              expect(err).toBeUndefined();
              fail('Error callback called, but method should have succeeded');
            });
        });
      });

      describe('getNotRegistered', function () {
        it("should succeed", function (done) {
          onegini.user.authenticators.getNotRegistered(
            {
              profileId: registeredProfileId
            },
            function (result) {
              expect(result).toBeDefined();
              for (var r in result) {
                var authenticator = result[r];
                expect(authenticator.authenticatorType).toBeDefined();
                expect(authenticator.authenticatorId).toBeDefined();
                expect(authenticator.isPreferred).toBe(false);
                expect(authenticator.isRegistered).toBeDefined();
                expect(authenticator.name).toBeDefined();
              }
              done();
            },
            function (err) {
              expect(err).toBeUndefined();
              fail('Error callback called, but method should have succeeded');
            });
        });
      });

      describe("getPreferred", function () {
        it("Should succeed and be default PIN authenticator", function (done) {
          onegini.user.authenticators.getPreferred(
            function (result) {
              expect(result).toBeDefined();
              expect(result.authenticatorType).toBe("PIN");
              expect(result.authenticatorId).toBeDefined();
              done();
            },
            function (err) {
              expect(err).toBeUndefined();
              fail("Error callback called, but method should have succeeded");
            });
        });
      });

      if (config.testForMultipleAuthenticators) {
        describe("registerNew", function () {
          it("should be cancellable", function (done) {
            onegini.user.authenticators.registerNew({ authenticatorType: "Fingerprint" })
              .onPinRequest(function (actions) {
                actions.cancel();
              })
              .onError(function (err) {
                expect(err).toBeDefined();
                expect(err.code).toBe(9006);
                done();
              });
          });

          it("should succeed", function (done) {
            onegini.user.authenticators.registerNew({ authenticatorType: "Fingerprint" })
              .onPinRequest(function (actions, options) {
                expect(actions).toBeDefined();
                expect(actions.providePin).toBeDefined();
                expect(options).toBeDefined();

                if (options.remainingFailureCount === options.maxFailureCount - 1) {
                  expect(options.code).toBe(8012);
                  expect(options.description).toBeDefined();
                  actions.providePin(config.pin);
                }
                else {
                  expect(options.remainingFailureCount).toBeDefined();
                  expect(options.maxFailureCount).toBeDefined();
                  expect(options.remainingFailureCount).toBe(3);
                  expect(options.maxFailureCount).toBe(3);
                  actions.providePin('incorrect');
                }
              })
              .onSuccess(function () {
                expect(true).toBe(true);
                done();
              })
              .onError(function (err) {
                expect(err).toBeUndefined();
                fail('Authenticator registration failed, but should have succeeded');
              });
          });
        });

        describe("user.authenticate with fingerprint", function () {
          afterAll(function (done) {
            onegini.user.authenticate(registeredProfileId, 'PIN')
              .onPinRequest(function (actions) {
                actions.providePin(config.pin);
              })
              .onSuccess(done);
          });

          it("should respect the authenticator argument", function (done) {
            onegini.user.authenticate({ profileId: registeredProfileId }, { authenticatorType: 'Fingerprint' })
              .onPinRequest(function () {
                fail('PIN request called, but should have been fingerprint');
              })
              .onFingerprintRequest(function (actions, request) {
                expect(actions).toBeDefined();
                expect(request).toBeDefined();
                actions.denyFingerprint();
              })
              .onSuccess(function() {
                fail();
              })
              .onError(function(err) {
                expect(err).toBeDefined();
                expect(err.code).toBe(9006);
                done();
              })
          });
        });

        describe("setPreferred", function () {
          it("Should succeed with an existing authenticator", function (done) {
            onegini.user.authenticators.setPreferred(
              {
                authenticatorType: "PIN"
              }, function () {
                expect(true).toBe(true);
                done();
              },
              function (err) {
                expect(err).toBeUndefined();
                fail("Error callback called, but method should have failed.");
              });
          });
        });

        describe("deregister", function () {
          it("Should succeed with existing fingerprint authenticator", function (done) {
            onegini.user.authenticators.deregister(
              {
                authenticatorType: "Fingerprint"
              }, function () {
                expect(true).toBe(true);
                done();
              }, function (err) {
                expect(err).toBeUndefined();
                fail("Error callback called, but method should have failed.");
              });
          });
        });
      }
      else {
        console.warn("Skipping authenticators (2/2). Multiple authenticator tests disabled");
      }
    });

    if (config.testForMobileFingerprintAuthentication) {
      describe("mobileAuth.push (4/4)", function () {
        describe("register Fingerprint authenticator", function () {
          it("should succeed", function (done) {
            onegini.user.authenticators.registerNew({ authenticatorType: "Fingerprint" })
              .onPinRequest(function (actions) {
                actions.providePin(config.pin);
              })
              .onSuccess(function () {
                expect(true).toBe(true);
                done();
              })
              .onError(function (err) {
                expect(err).toBeUndefined();
                fail('Authenticator registration failed, but should have suceeded');
              });
          });
        });
        describe("on", function () {
          it("Should accept a mobile fingerprint request", function (done) {
            onegini.mobileAuth.push.on("fingerprint")
              .onFingerprintRequest(function (actions, request) {
                console.log("Please provide correct fingerprint");
                expect(request.type).toBeDefined();
                expect(request.message).toBeDefined();
                expect(request.profileId).toBeDefined();
                actions.accept();
              })
              .onError(function () {
                fail("Mobile authentication request failed, but should have succeeded");
              })
              .onSuccess(function () {
                done();
              });

            sendPushMobileAuthRequest("push_with_fingerprint_cordova");
          });

          it("Should reject a mobile fingerprint request", function (done) {
            onegini.mobileAuth.push.on("fingerprint")
              .onFingerprintRequest(function (actions, request) {
                expect(request.type).toBeDefined();
                expect(request.message).toBeDefined();
                expect(request.profileId).toBeDefined();
                actions.deny();
              })
              .onError(function () {
                done();
              })
              .onSuccess(function () {
                fail("Mobile authentication request succeeded, but should have failed");
              });

            sendPushMobileAuthRequest("push_with_fingerprint_cordova");
          });

          if (cordova.platformId === "android") {
            it("Should be notified on fingerprint captured", function (done) {
              var didCallCaptured = false;
              onegini.mobileAuth.push.on("fingerprint")
                .onFingerprintRequest(function (actions, request) {
                  console.log("Please provide any fingerprint");
                  expect(request).toBeDefined();
                  actions.accept();
                })
                .onFingerprintCaptured(function () {
                  console.log("Please remove your finger");
                  didCallCaptured = true;
                })
                .onError(function (err) {
                  expect(err).toBeUndefined();
                  fail("Mobile fingerprint authentication threw instead of triggering fingerprint capture event");
                })
                .onSuccess(function () {
                  expect(didCallCaptured).toBe(true);
                  setTimeout(done, 500);
                });

              sendPushMobileAuthRequest("push_with_fingerprint_cordova");
            });

            it("Should request fingerprint authentication again on incorrect fingerprint", function (done) {
              onegini.mobileAuth.push.on("fingerprint")
                .onFingerprintRequest(function (actions, request) {
                  console.log("Please provide incorrect fingerprint");
                  expect(request).toBeDefined();
                  actions.accept();
                })
                .onFingerprintFailed(function () {
                  expect(true).toBe(true);
                  done();
                })
                .onError(function () {
                  fail("Mobile fingerprint authentication threw instead of requesting another attempt");
                })
                .onSuccess(function () {
                  fail("Mobile fingerprint authentication didn't request another attempt (or you supplied a correct fingerprint)")
                });

              sendPushMobileAuthRequest("push_with_fingerprint_cordova");
            });
          }
        });
      });
    }
    else {
      console.warn("Skipping mobileAuth.push (4/4). Mobile fingerprint authentication tests disabled");
    }
  });

  /******** onegini.resource (1/2) *********/

  describe('onegini.resource', function () {
    it('should exist', function () {
      expect(onegini.resource).toBeDefined();
    });

    describe('fetch', function () {
      it('should exist', function () {
        expect(onegini.resource.fetch).toBeDefined();
      });

      it('should fetch a non-anonymous resource', function (done) {
        onegini.resource.fetch(
          {
            url: 'https://onegini-msp-snapshot.test.onegini.io/resources/devices',
            headers: {
              'X-Test-String': 'foobar',
              'X-Test-Int': 1337
            }
          },
          function (response) {
            expect(response).toBeDefined();
            expect(response.body).toBeDefined();
            expect(response.rawBody).toBeDefined();
            expect(response.json).toBeDefined();
            expect(response.headers).toBeDefined();
            expect(response.status).toEqual(200);
            expect(response.statusText).toBeDefined();
            expect(response.json.devices).toBeDefined();
            done();
          }, function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });

      it('should fetch a relative non-anonymous resource', function (done) {
        onegini.resource.fetch(
          {
            url: '/devices',
            headers: {
              'X-Test-String': 'foobar',
              'X-Test-Int': 1337
            }
          },
          function (response) {
            expect(response).toBeDefined();
            expect(response.body).toBeDefined();
            expect(response.rawBody).toBeDefined();
            expect(response.json).toBeDefined();
            expect(response.headers).toBeDefined();
            expect(response.status).toEqual(200);
            expect(response.statusText).toBeDefined();
            expect(response.json.devices).toBeDefined();
            done();
          }, function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });

      it('should fetch a non JSON resource', function (done) {
        onegini.resource.fetch(
          {
            url: 'https://onegini-msp-snapshot.test.onegini.io/admin/static/img/connect.png',
            headers: {
              'X-Test-String': 'foobar',
              'X-Test-Int': 1337
            }
          },
          function (response) {
            expect(response).toBeDefined();
            expect(response.body).toBeDefined();
            expect(response.rawBody).toBeDefined();
            try {
              response.json;
              fail("An error is expected here");
            }
            catch (error) {
              if (!error instanceof TypeError) {
                fail("Expected a TypeError because an image is not a valid JSON response")
              }
            }
            expect(response.headers).toBeDefined();
            expect(response.status).toEqual(200);
            expect(response.statusText).toBeDefined();
            done();
          }, function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });

      it('should fetch a resource with a specific body without content-type', function (done) {
        var body = "foobar";
        onegini.resource.fetch(
          {
            url: 'https://onegini-msp-snapshot.test.onegini.io/resources/mirror-request',
            method: 'POST',
            body: body,
            headers: {
              'X-Test-String': 'foobar',
              'X-Test-Int': 1337
            }
          },
          function (response) {
            expect(response).toBeDefined();
            expect(response.body).toEqual(body);
            expect(response.rawBody).toBeDefined();
            expect(response.headers).toBeDefined();
            expect(response.headers['Content-Type']).toEqual('text/plain;charset=utf-8');
            expect(response.status).toEqual(200);
            expect(response.statusText).toBeDefined();
            done();
          }, function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });

      it('should fetch a relative resource with a specific body without content-type', function (done) {
        var body = "foobar";
        onegini.resource.fetch(
          {
            url: '/mirror-request',
            method: 'POST',
            body: body,
            headers: {
              'X-Test-String': 'foobar',
              'X-Test-Int': 1337
            }
          },
          function (response) {
            expect(response).toBeDefined();
            expect(response.body).toEqual(body);
            expect(response.rawBody).toBeDefined();
            expect(response.headers).toBeDefined();
            expect(response.headers['Content-Type']).toEqual('text/plain;charset=utf-8');
            expect(response.status).toEqual(200);
            expect(response.statusText).toBeDefined();
            done();
          }, function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });

      it('should fetch a resource with a JSON body', function (done) {
        var body = {
          "foo": "oof",
          "bar": "baz"
        };
        onegini.resource.fetch(
          {
            url: 'https://onegini-msp-snapshot.test.onegini.io/resources/mirror-request',
            method: 'POST',
            body: body,
            headers: {
              'Content-Type': 'application/json;charset=utf-8',
              'X-Test-String': 'foobar',
              'X-Test-Int': 1337
            }
          },
          function (response) {
            expect(response).toBeDefined();
            expect(response.body).toEqual(JSON.stringify(body));
            expect(response.rawBody).toBeDefined();
            expect(response.headers).toBeDefined();
            expect(response.headers['Content-Type']).toEqual('application/json;charset=utf-8');
            expect(response.status).toEqual(200);
            expect(response.statusText).toBeDefined();
            done();
          }, function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });

      it('should fetch a relative resource with a JSON body', function (done) {
        var body = {
          "foo": "oof",
          "bar": "baz"
        };
        onegini.resource.fetch(
          {
            url: '/mirror-request',
            method: 'POST',
            body: body,
            headers: {
              'Content-Type': 'application/json;charset=utf-8',
              'X-Test-String': 'foobar',
              'X-Test-Int': 1337
            }
          },
          function (response) {
            expect(response).toBeDefined();
            expect(response.body).toEqual(JSON.stringify(body));
            expect(response.rawBody).toBeDefined();
            expect(response.headers).toBeDefined();
            expect(response.headers['Content-Type']).toEqual('application/json;charset=utf-8');
            expect(response.status).toEqual(200);
            expect(response.statusText).toBeDefined();
            done();
          }, function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });

      it('should require a url', function () {
        expect(function () {
          onegini.resource.fetch();
        }).toThrow(new TypeError("Onegini: missing 'url' argument for fetch"));
      });

      it('should return error context when request fails', function (done) {
        onegini.resource.fetch({
            method: 'POST',
            url: 'https://onegini-msp-snapshot.test.onegini.io/resources/devices'
          }, function (response) {
            expect(response).toBeUndefined();
            fail('Success callback called, but method should have failed');
          },
          function (err) {
            expect(err).toBeDefined();
            expect(err.httpResponse.status).toEqual(405);
            expect(err.code).toEqual(8013);
            done();
          })
      });

      it('should return error context when relative resource request fails', function (done) {
        onegini.resource.fetch({
            method: 'POST',
            url: '/devices'
          }, function (response) {
            expect(response).toBeUndefined();
            fail('Success callback called, but method should have failed');
          },
          function (err) {
            expect(err).toBeDefined();
            expect(err.httpResponse.status).toEqual(405);
            expect(err.code).toEqual(8013);
            done();
          })
      });

      it('should intercept an XMLHttpRequest', function (done) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', 'https://onegini-msp-snapshot.test.onegini.io/resources/devices');
        xhr.onload = function () {
          expect(this.readyState).toEqual(4);
          expect(this.status).toBe(200);
          expect(JSON.parse(this.responseText).devices).toBeDefined();
          done();
        };
        xhr.send();
      });

      it('should intercept an XMLHttpRequest for binary data', function (done) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', 'https://onegini-msp-snapshot.test.onegini.io/resources/devices');
        xhr.responseType = 'arrayBuffer';
        xhr.onload = function () {
          expect(this.readyState).toEqual(4);
          expect(this.status).toBe(200);
          expect(this.response).toBeDefined();
          expect(this.response instanceof String).toBe(false);
          done();
        };
        xhr.send();
      });

      describe('implicit', function () {
        beforeAll(function (done) {
          onegini.user.authenticateImplicitly(registeredProfileId, done);
        });

        it('should fetch an implicit resource', function (done) {
          onegini.resource.fetch({
            url: 'https://onegini-msp-snapshot.test.onegini.io/resources/user-id-decorated',
            auth: onegini.resource.auth.IMPLICIT
          }, function (response) {
            expect(response.json.decorated_user_id).toEqual('✨ ' + config.userId + ' ✨');
            done();
          }, function (err) {
            console.error(err);
            fail('Method failed, but should have succeeded');
          });
        });

        it('should fetch a relative implicit resource', function (done) {
          onegini.resource.fetch({
            url: '/user-id-decorated',
            auth: onegini.resource.auth.IMPLICIT
          }, function (response) {
            expect(response.json.decorated_user_id).toEqual('✨ ' + config.userId + ' ✨');
            done();
          }, function (err) {
            console.error(err);
            fail('Method failed, but should have succeeded');
          });
        });

      });
    });
  });

  /******** onegini.user (2/2) *********/

  describe('onegini.user', function () {
    describe('changePin', function () {
      it("Should exist", function () {
        expect(onegini.user.changePin).toBeDefined();
      });

      it("should be cancellable at the authenticate with pin step", function (done) {
        onegini.user.changePin()
          .onPinRequest(function (actions, options) {
            actions.cancel();
          })
          .onError(function (err) {
            expect(err).toBeDefined();
            expect(err.code).toBe(9006);
            done();
          })
          .onSuccess(function () {
            fail("Change pin should have failed, but succeeded");
            done();
          });
      });

      it("should be cancellable at the create pin step", function (done) {
        onegini.user.changePin()
          .onPinRequest(function (actions, options) {
            actions.providePin(config.pin)
          })
          .onCreatePinRequest(function (actions, options) {
            actions.cancel();
          })
          .onError(function (err) {
            expect(err).toBeDefined();
            expect(err.code).toBe(9006);
            done();
          })
          .onSuccess(function () {
            fail("Change pin should have failed, but succeeded");
            done();
          });
      });

      it("Should succeed", function (done) {
        var count = 0;

        onegini.user.changePin()
          .onPinRequest(function (actions, options) {
            expect(actions).toBeDefined();
            expect(actions.providePin).toBeDefined();
            expect(options).toBeDefined();

            if (options.remainingFailureCount === options.maxFailureCount - 1) {
              expect(options.code).toBe(8012);
              expect(options.description).toBeDefined();
              actions.providePin(config.pin);
            }
            else {
              expect(options.remainingFailureCount).toBeDefined();
              expect(options.maxFailureCount).toBeDefined();
              expect(options.remainingFailureCount).toBe(3);
              expect(options.maxFailureCount).toBe(3);
              actions.providePin('incorrect');
            }
          })
          .onCreatePinRequest(function (actions, options) {
            expect(options.pinLength).toBe(5);
            expect(function () {
              actions.createPin();
            }).toThrow(new TypeError('Onegini: missing "pin" argument for createPin'));

            if (count === 0) {
              actions.createPin('invalid');
            }
            else {
              expect(count).toBe(1);
              actions.createPin(config.pin);
            }

            count += 1;
          })
          .onSuccess(function () {
            done();
          })
          .onError(function (err) {
            expect(err).toBeUndefined();
            fail('Change pin failed, but should have succeeded');
          });
      });
    });

    describe('isUserRegistered', function () {
      it("should exist", function () {
        expect(onegini.user.isUserRegistered).toBeDefined();
      });

      it("'profileId' argument mandatory", function () {
        expect(function () {
          onegini.user.isUserRegistered({}, function () {
          }, function () {
          });
        }).toThrow(new TypeError("Onegini: missing 'profileId' argument for isUserRegistered"));
      });

      it("should succeed with correct profileId", function (done) {
        onegini.user.isUserRegistered(
          {
            profileId: registeredProfileId
          },
          function (result) {
            expect(result).toBe(true);
            done();
          },
          function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });

      it("should fail with incorrect profileId", function (done) {
        onegini.user.isUserRegistered(
          {
            profileId: "UNKNOWN"
          },
          function (result) {
            expect(result).toBe(false);
            done();
          },
          function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });
    });

    describe('deregister', function () {
      it("should exist", function () {
        expect(onegini.user.deregister).toBeDefined();
      });

      it("'profileId' argument mandatory", function () {
        expect(function () {
          onegini.user.deregister({}, function () {
          }, function () {
          });
        }).toThrow(new TypeError("Onegini: missing 'profileId' argument for deregister"));
      });

      it("no user found for profileId", function (done) {
        onegini.user.deregister(
          {
            profileId: "UNKNOWN"
          },
          function (result) {
            expect(result).toBeUndefined();
          },
          function (err) {
            expect(err).toBeDefined();
            expect(err.description).toBe("Onegini: No registered user found.");
            done();
          });
      });

      it("should succeed with correct profileId", function (done) {
        onegini.user.deregister(
          {
            profileId: registeredProfileId
          },
          function (result) {
            expect(result).toBeDefined();
            done();
          },
          function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });
    });

    describe("getAuthenticatedUserProfile (3/3)", function () {
      it("should fail again", function (done) {
        onegini.user.getAuthenticatedUserProfile(
          function (result) {
            expect(result).toBeUndefined();
          },
          function (err) {
            expect(err).toBeDefined();
            expect(err.description).toBe("Onegini: No user authenticated.");
            done();
          });
      });
    });

    describe('getUserProfiles (2/2)', function () {
      it("should be one less", function (done) {
        onegini.user.getUserProfiles(
          function (result) {
            expect(result).toBeDefined();
            expect(result.length).toBeLessThan(nrOfUserProfiles);
            done();
          },
          function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });
    });

  });


  /******** onegini.device *********/

  describe('onegini.device', function () {
    it("should exist", function () {
      expect(onegini.device).toBeDefined();
    });

    describe("authenticate", function () {
      it("should exist", function () {
        expect(onegini.device.authenticate).toBeDefined();
      });

      it('should fail with invalid scopes', function (done) {
        onegini.device.authenticate(
          {
            scopes: ["incorrect"]
          },
          function () {
            expect(true).toBe(false);
          },
          function (err) {
            expect(err).toBeDefined();
            done();
          });
      });

      it('should succeed with valid scopes', function (done) {
        onegini.device.authenticate(
          {
            scopes: ["application-details"]
          },
          function () {
            expect(true).toBe(true);
            done();
          },
          function (err) {
            expect(err).toBeUndefined();
            fail('Error callback called, but method should have succeeded');
          });
      });
    });
  });

  /******** onegini.resource (2/2) *********/

  describe('onegini.resource', function () {
    it('should fetch an anonymous resource', function (done) {
      onegini.resource.fetch({
          url: 'https://onegini-msp-snapshot.test.onegini.io/resources/application-details',
          auth: onegini.resource.auth.ANONYMOUS
        },
        function (response) {
          expect(response).toBeDefined();
          expect(response.body).toBeDefined();
          expect(response.rawBody).toBeDefined();
          expect(response.json).toBeDefined();
          expect(response.headers).toBeDefined();
          expect(response.status).toEqual(200);
          expect(response.statusText).toBeDefined();
          done();
        },
        function (err) {
          expect(err).toBeUndefined();
          fail('Error response called, but method should have succeeded');
        })
    });

    it('should fetch a relative anonymous resource', function (done) {
      onegini.resource.fetch({
          url: '/application-details',
          auth: onegini.resource.auth.ANONYMOUS
        },
        function (response) {
          expect(response).toBeDefined();
          expect(response.body).toBeDefined();
          expect(response.rawBody).toBeDefined();
          expect(response.json).toBeDefined();
          expect(response.headers).toBeDefined();
          expect(response.status).toEqual(200);
          expect(response.statusText).toBeDefined();
          done();
        },
        function (err) {
          expect(err).toBeUndefined();
          fail('Error response called, but method should have succeeded');
        })
    })
  });
};
