# Handling push mobile authentication requests

<!-- toc -->

When a user is enrolled for push mobile authentication, they are able to receive and respond to mobile authentication requests that are received on the device
through push notifications. The Onegini Cordova Plugin facilitates this by allowing you to register handler methods that implement the UI.

## `onegini.mobileAuth.push.on`

This method is used to register handlers for different mobile authentication methods. The various methods (**push**, **push with PIN**, etc.) can be [configured](https://docs.onegini.com/token-server/topics/mobile-apps/mobile-authentication/mobile-authentication.html#configure-authentication-properties) in the Token Server. Once configured, the handlers for these various methods need to be registered using `on`, which takes the method string as parameter.

| Parameter | Description |
| --- | --- |
| `method` | The mobile authentication method you wish to register a handler for

Currently, the available method strings are `"confirmation"` (push), `"pin"` (push with PIN) and `"fingerprint"` (push with fingerprint).

The `on` method returns a [`PushMobileAuthHandler`](PushMobileAuthHandler.md) object, which can be used to implement the various handler methods.
Examples for implementing Push Mobile Authentication request can be found in the Mobile Authentication with Push [topic guide](../../../topics/mobile-authentication-push.md).
