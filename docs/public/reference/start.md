# Bootstrapping the Onegini Cordova Plugin

The first thing that needs to be done when the app starts is to initizialize the Onegini Cordova Plugin. This will perform a few checks and report an error in case of trouble.

## `onegini.start`

This function takes an optional first argument with the following properties:

| Property | Default | Description |
| --- | --- | --- |
| `secureXhr` | `false` | Allow the plugin to intercept XHR GET/POST/... webview requests and route them through the plugin for added security

```js
// As with all Cordova plugins you must wait for "deviceready" to fire
document.addEventListener("deviceready", () => {
  onegini.start({ secureXhr: true })
      .then(() => {
        // you can now call any other method on the Onegini object
        console.log("Onegini is ready!");
      })
      .catch((err) => {
        console.log("Start error!\n\n" + err.description);
      });
}, false);
```

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 10001 | The error code
| `description` | "Onegini: Configuration error" | Human readable error description
