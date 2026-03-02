/**
 * @file FirebasexConfigPlugin.m
 * @brief iOS implementation of the FirebaseX Remote Config Cordova plugin.
 */
#import "FirebasexConfigPlugin.h"
#import "FirebasexCorePlugin.h"
@import FirebaseRemoteConfig;

@implementation FirebasexConfigPlugin

/** Initialises the plugin. */
- (void)pluginInitialize {
    NSLog(@"FirebasexConfigPlugin pluginInitialize");
}

/**
 * Fetches Remote Config values from the server.
 * If args[0] is provided, uses it as the cache expiration duration in seconds.
 * Otherwise uses the default cache expiration.
 */
- (void)fetch:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        @try {
            FIRRemoteConfig *remoteConfig = [FIRRemoteConfig remoteConfig];

            if ([command.arguments count] > 0) {
                int expirationDuration = [[command.arguments objectAtIndex:0] intValue];
                [remoteConfig fetchWithExpirationDuration:expirationDuration
                                       completionHandler:^(FIRRemoteConfigFetchStatus status,
                                                           NSError *_Nullable error) {
                    if (status == FIRRemoteConfigFetchStatusSuccess && error == nil) {
                        [[FirebasexCorePlugin sharedInstance] sendPluginSuccess:command];
                    } else if (error != nil) {
                        [[FirebasexCorePlugin sharedInstance] handleEmptyResultWithPotentialError:error command:command];
                    } else {
                        [[FirebasexCorePlugin sharedInstance] sendPluginError:command];
                    }
                }];
            } else {
                [remoteConfig fetchWithCompletionHandler:^(FIRRemoteConfigFetchStatus status,
                                                           NSError *_Nullable error) {
                    if (status == FIRRemoteConfigFetchStatusSuccess && error == nil) {
                        [[FirebasexCorePlugin sharedInstance] sendPluginSuccess:command];
                    } else if (error != nil) {
                        [[FirebasexCorePlugin sharedInstance] handleEmptyResultWithPotentialError:error command:command];
                    } else {
                        [[FirebasexCorePlugin sharedInstance] sendPluginError:command];
                    }
                }];
            }
        } @catch (NSException *exception) {
            [[FirebasexCorePlugin sharedInstance] handlePluginExceptionWithContext:exception :command];
        }
    }];
}

/** Activates the most recently fetched Remote Config values. */
- (void)activateFetched:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        @try {
            FIRRemoteConfig *remoteConfig = [FIRRemoteConfig remoteConfig];
            [remoteConfig activateWithCompletion:^(BOOL changed, NSError *_Nullable error) {
                [[FirebasexCorePlugin sharedInstance] handleBoolResultWithPotentialError:error
                                                                                command:command
                                                                                 result:true];
            }];
        } @catch (NSException *exception) {
            [[FirebasexCorePlugin sharedInstance] handlePluginExceptionWithContext:exception :command];
        }
    }];
}

/**
 * Fetches and activates Remote Config values in one operation.
 * Returns true if fresh values were fetched from remote or pre-fetched data was used.
 */
- (void)fetchAndActivate:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        @try {
            FIRRemoteConfig *remoteConfig = [FIRRemoteConfig remoteConfig];
            [remoteConfig fetchAndActivateWithCompletionHandler:^(
                              FIRRemoteConfigFetchAndActivateStatus status,
                              NSError *_Nullable error) {
                bool activated =
                    (status == FIRRemoteConfigFetchAndActivateStatusSuccessFetchedFromRemote ||
                     status == FIRRemoteConfigFetchAndActivateStatusSuccessUsingPreFetchedData);
                [[FirebasexCorePlugin sharedInstance] handleBoolResultWithPotentialError:error
                                                                                command:command
                                                                                 result:activated];
            }];
        } @catch (NSException *exception) {
            [[FirebasexCorePlugin sharedInstance] handlePluginExceptionWithContext:exception :command];
        }
    }];
}

/** Resets Remote Config to defaults. Not currently available on iOS; returns an error. */
- (void)resetRemoteConfig:(CDVInvokedUrlCommand *)command {
    [[FirebasexCorePlugin sharedInstance] sendPluginErrorWithMessage:
        @"resetRemoteConfig is not currently available on iOS" :command];
}

/**
 * Gets a single Remote Config value by key, returned as a string.
 *
 * @param command args[0]: the parameter key.
 */
- (void)getValue:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        @try {
            NSString *key = [command.arguments objectAtIndex:0];
            FIRRemoteConfig *remoteConfig = [FIRRemoteConfig remoteConfig];
            NSString *value = remoteConfig[key].stringValue;
            CDVPluginResult *pluginResult =
                [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                  messageAsString:value];
            [self.commandDelegate sendPluginResult:pluginResult
                                        callbackId:command.callbackId];
        } @catch (NSException *exception) {
            [[FirebasexCorePlugin sharedInstance] handlePluginExceptionWithContext:exception :command];
        }
    }];
}

/**
 * Gets all Remote Config values as a dictionary of key-string pairs.
 * Keys are retrieved from default, remote, or static sources in that order of preference.
 */
- (void)getAll:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        @try {
            FIRRemoteConfig *remoteConfig = [FIRRemoteConfig remoteConfig];
            NSArray *defaultKeys = [remoteConfig allKeysFromSource:FIRRemoteConfigSourceDefault];
            NSArray *remoteKeys = [remoteConfig allKeysFromSource:FIRRemoteConfigSourceRemote];
            NSArray *staticKeys = [remoteConfig allKeysFromSource:FIRRemoteConfigSourceStatic];
            NSArray *keys = defaultKeys;
            if ([keys count] == 0) {
                keys = remoteKeys;
            }
            if ([keys count] == 0) {
                keys = staticKeys;
            }
            NSMutableDictionary *result = [[NSMutableDictionary alloc] init];
            for (NSString *key in keys) {
                [result setObject:remoteConfig[key].stringValue forKey:key];
            }
            CDVPluginResult *pluginResult =
                [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                              messageAsDictionary:result];
            [self.commandDelegate sendPluginResult:pluginResult
                                        callbackId:command.callbackId];
        } @catch (NSException *exception) {
            [[FirebasexCorePlugin sharedInstance] handlePluginExceptionWithContext:exception :command];
        }
    }];
}

/**
 * Gets metadata about the Remote Config instance.
 * Returns configSettings (minimumFetchInterval, fetchTimeout), fetchTimeMillis, and lastFetchStatus.
 */
- (void)getInfo:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        @try {
            FIRRemoteConfig *remoteConfig = [FIRRemoteConfig remoteConfig];
            NSInteger minimumFetchInterval = remoteConfig.configSettings.minimumFetchInterval;
            NSInteger fetchTimeout = remoteConfig.configSettings.fetchTimeout;
            NSDate *lastFetchTime = remoteConfig.lastFetchTime;
            FIRRemoteConfigFetchStatus lastFetchStatus = remoteConfig.lastFetchStatus;

            NSDictionary *configSettings = @{
                @"minimumFetchInterval" : [NSNumber numberWithInteger:minimumFetchInterval],
                @"fetchTimeout" : [NSNumber numberWithInteger:fetchTimeout],
            };

            NSDictionary *infoObject = @{
                @"configSettings" : configSettings,
                @"fetchTimeMillis" :
                    (lastFetchTime
                         ? [NSNumber numberWithInteger:(lastFetchTime.timeIntervalSince1970 * 1000)]
                         : [NSNull null]),
                @"lastFetchStatus" : [NSNumber numberWithInteger:(lastFetchStatus)],
            };

            CDVPluginResult *pluginResult =
                [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                              messageAsDictionary:infoObject];
            [self.commandDelegate sendPluginResult:pluginResult
                                        callbackId:command.callbackId];
        } @catch (NSException *exception) {
            [[FirebasexCorePlugin sharedInstance] handlePluginExceptionWithContext:exception :command];
        }
    }];
}

/**
 * Sets remote config settings for fetch timeout and minimum fetch interval.
 *
 * @param command args[0]: fetchTimeout in seconds, args[1]: minimumFetchInterval in seconds.
 */
- (void)setConfigSettings:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        @try {
            FIRRemoteConfig *remoteConfig = [FIRRemoteConfig remoteConfig];
            FIRRemoteConfigSettings *settings = [[FIRRemoteConfigSettings alloc] init];

            if ([command.arguments objectAtIndex:0] != [NSNull null]) {
                settings.fetchTimeout = [[command.arguments objectAtIndex:0] longValue];
            }
            if ([command.arguments objectAtIndex:1] != [NSNull null]) {
                settings.minimumFetchInterval = [[command.arguments objectAtIndex:1] longValue];
            }
            remoteConfig.configSettings = settings;
            [[FirebasexCorePlugin sharedInstance] sendPluginSuccess:command];
        } @catch (NSException *exception) {
            [[FirebasexCorePlugin sharedInstance] handlePluginExceptionWithContext:exception :command];
        }
    }];
}

/**
 * Sets default values for Remote Config parameters.
 *
 * @param command args[0]: NSDictionary of key-value default pairs.
 */
- (void)setDefaults:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        @try {
            NSDictionary *defaults = [command.arguments objectAtIndex:0];
            FIRRemoteConfig *remoteConfig = [FIRRemoteConfig remoteConfig];
            [remoteConfig setDefaults:defaults];
            [[FirebasexCorePlugin sharedInstance] sendPluginSuccess:command];
        } @catch (NSException *exception) {
            [[FirebasexCorePlugin sharedInstance] handlePluginExceptionWithContext:exception :command];
        }
    }];
}

@end
