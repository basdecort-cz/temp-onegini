# User registration

## Introduction

User registration is a fundamental part of the Onegini Mobile Security Platform. As developer you have a couple options to handle this process:

## Using the default WebView.

Example to let the user register using a simple WebView or browser:
```js
onegini.user.register({
    id: identityProviderId
  })
  .onCreatePinRequest((actions, options) => {
    let pin = prompt(`Create your {options.pinLength} digit pin`);
    actions.createPin(pin);
  })
  .onSuccess(() => {
    alert('Registration success!');
  })
  .onError((err) => {
    alert('Registration failed: ' + err.description);
    console.error(err);
  });
```

This will open the default system browser on Android and a `WKWebView` on iOS.

## Using SFSafariViewController on iOS

The `WKWebView` on iOS can be replaced with `SFSafariViewController`.
This allows your users to access Safari extensions like password managers,
it also allows users to bring their cookies from your website so they might continue an already existing session started in Safari.

To enable `SFSafariViewController` set the following preference in your `config.xml`:
```xml
<platform name="ios">
  <preference name="OneginiWebView" value="SFSafariViewController"/>
</platform>
```

iOS also needs to know about your redirect URL scheme. This is done through setting `CFBundleURLSchemes` in your `-Info.plist`,
refer to [Apple's documentation](https://developer.apple.com/library/content/documentation/Carbon/Conceptual/LaunchServicesConcepts/LSCConcepts/LSCConcepts.html) 
for further details.

We recommend to use a Cordova plugin to modify your `-Info.plist` file and not do it manually. The 
[cordova-custom-config plugin](https://www.npmjs.com/package/cordova-custom-config) is a good plugin that can make the necessary changes.

The example below shows a part of a `config.xml` to configure the custom URL scheme using the cordova-custom-config plugin:

```xml
    <config-file platform="ios" target="*-Info.plist" parent="CFBundleURLTypes">
      <array>
        <dict>
          <key>CFBundleURLSchemes</key>
          <array>
            <string>__YOUR_CUSTOM_SCHEME__</string>
          </array>
        </dict>
      </array>
    </config-file>
```

The plugin will now use `SFSafariViewController` to open the registration endpoint. Note that the `SFSafariViewController` is only available on iOS 9 and later, 
the plugin will fall back to WKWebView on iOS 8.

## Using a JavaScript callback

User registration can be handled entirely through JS. This way you have full control over the registration flow.

To prevent the plugin from opening any browser, set the `OneginiWebView` preference to `disabled` in your `config.xml`:
```xml
<preference name="OneginiWebView" value="disabled"/>
```

Handle the `onRegistrationRequest` event and obtain the callback URL yourself:
```js
onegini.user.register({
    id: identityProviderId
  })
  .onRegistrationRequest((actions, options) => {
    // Some logic to fetch the callback URL
    let registrationUrl = registerWithUrl(options.url);
    actions.handleRegistrationUrl(registrationUrl);
  })
  .onCreatePinRequest((actions, options) => {
    let pin = prompt(`Create your {options.pinLength} digit pin`);
    actions.createPin(pin);
  })
  .onSuccess((result) => {
    alert('Registration success!');
  })
  .onError((err) => {
    alert('Registration failed: ' + err.description);
    console.error(err);
  });
```

## Choosing an identity provider

To select an identity provider which will be used during the registration process you need to pass its id in place of the `identityProviderId` param. 
If this parameter isn't specified or if its value is `null` the default identity provider set on the **Token Server** will be used. To learn more about identity 
providers please read the [getIdentityProviders](../reference/user/getIdentityProviders.md) reference guide. 
