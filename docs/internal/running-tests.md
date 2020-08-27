# Running tests for the Cordova plugin

The Onegini Cordova plugin contains a test plugin with a lot of tests that verify whether the plugin correctly works. The plugin is build using the 
`cordova-plugin-test-framework` plugin. The actual tests are Jasmine tests.

In order to run the tests you need to have a Cordova application that includes the `onegini-cordova-plugin`. To this application you then need to add the test 
plugin. For example the [Cordova example app]({{book.example_app}}) is a nice app which can be used to run the tests.

## Prerequisites

You need to have access to the Onegini MSP test environment (currently this is located at: https://onegini-msp-snapshot.test.onegini.io).
  
Also the example application must be configured for this environment. If you don't use this environment multiple tests will fail

## Installation

>**Note**: All bash commands specified below must be executed from the directory where you cloned the Cordova example app. We assume that you have cloned the 
Cordova plugin in the following directory: `cordova-plugin-onegini`.

In order to install the test plugin you need to add the following Cordova plugins:
- Cordova test framework (`cordova-plugin-test-framework`)
- Onegini test plugin (`cordova-plugin-onegini-tests`)

```bash
cordova plugin add cordova-plugin-test-framework
cordova plugin add ../cordova-plugin-onegini/tests
```

## Running the tests

Once you have installed the test plugin you can just run the example app as you would normally do. You'll see that the example app starts with a different UI.
Just click the `Auto tests` button to start the automatic tests. Happy testing!

### Customising tests

In the test plugin js file (`tests.js`) there are a few boolean flags. Use these flags in order to enable / disable certain test scenarios. 