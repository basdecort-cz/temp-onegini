# Authentication

<!-- toc -->

Your users can authenticate themselves using any [authenticator](authenticators.md) registered to them. Authentication is done via an
[AuthenticationHandler](AuthenticationHandler.md). If a user is already authenticated when calling this function, they will be logged out and have to authenticate again.

## `onegini.user.authenticate`

- Used to authenticate a user.
- Requires an object containing a `profileId`.
- Returns a new AuthenticationHandler.

| Property | Default | Description |
| --- | --- | --- |
| `profileId` | - | The profile ID you previously stored during registration

**Example for authentication with PIN:**

```js
onegini.user.authenticate({
    profileId: "some profile ID"
  })
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

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 9001 | The error code
| `description` | "Invalid Pin" | Human readable error description
