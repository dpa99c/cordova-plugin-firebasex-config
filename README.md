# cordova-plugin-firebasex-config

Firebase Remote Config module for `cordova-plugin-firebasex`.

## Installation

```bash
cordova plugin add cordova-plugin-firebasex-config
```

This plugin depends on `cordova-plugin-firebasex-core` which will be installed automatically.

## API

### fetch([cacheExpirationSeconds], success, error)
Fetch Remote Config values from the server. Optionally specify a cache expiration time in seconds.

```javascript
FirebasexConfigPlugin.fetch(3600, function() {
    console.log("Fetched successfully");
}, function(error) {
    console.error(error);
});
```

### activateFetched(success, error)
Activate the last fetched Remote Config values.

### fetchAndActivate(success, error)
Fetch and immediately activate Remote Config values.

### resetRemoteConfig(success, error)
Reset all Remote Config values to defaults. Note: not available on iOS.

### getValue(key, success, error)
Get a single Remote Config value by key.

### getAll(success, error)
Get all active Remote Config values.

### getInfo(success, error)
Get metadata about the Remote Config state (fetch time, status, settings).

### setConfigSettings(fetchTimeout, minimumFetchInterval, success, error)
Configure the Remote Config client timeouts and intervals.

### setDefaults(defaults, success, error)
Set local default values for Remote Config keys.

```javascript
FirebasexConfigPlugin.setDefaults({
    welcome_message: "Hello!",
    feature_enabled: true
}, function() {
    console.log("Defaults set");
}, function(error) {
    console.error(error);
});
```
