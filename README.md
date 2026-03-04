# cordova-plugin-firebasex-config

[![npm version](https://img.shields.io/npm/v/cordova-plugin-firebasex-config.svg)](https://www.npmjs.com/package/cordova-plugin-firebasex-config)

Firebase Remote Config module for the [modular FirebaseX Cordova plugin suite](https://github.com/dpa99c/cordova-plugin-firebasex#modular-plugins).

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Installation](#installation)
- [API](#api)
  - [fetch](#fetch)
  - [activateFetched](#activatefetched)
  - [fetchAndActivate](#fetchandactivate)
  - [resetRemoteConfig](#resetremoteconfig)
  - [getValue](#getvalue)
  - [getInfo](#getinfo)
  - [getAll](#getall)
  - [setConfigSettings](#setconfigsettings)
  - [setDefaults](#setdefaults)
- [Reporting issues](#reporting-issues)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Installation

Install the plugin by adding it to your project's config.xml:

    cordova plugin add cordova-plugin-firebasex-config

or by running:

    cordova plugin add cordova-plugin-firebasex-config

**This module depends on `cordova-plugin-firebasex-core` which will be installed automatically as a dependency.**

## Plugin variables

| Variable | Default | Description |
|---|---|---|
| `ANDROID_FIREBASE_CONFIG_VERSION` | `23.0.1` | Android Firebase Remote Config SDK version. |
| `IOS_FIREBASE_SDK_VERSION` | `12.9.0` | iOS Firebase SDK version (for config pod). |

# API

The following methods are available via the `FirebasexConfig` global object.

## fetch

Fetch Remote Config parameter values for your app:

**Parameters**:

-   {integer} cacheExpirationSeconds (optional) - cache expiration in seconds.
    According to [the documentation](https://firebase.google.com/docs/remote-config/use-config-web#throttling) the default behavior is to cache for 12 hours, so if you want to quickly detect changes make sure you set this value.
-   {function} success - callback function on successfully fetching remote config
-   {function} error - callback function which will be passed a {string} error message as an argument

```javascript
FirebasexConfig.fetch(
    function () {
        // success callback
    },
    function () {
        // error callback
    }
);
// or, specify the cacheExpirationSeconds
FirebasexConfig.fetch(
    600,
    function () {
        // success callback
    },
    function () {
        // error callback
    }
);
```

## activateFetched

Activate the Remote Config fetched config:

**Parameters**:

-   {function} success - callback function which will be passed a {boolean} argument indicating whether result the current call activated the fetched config.
-   {function} error - callback function which will be passed a {string} error message as an argument

```javascript
FirebasexConfig.activateFetched(
    function (activated) {
        // activated will be true if there was a fetched config activated,
        // or false if no fetched config was found, or the fetched config was already activated.
        console.log(activated);
    },
    function (error) {
        console.error(error);
    }
);
```

## fetchAndActivate

Fetches and activates the Remote Config in a single operation.

**Parameters**:

-   {function} success - callback function which will be passed a {boolean} argument indicating whether result the current call activated the fetched config.
-   {function} error - callback function which will be passed a {string} error message as an argument

```javascript
FirebasexConfig.fetchAndActivate(
    function (activated) {
        // activated will be true if there was a fetched config activated,
        // or false if no fetched config was found, or the fetched config was already activated.
        console.log(activated);
    },
    function (error) {
        console.error(error);
    }
);
```

## resetRemoteConfig

Deletes all activated, fetched and defaults configs and resets all Firebase Remote Config settings.

Android only.

**Parameters**:

-   {function} success - callback function to call on successful reset.
-   {function} error - callback function which will be passed a {string} error message as an argument

```javascript
FirebasexConfig.resetRemoteConfig(
    function () {
        console.log("Successfully reset remote config");
    },
    function (error) {
        console.error("Error resetting remote config: " + error);
    }
);
```

## getValue

Retrieve a Remote Config value:

**Parameters**:

-   {string} key - key for which to fetch associated value
-   {function} success - callback function which will be passed a {string} argument containing the value stored against the specified key.
    If the expected value is of a different primitive type (e.g. `boolean`, `integer`) you should cast the value to the appropriate type.
-   {function} error - callback function which will be passed a {string} error message as an argument

```javascript
FirebasexConfig.getValue(
    "key",
    function (value) {
        console.log(value);
    },
    function (error) {
        console.error(error);
    }
);
```

## getInfo

Get the current state of the FirebaseRemoteConfig singleton object:

**Parameters**:

-   {function} success - callback function which will be passed an {object} argument containing the state info
-   {function} error - callback function which will be passed a {string} error message as an argument

```javascript
FirebasexConfig.getInfo(
    function (info) {
        // how many (secs) fetch cache is valid and data will not be refetched
        console.log(info.configSettings.minimumFetchInterval);
        // value in seconds to abandon a pending fetch request made to the backend
        console.log(info.configSettings.fetchTimeout);
        // the timestamp (milliseconds since epoch) of the last successful fetch
        console.log(info.fetchTimeMillis);
        // the status of the most recent fetch attempt (int)
        // 0 = Config has never been fetched.
        // 1 = Config fetch succeeded.
        // 2 = Config fetch failed.
        // 3 = Config fetch was throttled.
        console.log(info.lastFetchStatus);
    },
    function (error) {
        console.error(error);
    }
);
```

## getAll

Returns all Remote Config as key/value pairs.

**Parameters**:

-   {function} success - callback function which will be passed an {object} argument where key is the remote config key and value is the value as a string. If the expected key value is a different primitive type then cast it to the appropriate type.
-   {function} error - callback function which will be passed a {string} error message as an argument

```javascript
FirebasexConfig.getAll(
    function (values) {
        for (var key in values) {
            console.log(key + "=" + values[key]);
        }
    },
    function (error) {
        console.error(error);
    }
);
```

## setConfigSettings

Changes the default Remote Config settings:

-   Fetch timeout sets how long your app should wait for new Remote Config values before timing out.
    -   Useful when you don't want your application to wait longer than X seconds to fetch new Remote Config values
-   Minimum fetch interval sets the minimum interval for which you want to check for any new Remote Config parameter values.
    -   Keep in mind that setting too short an interval in production might cause your app to run into rate limits.

**Parameters**:

-   {integer} fetchTimeout - fetch timeout in seconds.
    -   Default is 60 seconds.
    -   Specify as `null` value to omit setting this value.
-   {integer} minimumFetchInterval - minimum fetch interval in seconds.
    -   Default is 12 hours.
    -   Specify as `null` value to omit setting this value.
    -   Set to `0` to disable minimum interval entirely (**DO NOT** do this in production)
-   {function} success - callback function to be call on successfully setting the remote config settings
-   {function} error - callback function which will be passed a {string} error message as an argument

```javascript
var fetchTimeout = 60;
var minimumFetchInterval = 3600;
FirebasexConfig.setConfigSettings(
    fetchTimeout,
    minimumFetchInterval,
    function () {
        console.log("Successfully set Remote Config settings");
    },
    function (error) {
        console.error("Error setting Remote Config settings: " + error);
    }
);
```

## setDefaults

Sets in-app default values for your Remote Config parameters until such time as values are populated from the remote service via a fetch/activate operation.

**Parameters**:

-   {object} defaults - object specifying the default remote config settings
    -   key is the name of your Remote Config parameter
    -   value is the default value
-   {function} success - callback function to be call on successfully setting the remote config parameter defaults
-   {function} error - callback function which will be passed a {string} error message as an argument

```javascript
// define defaults
var defaults = {
    my_int: 1,
    my_double: 3.14,
    my_boolean: true,
    my_string: "hello world",
    my_json: { foo: "bar" },
};
// set defaults
FirebasexConfig.setDefaults(defaults);
```

# Reporting issues

Before reporting an issue with this plugin, please do the following:
- Check the existing [issues](https://github.com/dpa99c/cordova-plugin-firebasex-config/issues) to see if the issue has already been reported.
- Check the [issue template](https://github.com/dpa99c/cordova-plugin-firebasex-config/issues/new/choose) and provide all requested information.
- The more information and context you provide, the easier it is for the maintainers to understand the issue and provide a resolution.
