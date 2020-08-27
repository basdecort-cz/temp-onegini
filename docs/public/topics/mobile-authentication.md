# Mobile authentication

<!-- toc -->

## Introduction

The **Onegini Mobile Security Platform** offers a mobile authentication mechanism in a user friendly and secure way. You can for instance take advantage of
mobile authentication to add second factor authentication to your product, that can be used to improve the security of selected actions like logging into your
website or accepting a transaction payment.

The mobile authentication feature is an extensive feature that has a number of different possibilities. E.g. there are different ways that mobile authentication
is triggered / received on a mobile device:
- With push notifications; The user gets a push notification on his phone to alert him that a mobile authentication transaction is pending.
- With an One-Time-Password (OTP); The user provides an OTP in order to confirm a mobile authentication transaction. Since the OTP is long it is likely that
the OTP is transformed into a QR code and the user scans this code with his mobile device.

The mobile authentication with Push mechanism offers different ways of user authentication so you can ask your users for additional verification when accepting
 a mobile authentication request.

## Setup and requirements

Before mobile authentication can be used, you should configure the Token Server to support this functionality. Please
follow [Mobile authentication configuration]({{book.app_config_mobile_authentication}}) guide to set it up.

When the Token Server is configured, you can enroll and handle mobile authentication requests using the Onegini SDK.

## Enrollment
The mobile authentication enrollment process is separated into two different steps:
- enroll: Performs the basic enrollment
- enroll with push: Exchanges push tokens with the Token Server so it can send push notifications to a mobile device.

This paragraph describes the first enrollment step, please check the [push enrollment](mobile-authentication-push.md#enrollment) topic guide for the enrollment with 
push documentation. After you have performed this step you can use mobile authentication with OTP.

If you don't want to use mobile authentication **with push** you don't need to perform the `onegini.mobileAuth.push.enroll` step. The
`onegini.mobileAuth.enroll` method enables the basic mobile authentication feature. Mobile authentication with OTP is possible after you enrolled the user
(using the `onegini.mobileAuth.enroll` method).

>**Note:** It is advised to perform the `onegini.mobileAuth.enroll` step as soon as possible in your application as it is quite resource intensive because it
generates a private key and certificate.
The Onegini Cordova plugin requires an authenticated or logged in user to enroll for mobile authentication. The user can enroll for mobile authentication on
every device that he/she installed your application on.

```js
onegini.mobileAuth.enroll()
    .then(() => {
      alert("Enrollment success!");
    })
    .catch((err) => {
      alert("Enrollment error!\n\n" + err.description);
    });
```

Successive invocations of enrollment for mobile authentication will re-enroll the device only if the mobile
authentication override is enabled in The Token Server configuration. See the [Token Server mobile authentication
configuration]({{book.app_config_mobile_authentication}}) for more information on the server side configuration of
mobile authentication.

The plugin also provides a convenience method to check whether a user is enrolled for mobile authentication. The `onegini.mobileAuth.isUserEnrolled` method 
provides you with the information whether the user is already enrolled for mobile authentication. Below follows an example implementation.

```js
onegini.mobileAuth.isUserEnrolled({
      profileId: "W8DUJ2"
    })
    .then((isEnrolled) => {
      if (isEnrolled) {
        alert("The user is enrolled.");
      } else {
        alert("The user is not enrolled.");
      }
    })
    .catch((err) => {
      alert("error!\n\n" + err.description);
    });
```

Enrolling for mobile authentication will NOT enroll automatically for push mobile authentication. To enroll for push please follow the
[Push Mobile Authentication Enrollment](./mobile-authentication-push.md#enrollment) guide.

## Request handling

The Onegini Cordova plugin is capable of handling two types of mobile authentication requests. For more information on handling each
mobile authentication type, please refer to the corresponding request handling guides.
* Push - [Push Mobile Authentication - Request handling](./mobile-authentication-push.md#request-handling) guide
* OTP - [OTP Mobile Authentication - Request handling](./mobile-authentication-otp.md#request-handling) guide
