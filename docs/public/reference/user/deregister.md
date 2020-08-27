# Deregistration

<!-- toc -->

Deregistration is the process of removing a user (profile) from the device and server.

## `onegini.user.deregister`

This function takes a mandatory first argument with the following properties:

| Property | Default | Description |
| --- | --- | --- |
| `profileId` | - | The profile ID you previously stored during registration

```js
onegini.user.deregister({
      profileId: "W8DUJ2"
    })
    .then(() => {
      console.log("The provided profile is no longer registered!");
    })
    .catch((err) => {
      console.log("Deregister error!\n\n" + err.description);
    });
```

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8003 | The error code
| `description` | "Onegini: No registered user found." | Human readable error description
