# Registration

<!-- toc -->

Before a user can authenticate using PIN or fingerprint, a user will need to register with your Token Server. Registration can be initiated using this method.

## `onegini.user.register`

- Returns a new [`AuthenticationHandler`](AuthenticationHandler.md).
- Takes an optional object with a `scopes` property:

| Property | Default | Description |
| --- | --- | --- |
| `scopes` | server configuration | An array of scopes the user will register for (optional)

```js
onegini.user.register()
    .onCreatePinRequest(function (actions, options) {
      var pin = prompt("Create your " + options.pinLength + " digit PIN");
      actions.createPin(pin);
    })
    .onSuccess(function () {
      alert('Registration success!');
    })
    .onError(function (err) {
      alert('Registration error!\n\n' + err.description)
    });
```

The success callback contains an array of objects with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `profileId` | "W8DUJ2" | A profile ID associated with the user

The error callback contains an object with the following properties.

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8000 | The error code
| `description` | "Onegini: Internal plugin error." | Human readable error description
