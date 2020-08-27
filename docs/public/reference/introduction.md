# Reference guides

This section describes the APIs that are exposed by the Onegini Cordova Plugin.

## APIs

  * [`onegini.start`](start.md)
  * [`onegini.user.*`](user/introduction.md)
  * [`onegini.device.*`](device/introduction.md)
  * [`onegini.resource.*`](resource/introduction.md)
  * [`onegini.mobileAuth.*`](mobileAuthentication/introduction.md)

## Using promises instead of callbacks

All API functions accept an optional success callback and error callback function.

If Promises are more your style then you may be happy to learn that all methods that would only call their success or error callbacks once return a Promise if no success callback is supplied.

For example, say you want to get all the authenticators available for a certain user. With callbacks you might use:

```js
onegini.user.authenticators.getAll(
  {
    profileId: "W8DUJ2"
  },

  // success callback
  function (authenticators) {
    console.log("Authenticators available:", authenticators);
  },

  // error callback
  function (err) {
    console.log("Something went wrong! Error: " + err.description);
  }
);
```

...or as a Promise, you might use:

```js
onegini.user.authenticators.getAll({ profileId: "W8DUJ2" })
    .then(() => {
      console.log("Authenticators available:", authenticators);
    })
    .catch((err) => {
      console.log("Something went wrong! Error: " + err.description);
    });
```
