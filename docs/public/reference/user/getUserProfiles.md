# Get User Profiles

<!-- toc -->

The Onegini Cordova Plugin maintains a set of profiles that you have created. This method allows you to retrieve all existing profiles.

## `onegini.user.getUserProfiles`

- Used to get an array of all existing userProfiles.

```js
onegini.user.getUserProfiles()
    .then((userProfiles) => {
      console.log("User profiles registered: ", userProfiles);
    })
    .catch((err) => {
      console.log("Error!\n\n", err.description)
    });
```

The success callback contains an array of objects with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `profileId` | - | A profile ID associated with the user

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8000 | The error code
| `description` | "Onegini: Internal plugin error." | Human readable error description
