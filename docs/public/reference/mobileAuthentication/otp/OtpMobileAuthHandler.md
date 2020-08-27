# OTP mobile authentication Handler

<!-- toc -->

## Introduction

When a user is enrolled for mobile authentication, they are able to receive and respond to mobile authentication requests. After initiating an OTP request, the Onegini Cordova plugin returns a `MobileAuthenticationHandler` object, which can be used to register callbacks which will be called when certain OTP mobile authentication steps are required.

**Example:** When an OTP mobile authentication request is initiated the `onConfirmationRequest` callback will be called.

```js
let handler = onegini.mobileAuth.otp.handleRequest({
  otp: "base64 encoded OTP"
});

handler.onConfirmationRequest((actions, request) => {
  // Called when the provided OTP should only be confirmed without additional verification.
});
```

When `onConfirmationRequest` is called, the user should be prompted to either accept or deny the OTP mobile authentication request. Once the user has made a choice, it can be supplied to the Onegini Cordova Plugin by calling an `action`, in this case `accept` or `deny`. The UI to do this is left up to the developer. The following example shows the general strategy to do this.

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
- [`onSuccess`](#onsuccess)
- [`onError`](#onerror)

### `onConfirmationRequest`

This method is called on a mobile authentication request with the **push** method. The request can either be accepted using `actions.accept` or denied using `actions.deny`.

```js
handler.onConfirmationRequest((actions, request) => {
  actions.accept();
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
