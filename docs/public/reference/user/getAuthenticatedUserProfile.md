# Get Authenticated User Profile

<!-- toc -->

This method returns the currently authenticated user (profile). The method fails when no user is authenticated.

## `onegini.user.getAuthenticatedUserProfile`

```js
onegini.user.getAuthenticatedUserProfile()
    .then((userProfile) => {
      console.log("Authenticated userProfile:", userProfile);
    })
    .catch((err) => {
      console.log("Error!\n\n" + err.description);
    });
```

The success callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `profileId` | - | A profile ID associated with the user

The error callback can, for example, be invoked in case the user was not authenticated. It contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8005 | The error code
| `description` | "Onegini: No user authenticated." | Human readable error description
