# Authenticate device

<!-- toc -->

The device can be authenticated for specific (or the default) scopes.

## `onegini.device.authenticate`

This function takes an optional first argument with the following properties:

| Property | Default | Description |
| --- | --- | --- |
| `scopes` | server configuration | An array of scopes the device will authenticate for

```js
onegini.device.authenticate({ scopes: ["read"] })
    .then(() => {
      alert("The device has been authenticated!");
    })
    .catch((err) => {
      alert("Device authentication error!\n\n" + err.description);
    });
```

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 9001 | The error code
| `description` | "Invalid Pin" | Human readable error description
