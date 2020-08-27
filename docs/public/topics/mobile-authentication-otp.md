# Mobile authentication with OTP

<!-- toc -->

## Introduction

The Onegini Mobile Security Platform offers an ability of mobile authentication with a One Time Password (OTP). Mobile authentication with OTP
provides users an easy and secure way for two factor authentication or single factor authentication where no passwords are required. A good use case is for
example letting a user login to your web application using his/her mobile device by scanning a QR code displayed within a browser. This essentially allows the
user to authenticate using his/her mobile device. It is also not relying on third party services like APNs or FCM. All of the communication stays between App,
web application and Mobile Security Platform.

An Example implementation could work like this: A web application fetches the OTP from the Token Server and displays it on the login page in the form
of a QR code. Then user opens your mobile application and scans the QR code with his camera and is automatically logged in into your website. Of course it's up
to you to choose how to implement it, the above scenario is just an example.

## Setup and requirements

OTP mobile authentication requires configuration on the Token Server side. Please follow the [Mobile authentication
configuration]({{book.configure_mobile_authentication_types}}) guide in order to setup the OTP mobile authentication type.

## Enrollment

It's only required to enroll for mobile authentication to use OTP. If the user is not enrolled, you can perform
enrollment by following the [Mobile Authentication Enrollment](./mobile-authentication.md#enrollment) guide.

## Request handling

Once you have retrieved an OTP in your application you need to hand it over to the Onegini Cordova Plugin in order to let our SDK process it.

For mobile authentication with push there is support for multiple methods to additionally authenticate a user. For mobile auth with OTP we currently only
support the simple scenario where the user can accept or deny the request. The code example below shows how to implement this.

**Example code to handle OTP authentication**
```js
onegini.mobileAuth.otp.handleRequest({
  otp: 'base64 encoded OTP'
})
  .onConfirmationRequest((actions, request) => {
    console.log("New OTP mobile authentication request", request);

    // Ask user if they want to accept or deny the request. In this
    // example, the user accepts the request.
    let userAcceptedRequest = true;

    if (userAcceptedRequest) {
      actions.accept();
    } else {
      actions.deny();
    }
  })
  .onSuccess(() => {
    alert("OTP Mobile authentication request success!");
  })
  .onError((err) => {
    alert("OTP Mobile authentication request failed!\n\n" + err.description);
  });
```
