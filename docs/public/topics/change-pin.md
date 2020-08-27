# Change PIN

<!-- toc -->

## Changing PIN

The Onegini Cordova plugin exposes a function ([`onegini.user.changePin`](../reference/user/changePin.md)) to allow the currently logged in user to change their PIN. The user is first required to provide their current PIN, before being allowed to create the new PIN. The same familiar UI handler methods `onPinRequest` and `onCreatePinRequest` must therefore be implemented.

**Example code to change PIN of currently logged in user:**

```js
onegini.user.changePin()
    .onPinRequest((actions, options) => {
      var pin = prompt("Please enter your current PIN");
      actions.providePin(pin);
    })
    .onCreatePinRequest((actions, options) => {
      var pin = prompt("Please enter your new PIN");
      actions.createPin(pin);
    })
    .onSuccess(() => {
      alert("Change PIN success!");
    })
    .onError((err) => {
      alert("Change PIN error!\n\n" + err.description)
    });
```

Note that the PIN entered by the user should **not** be stored on the device or elsewhere in any shape or form. The Onegini Cordova plugin takes care of this for you in a secure manner.
