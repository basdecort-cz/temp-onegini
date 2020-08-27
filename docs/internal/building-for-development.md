# Building for development

This chapter shortly describes how to build the plugin for development.

## Introduction

The Cordova plugin uses Webpack to manage it's dependencies and minify javascripts and make sure that everything can be properly resolved on the devices

- Webpack: Used to tie everything together. pack all the dependencies into one file, minify the javascript, trigger babel, etc.
- Babel: Used to transpile new JS syntax into something that an older JS engine on old devices understands.
- Babel polyfill: Used to import any polyfills that are available currently.
- Babel preset env: Used to set specific devices that we want to target our JS for.


## Building
 
When you make a release the build is automatically triggered so then nothing is necessary. However, when developing you must manually trigger the following
command before you add the plugin to any project.

```bash
npm run build
```

the build script will trigger a webpack build for development. When you want to let Webpack automatically build when one of the JS files has changed you can 
run the following script:

```bash
webpack --watch
```