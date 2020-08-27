# Push mobile authentication Handler

<!-- toc -->

## Introduction

When a user is enrolled for push mobile authentication, they are able to receive and respond to mobile authentication requests. After receiving a push notification containing such a request, the Onegini Cordova Plugin needs to know how to handle it. This is done via a `PushMobileAuthHandler` object, which can be used to register callbacks which will be called when certain mobile authentication steps are required.

**Example:** When a mobile authentication request of type **push** is received, the `onConfirmationRequest` callback will be called.

```js
let handler = onegini.mobileAuth.push.on("confirmation");

handler.onConfirmationRequest((actions, request) => {
  // Called when the SDK receives a mobile authentication request of type push
});
```

When `onConfirmationRequest` is called, the user should be prompted to either accept or deny the mobile authentication request. Once the user has made a choice, it can be supplied to the Onegini Cordova Plugin by calling an `action`, in this case `accept` or `deny`. The UI to do this is left up to the developer. The following example shows the general strategy to do this.

```js
handler.onConfirmationRequest((actions, request) => {
  // Suppose the user chooses to accept the mobile authentication request.
  let userAcceptedRequest = true;

  if (userAcceptedRequest) {
    action.accept();
  } else {
    action.deny();
  }
});
```

If the request and responses were valid, the `onSuccess` handler will be called.

```js
handler.onSuccess(() => {
  alert("Mobile authentication confirmation success!");
});
```

## Methods

The following methods can be registered to handle different mobile authentication request types.

- [`onConfirmationRequest`](#onconfirmationrequest)
- [`onPinRequest`](#onpinrequest)
- [`onFingerprintRequest`](#onfingerprintrequest)
- [`onFingerprintCaptured`](#onfingerprintcaptured)
- [`onFingerprintFailed`](#onfingerprintfailed)
- [`onSuccess`](#onsuccess)
- [`onError`](#onerror)

### `onConfirmationRequest`

This method is called on a mobile authentication request with the **push** method. The request can either be accepted using `actions.accept` or denied using `actions.deny`.

```js
handler.onConfirmationRequest((actions, request) => {
  actions.accept();
});
```

### `onPinRequest`

This method is called on a mobile authentication request with the **push with PIN** method. It can also be called on the **push with fingerprint** method, in case of PIN fallback. The request can either be accepted using `actions.accept` taking the PIN as argument, or denied using `actions.deny`.

```js
handler.onPinRequest((actions, request) => {
  actions.accept("12346");
});
```

Note that if the PIN was incorrect, this method will be called again (until the user has exceeded the attempt limit). As always, the PIN should **not** be stored on the device.

### `onFingerprintRequest`

This method is called on a mobile authentication request with the **push with fingerprint method.** The request can either be accepted using `actions.accept` or denied using `actions.deny`.

```js
handler.onFingerprintRequest((actions, request) => {
  actions.accept();
});
```

Unlike `onPinRequest`, this method will only be called once. Touch ID on iOS uses its own native UI to provide feedback to users, and the whole process is atomic. On Android, `onFingerprintFailed` will be called if the user needs to scan their fingerprint again.

### `onFingerprintCaptured`

This method is called when the fingerprint sensor detects that a finger has been placed on the sensor. At this point, the fingerprint authenticity has not yet been determined. This method can be used to provide feedback to the user so that they are aware the device has detected their finger.

Note that this method is **not** available on iOS. TouchID on iOS uses its own native UI to provide feedback to users.

```js
handler.onFingerprintCaptured(() => {
  console.log("Fingerprint captured!");
});
```

### `onFingerprintFailed`

This method is called when the fingerprint previously detected was not recognized as the correct fingerprint. This method can be used to provide feedback to the user so they are aware the device did not recognize their fingerprint, and that they need to try again.

```js
handler.onFingerprintFailed(() => {
  console.log("Fingerprint failed!");
});
```

### `onSuccess`

This method is called when the user has successfully accepted the mobile authentication request. At this point, the authentication request is considered completed, and another push notification can be handled.

```js
handler.onSuccess(() => {
  alert("Mobile authentication request accepted!");
});
```

### `onError`

This method is called when the user has denied the mobile authentication request, the user has failed to authenticate themselves, or an error has occurred. At this point, the authentication request is considered completed, albeit failed. Another push notification can be handled.

```js
handler.onError(() => {
  alert("Mobile authentication request failed!");
});
```
