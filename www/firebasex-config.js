var exec = require("cordova/exec");
var SERVICE = "FirebasexConfigPlugin";

var ensureBoolean = function (value) {
    if (value === "true") {
        value = true;
    } else if (value === "false") {
        value = false;
    }
    return !!value;
};

var ensureBooleanFn = function (callback) {
    return function (result) {
        callback(ensureBoolean(result));
    };
};

exports.fetch = function (cacheExpirationSeconds, success, error) {
    var args = [];
    if (typeof cacheExpirationSeconds === "number") {
        args.push(cacheExpirationSeconds);
    } else {
        error = success;
        success = cacheExpirationSeconds;
    }
    exec(success, error, SERVICE, "fetch", args);
};

exports.activateFetched = function (success, error) {
    exec(ensureBooleanFn(success), error, SERVICE, "activateFetched", []);
};

exports.fetchAndActivate = function (success, error) {
    exec(ensureBooleanFn(success), error, SERVICE, "fetchAndActivate", []);
};

exports.resetRemoteConfig = function (success, error) {
    exec(ensureBooleanFn(success), error, SERVICE, "resetRemoteConfig", []);
};

exports.getValue = function (key, success, error) {
    exec(success, error, SERVICE, "getValue", [key]);
};

exports.getInfo = function (success, error) {
    exec(success, error, SERVICE, "getInfo", []);
};

exports.setConfigSettings = function (fetchTimeout, minimumFetchInterval, success, error) {
    exec(success, error, SERVICE, "setConfigSettings", [fetchTimeout, minimumFetchInterval]);
};

exports.setDefaults = function (defaults, success, error) {
    exec(success, error, SERVICE, "setDefaults", [defaults]);
};

exports.getAll = function (success, error) {
    exec(success, error, SERVICE, "getAll", []);
};
