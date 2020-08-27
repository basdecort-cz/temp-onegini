# Logout

<!-- toc -->

For security reasons, it is always advisable to explicitly logout a user. The Onegini Cordova Plugin exposes the following function to do so.

## `onegini.user.logout`

- Logs out the currently authenticated user.

```js
onegini.user.logout()
    .then(() => {
      console.log("Logout successful!");
    })
    .catch((err) => {
      console.log("Something went wrong! " + err.description);
    });
```

The error callback contains an object with the following properties.

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8000 | The error code
| `description` | "Onegini: Internal plugin error." | Human readable error description
