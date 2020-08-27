# changePin

<!-- toc -->

Once authenticated, a user is able to change their PIN. Changing a user's PIN is done via an [AuthenticationHandler](AuthenticationHandler.md)

## `onegini.user.changePin`

This functions takes no arguments, as it is executed for the currently authenticated user. After calling this method, you will need to supply an `onPinRequest` and `onCreatePinRequest` callback. These callbacks will verify the current PIN and create a new one.

If the flow fails completely (e.g. the user is deregistered as a result of exceeding the maximum number of allowed PIN entries), the `onError` callback will be called with an error object.

**Example code changing a user's PIN:**

```js
onegini.user.changePin()
    .onPinRequest((actions, options) => {
      var pin = prompt("Please enter your PIN");
      actions.providePin(pin);
    })
    .onCreatePinRequest((actions) => {
      var pin = prompt("Enter your new PIN");
      actions.createPin(pin);
    })
    .onSuccess(() => {
      alert('Change pin success!');
    })
    .onError((err) => {
      alert('Change pin error!\n\n' + err.description)
    });
```

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8012 | The error code
| `description` | "Onegini: Incorrect PIN" | Human readable error description
