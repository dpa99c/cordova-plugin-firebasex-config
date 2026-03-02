/**
 * @fileoverview Cordova JavaScript interface for the FirebaseX Remote Config plugin.
 *
 * Provides methods for fetching, activating, and reading Firebase Remote Config
 * values, as well as setting defaults and configuration options.
 *
 * @module firebasex-config
 * @see https://firebase.google.com/docs/remote-config
 */

var exec = require("cordova/exec");

/** @private Cordova service name registered in plugin.xml. */
var SERVICE = "FirebasexConfigPlugin";

/**
 * Coerces "true"/"false" strings to boolean, then applies double-negation.
 *
 * @private
 * @param {*} value - The value to coerce.
 * @returns {boolean} The boolean result.
 */
var ensureBoolean = function (value) {
    if (value === "true") {
        value = true;
    } else if (value === "false") {
        value = false;
    }
    return !!value;
};

/**
 * Wraps a callback so that its result is passed through {@link ensureBoolean}.
 *
 * @private
 * @param {function} callback - The original callback.
 * @returns {function} A wrapper that calls callback with a boolean.
 */
var ensureBooleanFn = function (callback) {
    return function (result) {
        callback(ensureBoolean(result));
    };
};

/**
 * Fetches Remote Config values from the server.
 *
 * @param {number} [cacheExpirationSeconds] - Optional cache timeout in seconds.
 *   If omitted, the default cache expiration is used.
 * @param {function} success - Called on successful fetch.
 * @param {function} error - Called with an error message on failure.
 */
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

/**
 * Activates the most recently fetched Remote Config values.
 *
 * @param {function} success - Called with a boolean: {@code true} if previously
 *   fetched values were activated successfully.
 * @param {function} error - Called with an error message on failure.
 */
exports.activateFetched = function (success, error) {
    exec(ensureBooleanFn(success), error, SERVICE, "activateFetched", []);
};

/**
 * Fetches and activates Remote Config values in a single call.
 *
 * @param {function} success - Called with a boolean: {@code true} if fresh values
 *   were fetched and activated, {@code false} if cached values were used.
 * @param {function} error - Called with an error message on failure.
 */
exports.fetchAndActivate = function (success, error) {
    exec(ensureBooleanFn(success), error, SERVICE, "fetchAndActivate", []);
};

/**
 * Resets all Remote Config values back to defaults.
 * Note: not currently available on iOS.
 *
 * @param {function} success - Called with a boolean on success.
 * @param {function} error - Called with an error message on failure.
 */
exports.resetRemoteConfig = function (success, error) {
    exec(ensureBooleanFn(success), error, SERVICE, "resetRemoteConfig", []);
};

/**
 * Gets a single Remote Config value by key.
 *
 * @param {string} key - The parameter key.
 * @param {function} success - Called with the value as a string.
 * @param {function} error - Called with an error message on failure.
 */
exports.getValue = function (key, success, error) {
    exec(success, error, SERVICE, "getValue", [key]);
};

/**
 * Gets metadata about the Remote Config instance including fetch time and status.
 *
 * @param {function} success - Called with an object containing:
 *   - {@code configSettings}: object with fetch settings
 *   - {@code fetchTimeMillis}: last fetch timestamp in milliseconds
 *   - {@code lastFetchStatus}: numeric status code of last fetch
 * @param {function} error - Called with an error message on failure.
 */
exports.getInfo = function (success, error) {
    exec(success, error, SERVICE, "getInfo", []);
};

/**
 * Sets the fetch timeout and minimum fetch interval for Remote Config.
 *
 * @param {number} fetchTimeout - Fetch timeout in seconds.
 * @param {number} minimumFetchInterval - Minimum interval between fetches in seconds.
 * @param {function} success - Called on success.
 * @param {function} error - Called with an error message on failure.
 */
exports.setConfigSettings = function (fetchTimeout, minimumFetchInterval, success, error) {
    exec(success, error, SERVICE, "setConfigSettings", [fetchTimeout, minimumFetchInterval]);
};

/**
 * Sets default values for Remote Config parameters.
 *
 * @param {Object} defaults - Key-value pairs of default parameter values.
 * @param {function} success - Called on success.
 * @param {function} error - Called with an error message on failure.
 */
exports.setDefaults = function (defaults, success, error) {
    exec(success, error, SERVICE, "setDefaults", [defaults]);
};

/**
 * Gets all Remote Config parameter values as key-value pairs.
 *
 * @param {function} success - Called with an object mapping keys to their string values.
 * @param {function} error - Called with an error message on failure.
 */
exports.getAll = function (success, error) {
    exec(success, error, SERVICE, "getAll", []);
};
