# Authentication Handler

<!-- toc -->

## Introduction

When a user is required to Authenticate for certain methods, the Onegini Cordova Plugin will return an `AuthenticationHandler`.
This object can be used to register callbacks which will be called when certain authentication steps are required.

**Example: When a user's PIN is required, the `onPinRequest` callback will be called.**

```js
let handler = onegini.user.authenticate({
  profileId: "W8DUJ2"
});

handler.onPinrequest((actions, options) => {
  // Called when the SDK requests a users PIN.
});
```

When `onPinRequest` is called, the user should be asked for the PIN. After the user has entered their PIN, you can supply it to the SDK by calling an `action`,
in this case `providePin`.

**Example using the `prompt` to ask for the PIN:**

```js
handler.onPinRequest((actions, options) => {
  let pin = prompt("Please enter your PIN. You have " + options.remainingFailureCount + " attempts remaining.");
  actions.providePin(pin);
})
```

If the entered PIN is incorrect, the `onPinRequest` handler will be called with a new request. The `options` parameter contains additional parameters like max
allowed attempts and remaining attempts.

If the PIN is correct, the `onSuccess` handler will be called.

```js
handler.onSuccess(() => {
  alert("Authentication success!");
});
```

## Methods

The following methods can registered to handle different authentication events:

- [`onRegistrationRequest`](#onregistrationrequest)
- [`onPinRequest`](#onpinrequest)
- [`onCreatePinRequest`](#oncreatepinrequest)
- [`onFingerprintRequest`](#onfingerprintrequest)
- [`onFingerprintCaptured`](#onfingerprintcaptured)
- [`onFingerprintFailed`](#onfingerprintfailed)
- [`onSuccess`](#onsuccess)
- [`onError`](#onerror)

### `onRegistrationRequest`

This method is called when the SDK starts the registration flow. The options object contains a URL to the registration endpoint.
By default you do **not** have to register a handler for this event, as the plugin opens a browser automatically.
If you handle opening the registration page yourself, you can pass back the callback URL with `actions.handleRegistrationUrl` after the user has finished the
registration. You can determine whether the registration is done by checking if the URL that the browser opens matches the configured redirect URL in the Token
Server configuration.

```js
handler.onRegistrationRequest((actions, options) => {
  // Some logic to fetch the callback URL
  let registrationUrl = registerWithUrl(options.url);
  actions.handleRegistrationUrl(registrationUrl);
})
```

See also: [User registration topic guide](../../topics/user-registration.md).

### `onPinRequest`

This method is called when the user's PIN is required. The pin can be supplied using `actions.providePin`.

```js
handler.onPinRequest((actions, options) => {
  actions.providePin("12346");
});
```

### `onCreatePinRequest`

This method is called when a new PIN needs to be created. The new PIN can be provided using `actions.createPin`.

```js
handler.onCreatePinRequest((actions, options) => {
  console.log("Creating a new PIN of length " + options.pinLength);
  actions.createPin("12346");
});
```

### `onFingerprintRequest`

This method is called when the user is required to authenticate with fingerprint. The following three actions can be called in response:

- `acceptFingerprint`: _Start reading fingerprint sensor_.
- `denyFingerprint`: _Cancel this authentication attempt_.
- `fallbackToPin`: _Ask for a PIN instead, you will receive a PIN request_.

The `acceptFingerprint` actions can take an object with an `iosPrompt` value to display on the TouchID prompt.

```js
handler.onFingerprintRequest((actions) => {
  console.log("Accepting fingerprint request");
  actions.acceptFingerprint({ iosPrompt: 'Login to myApp' });
});
```

### `onFingerprintCaptured`

This method is called when the fingerprint sensor detects that a finger has been placed on the sensor. It has not yet been determined if the fingerprint is
correct. This method can be used to provide feedback to the user so that they are aware the device has detected their finger.

**Note:** this method is never called on iOS. TouchID on iOS uses its own native UI to provide feedback to users.

```js
handler.onFingerprintCaptured(() => {
  console.log("Fingerprint captured");
});
```

### `onFingerprintFailed`

This method is called when the fingerprint previously detected was not recognized as the correct fingerprint. This method can be used to provide feedback to
your users so the are aware the device did not recognize their fingerprint. Additionally, the user should be able to try again.

```js
handler.onFingerprintFailed(() => {
  console.log("Fingerprint incorrect! The user should try again.");
});
```

### `onSuccess`

This method is called when the user has been successfully authenticated. You can now perform actions that require the user to be authenticated.

```js
handler.onSuccess(() => {
  console.log("You have been successfully authenticated!");
});
```

### `onError`

This method is called when the authentication has failed. The failure can be due to several errors, like failing to connect to the network, or when the user
has exceeded the maximum attempts at entering a PIN and has been deregistered.

```js
handler.onError((err) => {
  console.log("Something went wrong while trying to authenticate! ", err);
})
```
