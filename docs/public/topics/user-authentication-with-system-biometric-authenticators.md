# User authentication with system biometric authenticators

## Introduction

The Onegini Cordova plugin allows you to authenticate users with the system biometric authenticators. These authenticators are provided by the device's 
operating system (iOS - Touch ID and Face ID, Android - fingerprint) if they are available on the device. System biometric authenticators can be used for both: 
regular and mobile authentication. Users will be able to retry system biometric authentication as many times as the OS allows them to. If the OS system's 
biometric authenticators API returns an error for any reason (for example in case of too many failed attempts), the Onegini Cordova plugin will revoke system 
biometric authenticator and will perform a fallback to PIN authentication.

<!-- toc -->

### Requirements
#### FaceID

iOS needs to have configured message displayed on FaceID alert. It's configurable by adding `NSFaceIDUsageDescription` in your config.xml file.

**Example configuration**

```xml
<platform name="ios">
  <config-file parent="NSFaceIDUsageDescription" target="*-Info.plist">
    <string>FaceID is used as a authenticator to login to application.</string>
  </config-file>
</platform>
```

Not specifying this property in your configuration will crash your application when you will try to use Face ID authentication.

### Differences between Android and iOS

It should be noted that there are significant differences between Fingerprint on Android and Touch ID on iOS. As a result, some methods may be available on only one of the operating systems. This will be specified where applicable.

## Enabling system biometric authenticator authentication

In order to enable system biometric authenticator authentication for a user, the Onegini Cordova plugin provides the [`onegini.user.authenticators.registerNew`](../reference/user/authenticators.md#oneginiuserauthenticatorsregisternew). This function requires the user to authenticate. As a result, it is necessary to implement the UI associated with the `onPinRequest` method, in addition to the `onSuccess` and `onError` methods.

**Example code for registering the system biometric authenticator:**

```js
onegini.user.authenticators.registerNew({ authenticatorType: "Fingerprint" })
    .onPinRequest((actions, options) => {
      var pin = prompt("Please enter your PIN");
      actions.providePin(pin);
    })
    .onSuccess(() => {
      alert("Success!");
    })
    .onError((err) => {
      alert("Error!\n\n" + err.description);
    });
```

> To register FaceID authenticator `authenticatorType` should be set as "Fingerprint"

Fingerprint authentication may not be available on every device. In this case, or if the authenticator has already been registered, the above method will return an error.

To request a list of available authenticators that have not yet been registered, the plugin exposes the [`onegini.user.authenticators.getNotRegistered`](../reference/user/authenticators.md#oneginiuserauthenticatorsgetnotregistered) function, which takes the `profileId` of the desired user as argument. If the device does not meet the fingerprint requirements, the fingerprint authenticator will not be present in the returned array of of authenticators.

Note that registering a new authenticator does not set it as the preferred authenticator for the user, which is PIN by default. To change this, [`onegini.user.authenticators.setPreferred`](../reference/user/authenticators#oneginiuserauthenticatorssetpreferred) can be used.

**Example code to set fingerprint as the preferred authenticator:**

```js
onegini.user.authenticators.setPreferred({ authenticatorType: "Fingerprint" })
    .then(() => {
      alert("Fingerprint set as preferred authenticator!");
    })
    .catch((err) => {
      alert("Error!\n\n" + err.description);
    });
```
> To set FaceID as the preferred authenticator `authenticatorType` should be set as "Fingerprint"


## Authenticating a user with fingerprint

Once the fingerprint authenticator has been registered and set as the preferred authenticator, the user is able to authenticate using fingerprint. The method to do so is the same as for PIN, the [`onegini.user.authenticate`](../reference/user/authenticate.md) method.

However, if fingerprint authentication is a possibility for the user, extra handler methods must be implemented. This is in addition to the PIN specific methods (which are necessary in case of fallback to PIN).

**Example code to log in a user with fingerprint:**

```js
onegini.user.authenticate({ profileId: "profileIdOfUser" })
    .onPinRequest((actions, options) => {
      var pin = prompt("Please enter your PIN");
      actions.providePin(pin);
    })
    .onFingerprintRequest((actions) => {
      alert("Accepting fingerprint authentication request");
      actions.acceptFingerprint({ iosPrompt: "Log in to Cordova Example App" });
    })
    .onFingerprintCaptured(() => {
      alert("Fingerprint captured! Waiting for verification.");
    })
    .onFingerprintFailed(() => {
      alert("Incorrect fingerprint! Please try again.");
    })
    .onSuccess(() => {
      alert("Authentication success!");
    })
    .onError(() => {
      alert("Authentication error!\n\n" + err.description);
    });
```

Note that `onFingerprintCaptured` and `onFingerprintFailed` will only be called on Android, as Touch ID on iOS does not emit these events.

If the user fails to authenticate using fingerprint too many times (this limit is set by the OS), the fingerprint authenticator is automatically deregistered and the relevant tokens are revoked by the Onegini Cordova plugin. At this point, a fallback to PIN is performed, and the user is request to enter their PIN via `onPinRequest`. If this too, fails, `onError` will be called, signalling the authentication has failed.
