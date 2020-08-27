# Get Identity Providers

<!-- toc -->

## Introduction

**OneginiIdentityProvider** interface allows you to take control over of user registration possibilities offered by the **Token Server**. The interface is 
used to specify which identity provider should be used during user registration. Identity providers can be created in the **Token Server** -> 
**Configuration** -> **Identity Providers**.

## `onegini.user.getIdentityProviders`

- Used to get an array of all existing identity providers.

```js
onegini.user.getIdentityProviders()
    .then((identityProviders) => {
      console.log("Available identity providers: ", identityProviders);
    })
    .catch((err) => {
      console.log("Error!\n\n", err.description)
    });
```

The success callback contains an array of objects with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `id` | 78365-78634-387bc-cb674 | A unique id of the identity provider
| `name` | LDAP | A human-readable name of the identity provider

The error callback contains an object with these properties:

| Property | Example | Description |
| --- | --- | --- |
| `code` | 8000 | The error code
| `description` | "Onegini: Internal plugin error." | Human-readable error description
