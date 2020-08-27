# Authenticators

<!-- toc -->

Authenticators are used to verify the identity of a user. The Onegini Cordova Plugin currently supports PIN and fingerprint authenticators. The fingerprint authenticator is only available on devices with a fingerprint sensor.

## `onegini.user.authenticators.getAll`

- Used to get an array of authenticators available for a specific user.
- Requires an object containing a `profileId`.

| Property | Example | Description |
| --- | --- | --- |
| `profileID` | "W8DUJ2" | The profile ID as received from `onegini.user.registration`

```js
onegini.user.authenticators.getAll({
      profileId: "W8DUJ2"
    })
    .then((authenticators) => {
      console.log("Available authenticators", authenticators);
    })
    .catch((err) => {
      if (err.code === 8003) {
        console.log("That user does not exist!");
      } else {
        console.log("Something went wrong! Error:", err);
      }
    });
```

The success callback contains an array of objects with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `authenticatorType` | "PIN" | The authenticator type
| `authenticatorId` | "PIN" | The authenticator ID, which distinguishes between authenticators of type "Custom". (Only required for custom authenticators)

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8003 | The error code
| `description` | "Onegini: No registered user found." | Human readable error description

## `onegini.user.authenticators.getNotRegistered`

- Used to get an array of authenticators that are _not currently registered_ for a specific user.
- Requires an object containing a `profileId`:

| Property | Example | Description |
| --- | --- | --- |
| `profileID` | "W8DUJ2" | The profile ID as received from `onegini.user.register`

```js
onegini.user.authenticators.getNotRegistered({
      profileId: "W8DUJ2"
    })
    .then((authenticators) => {
      console.log("Available authenticators", authenticators);
    })
    .catch((err) => {
      if (err.code === 8003) {
        console.log("That user does not exist!");
      } else {
        console.log("Something went wrong! Error:", err);
      }
    });
```

The success callback contains an array of objects with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `authenticatorType` | "PIN" | The authenticator type
| `authenticatorId` | "PIN" | The authenticator ID, which distinguishes between authenticators of type "Custom". (Only required for custom authenticators)

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8003 | The error code
| `description` | "Onegini: No registered user found." | Human readable error description

## `onegini.user.authenticators.getPreferred`

- Used to get the preferred authenticator for the currently authenticated user.
- Does not require any arguments.

```js
onegini.user.authenticators.getPreferred()
    .then((authenticator) => {
      console.log("Preferred authenticator:", authenticator);
    })
    .catch((err) => {
      if (err.code === 8003) {
        console.log("That user does not exist!");
      } else {
        console.log("Something went wrong! Error:", err);
      }
    });
```

The success callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `authenticatorType` | "PIN" | The authenticator type
| `authenticatorId` | "PIN" | The authenticator ID, which distinguishes between authenticators of type "Custom". (Only required for custom authenticators)

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8003 | The error code
| `description` | "Onegini: No registered user found." | Human readable error description

## `onegini.user.authenticators.setPreferred`

- Used to set the preferred authenticator for the currently authenticated user.
- Requires an argument with an `authenticatorType`:

| Property | Example | Description |
| --- | --- | --- |
| `authenticatorType` | "PIN" | The authenticator type
| `authenticatorId` | "PIN" | The authenticator ID, which distinguishes between authenticators of type "Custom". (Only required for custom authenticators)

```js
onegini.user.authenticators.setPreferred({
      authenticatorType: "PIN"
    })
    .then(() => {
      console.log("New authenticator set!");
    })
    .catch((err) => {
      console.log("Something went wrong! Error:", err);
    });
```

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8003 | The error code
| `description` | "Onegini: No registered user found." | Human readable error description

## `onegini.user.authenticators.registerNew`

- Used to register a new authenticator for the currently authenticated user.
- Return a new [AuthenticationHandler](AuthenticationHandler.md)
- Requires an argument with an `authenticatorType`:

| Property | Example | Description |
| --- | --- | --- |
| `authenticatorType` | "PIN" | The authenticator type
| `authenticatorId` | "PIN" | The authenticator ID, which distinguishes between authenticators of type "Custom". (Only required for custom authenticators)

```js
onegini.user.authenticators.registerNew({
      authenticatorType: "Fingerprint"
    })
    .onPinRequest((actions) => {
      var pin = prompt("Please enter your PIN code");
      actions.providePin(pin);
    })
    .onSuccess(() => {
      console.log("Authenticator registered!")
    })
    .onError((err) => {
      console.log("Failed to register authenticator!", err)
    });
```

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8003 | The error code
| `description` | "Onegini: No registered user found." | Human readable error description

## `onegini.user.authenticators.deregister`

- Used to deregister an authenticator for the currently authenticated user.
- Requires an argument with an `authenticatorType`:

| Property | Example | Description |
| --- | --- | --- |
| `authenticatorType` | "Fingerprint" | The authenticator type
| `authenticatorId` | "Fingerprint" | The authenticator ID, which distinguishes between authenticators of type "Custom". (Only required for custom authenticators)

```js
onegini.user.authenticators.deregister({
      authenticatorType: "Fingerprint"
    })
    .then(() => {
      console.log("Authenticator deregistered!");
    })
    .catch((err) => {
      console.log("Failed to deregister authenticator!", err);
    });
```

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8003 | The error code
| `description` | "Onegini: No registered user found." | Human readable error description
