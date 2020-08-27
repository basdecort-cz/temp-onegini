# Logging out

<!-- toc -->

## Logout a user

In the Onegini Cordova plugin, a user is treated as logged in as long as the user has an access token for the operation to be executed. Therefore, to logout the user, the access token needs to be removed. This can be done by calling the [`onegini.user.logout`](../reference/user/logout.md) function. The plugin will remove the access token on the client, and also send a request to the Token Server to invalidate the token server side.

**Example code to logout a user:**

```js
onegini.user.logout()
  .then(() => {
    alert("User logged out!");
  })
  .catch((err) => {
    alert("Logout error!\n\n" + err.description);
  });
```

If a refresh token is stored on the device, it will persist after the logout action. It can then be used to login again later.
