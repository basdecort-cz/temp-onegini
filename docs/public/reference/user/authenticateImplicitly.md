# Implicit Authentication

<!-- toc -->

You can use implicit authentication to authenticate users based on their client credentials. This means you can assume the user has successfully completed the
registration process in the past. After authenticating implicitly, you can fetch resources which require implicit authentication. Implicit authentication requires
no user interaction like asking for their PIN or Fingerprint.

## `onegini.user.authenticateImplicitly`

- Used to implicitly authenticate a user.
- Returns a promise or calls either success or error callback.
- Takes the following arguments:

| Property    | Default              | Description                                                   |
| ----------- | -------------------- | ------------------------------------------------------------- |
| `profileId` | -                    | A profile ID created when the user profile is registered      |
| `scopes`    | registration scopes  | An array of scopes the user will authenticate with (optional) |

The `scopes` array may only contain scopes that were obtained during registration. If no scopes are provided, the plugin will authenticate the user for
_all_ scopes granted during registration.

** Example: **
```js
onegini.user.authenticateImplicitly({
  userId: 'someUserId',
  scopes: ['account-balance']
}).then(() => {
  alert('Success!');
}).catch((err) => {
  alert('Something went wrong');
  console.error(err);
})
```
