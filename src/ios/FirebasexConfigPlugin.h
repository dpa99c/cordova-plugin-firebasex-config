#import <Cordova/CDVPlugin.h>

@interface FirebasexConfigPlugin : CDVPlugin

- (void)fetch:(CDVInvokedUrlCommand *)command;
- (void)activateFetched:(CDVInvokedUrlCommand *)command;
- (void)fetchAndActivate:(CDVInvokedUrlCommand *)command;
- (void)resetRemoteConfig:(CDVInvokedUrlCommand *)command;
- (void)getValue:(CDVInvokedUrlCommand *)command;
- (void)getAll:(CDVInvokedUrlCommand *)command;
- (void)getInfo:(CDVInvokedUrlCommand *)command;
- (void)setConfigSettings:(CDVInvokedUrlCommand *)command;
- (void)setDefaults:(CDVInvokedUrlCommand *)command;

@end
