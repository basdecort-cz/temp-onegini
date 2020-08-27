# Deregister user

<!-- toc -->

## Deregistering a user

Deregistering a user implies the removal of all of their data (including access and refresh tokens) from the device. It also includes a request to the Token Server to revoke all tokens associated with the user. The client credentials will remain stored on the device.

The Onegini Cordova plugin exposes the [`onegini.user.deregister`](../reference/user/deregister.md) function to properly deregister a user, as described above. It takes the user's `profileId` as argument.

**Example code to deregister a user:**

```js
onegini.user.deregister({ profileId: "profileIdOfUser" })
    .then(() => {
      alert("User deregistered!");
    })
    .catch((err) => {
      alert("Deregister error!\n\n" + err.description);
    });
```

Note that any existing user can be deregistered. They do not necessarily have to be logged in.
