# Handling OTP mobile authentication requests

<!-- toc -->

When a user is enrolled for mobile authentication, they are able to perform mobile authentication with OTP. The Onegini Cordova Plugin facilitates this by
allowing you to register handler methods that implement the UI.

## `onegini.mobileAuth.otp.handleRequest`

This method is used to trigger mobile authentication with OTP.

| Parameter | Description |
| --- | --- |
| `otp` | The base64 encoded OTP

The `handleRequest` method returns a [`OtpMobileAuthHandler`](OtpMobileAuthHandler.md) object, which can be used to implement the various handler methods.
Examples for implementing a OTP Mobile Authentication request can be found in the Mobile Authentication with OTP [topic guide](../../../topics/mobile-authentication-otp.md).
