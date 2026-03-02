/**
 * @file FirebasexConfigPlugin.h
 * @brief Cordova plugin interface for Firebase Remote Config on iOS.
 *
 * Provides fetching, activating, and reading Remote Config values,
 * setting defaults and configuration options.
 */
#import <Cordova/CDVPlugin.h>

/**
 * @brief Cordova plugin class for Firebase Remote Config on iOS.
 *
 * @see https://firebase.google.com/docs/remote-config
 */
@interface FirebasexConfigPlugin : CDVPlugin

/** Fetches Remote Config values, optionally with a custom cache expiration. @param command args[0]: optional cacheExpirationSeconds. */
- (void)fetch:(CDVInvokedUrlCommand *)command;
/** Activates the most recently fetched values. Returns boolean. */
- (void)activateFetched:(CDVInvokedUrlCommand *)command;
/** Fetches and activates in a single call. Returns boolean. */
- (void)fetchAndActivate:(CDVInvokedUrlCommand *)command;
/** Resets Remote Config to defaults. Note: not currently available on iOS. */
- (void)resetRemoteConfig:(CDVInvokedUrlCommand *)command;
/** Gets a single value by key. @param command args[0]: key string. */
- (void)getValue:(CDVInvokedUrlCommand *)command;
/** Gets all parameter values as key-value pairs. */
- (void)getAll:(CDVInvokedUrlCommand *)command;
/** Gets metadata including fetch time, status, and settings. */
- (void)getInfo:(CDVInvokedUrlCommand *)command;
/** Sets fetch timeout and minimum fetch interval. @param command args[0]: fetchTimeout, args[1]: minimumFetchInterval. */
- (void)setConfigSettings:(CDVInvokedUrlCommand *)command;
/** Sets default parameter values. @param command args[0]: NSDictionary of defaults. */
- (void)setDefaults:(CDVInvokedUrlCommand *)command;

@end
