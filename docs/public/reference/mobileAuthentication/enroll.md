# Enroll for mobile authentication

<!-- toc -->

Authenticated users can enroll for mobile authentication, allowing them to execute mobile authentication with OTP. See the [mobile authentication topic guide](../../topics/mobile-authentication.md) 
for more information on mobile authentication in general.

## `onegini.mobileAuth.enroll`

This function enrolls the currently logged in user for mobile authentication.

**Example enrollment for a logged in user:**

```js
onegini.mobileAuth.enroll()
    .then(() => {
      alert("Enrollment success!");
    })
    .catch((err) => {
      alert("Enrollment error!\n\n" + err.description);
    });
```

The error callback contains an object with the following properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8000 | The error code
| `description` | "Onegini: Internal plugin error" | Human readable error description
