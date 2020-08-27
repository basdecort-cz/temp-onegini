# User authentication

<!-- toc -->

## Introduction

The Cordova Plugin uses the OAuth 2.0 protocol to authenticate the device to access protected resources. To support this, the Cordova Plugin acts as an OAuth
2.0 client.

## Registering a user

The OAuth 2.0 protocol begins with registration. The [`onegini.user.register`](../reference/user/register.md) function can be used to register a user.
This function can take an array of scopes that authentication is requested for as argument. If no scopes are requested, the default scopes of the application
will be used.

When registering a user, the Cordova Plugin redirects the user to the authentication endpoint on the Token Server via the browser. Once the client credentials
have been validated, and an authorization grant has been issued, the user will be redirected to the app. Based on this authorization grant, the client will
request an access token for the specified set of scopes. If the grant includes a refresh token, the user will need to create a PIN.

The `onegini.user.register` function returns a new [`AuthenticationHandler`](../reference/user/AuthenticationHandler.md), for which we need to implement the UI
for the methods `onCreatePinRequest`, `onSuccess`, and `onError`.

**Example code to register a user:**

```js
onegini.user.register({ scopes: ["read"] })
    .onCreatePinRequest((actions, options) => {
      var pin = prompt("Create your " + options.pinLength + "digit PIN");
      actions.createPin(pin);
    })
    .onSuccess((result) => {
      alert("Registration success! Profile ID: " + result.profileId);
    })
    .onError((err) => {
      alert("Registration error!\n\n" + err.description);
    });
```

The `onSuccess` method indicates the user has been registered and also logged in. The `profileId` property of the resulting object can be stored for future use.

Note that the PIN entered by the user should **not** be stored on the device or elsewhere in any shape or form. The Cordova Plugin takes care of this for you in
a secure manner.

See also: [User Registration topic guide](user-registration.md).

## Getting registered users

While it is possible to keep track of registered users oneself, the Cordova Plugin also provides a function to retrieve all registered profiles.

**Example code to get registered users:**

```js
onegini.user.getUserProfiles()
  .then((result) => {
    if (result.length > 0) {
      alert("Found some registered users!");
    } else {
      alert("No registered users found.");
    }
  })
  .catch((err) => {
    alert("Get profiles error!\n\n" + err.description);
  });
```

It is also possible to check if a specific profile ID is registered. See [`onegini.user.isUserRegistered`](../reference/user/isUserRegistered.md).

## Authenticating a registered user

Once a user has been registered, they can be logged in using the [`onegini.user.authenticate`](../reference/user/authenticate.md), or the
[`onegini.user.authenticateImplicitly`](../reference/user/authenticateImplicitly.md) function. These functions take the `profileId` of the user wishing to log
in as argument.

**Example code to log in a user:**

```js
onegini.user.authenticate({ profileId: "profileIdOfUser" })
    .onPinRequest((actions, options) => {
      var pin = prompt("Please enter your PIN");
      actions.providePin(pin);
    })
    .onSuccess(() => {
      alert("Authentication success!");
    })
    .onError((err) => {
      alert("Authentication error!\n\n" + err.description);
    });
```

The result of authentication is an access token with an optional refresh token, depending on the scopes. The `onSuccess` method indicates the user has been
authenticated. At this point, it is possible to request data on behalf of the user.

If the authentication fails, the refresh token is removed from the device.
