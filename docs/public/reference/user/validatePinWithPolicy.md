# Validate PIN with policy

<!-- toc -->

At any moment in the app's lifecycle you can check whether or not a certain PIN code meets the policy you've configured server side. A PIN policy can be used to ensure your users can not register with an unsecure PIN.

Note that this check is available for convenience. A user will not be able to register using a PIN that does not conform to the policy configured on the server.

## `onegini.user.validatePinWithPolicy`

- Requires an object with a `pin` property:

| Property | Default | Description |
| --- | --- | --- |
| `pin` | - | The PIN you want to verify against the policy

The success callback will be invoked if the PIN is valid according to the policy. The error callback is invoked otherwise, with the usual error code and description explaining why it's not a valid PIN code.

```js
onegini.user.validatePinWithPolicy({
      pin: "28649"
    })
    .then(() => {
      console.log("The PIN is OK");
    })
    .catch((err) => {
      console.log("This PIN is invalid!\n\n", err.description);
    });
```

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 9011 | The error code
| `description` | "Provided PIN was marked as blacklisted on the Token Server." | Human readable error description
