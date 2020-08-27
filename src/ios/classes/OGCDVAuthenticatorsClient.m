/*
 * Copyright (c) 2017 Onegini B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "OGCDVAuthenticatorsClient.h"
#import "OGCDVConstants.h"
#import "OGCDVAuthenticatorsClientHelper.h"
#import "OGCDVUserClientHelper.h"

@implementation OGCDVAuthenticatorsClient {
}

- (void)getAll:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        ONGUserProfile *user = [OGCDVAuthenticatorsClient userProfileFromCommand:command];
        if (user == nil) {
            [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeProfileNotRegistered andMessage:OGCDVPluginErrDescriptionProfileNotRegistered];
            return;
        }

        NSSet<ONGAuthenticator *> *allAuthenticators = [[ONGUserClient sharedInstance] allAuthenticatorsForUser:user];
        NSMutableArray *result = [[NSMutableArray alloc] initWithCapacity:allAuthenticators.count];
        for (ONGAuthenticator *authenticator in allAuthenticators) {
            [result addObject:[OGCDVAuthenticatorsClientHelper dictionaryFromAuthenticator:authenticator]];
        }
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:result] callbackId:command.callbackId];
    }];
}

- (void)getRegistered:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        ONGUserProfile *user = [OGCDVAuthenticatorsClient userProfileFromCommand:command];
        if (user == nil) {
            [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeProfileNotRegistered andMessage:OGCDVPluginErrDescriptionProfileNotRegistered];
            return;
        }

        NSSet<ONGAuthenticator *> *registeredAuthenticators = [[ONGUserClient sharedInstance] registeredAuthenticatorsForUser:user];
        NSMutableArray *result = [[NSMutableArray alloc] initWithCapacity:registeredAuthenticators.count];
        for (ONGAuthenticator *authenticator in registeredAuthenticators) {
            [result addObject:[OGCDVAuthenticatorsClientHelper dictionaryFromAuthenticator:authenticator]];
        }
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:result] callbackId:command.callbackId];
    }];
}

- (void)getNotRegistered:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        ONGUserProfile *user = [OGCDVAuthenticatorsClient userProfileFromCommand:command];
        if (user == nil) {
            [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeProfileNotRegistered andMessage:OGCDVPluginErrDescriptionProfileNotRegistered];
            return;
        }

        NSSet<ONGAuthenticator *> *nonRegisteredAuthenticators = [[ONGUserClient sharedInstance] nonRegisteredAuthenticatorsForUser:user];
        NSMutableArray *result = [[NSMutableArray alloc] initWithCapacity:nonRegisteredAuthenticators.count];
        for (ONGAuthenticator *authenticator in nonRegisteredAuthenticators) {
            [result addObject:[OGCDVAuthenticatorsClientHelper dictionaryFromAuthenticator:authenticator]];
        }
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:result] callbackId:command.callbackId];
    }];
}

- (void)getPreferred:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        ONGUserProfile *user = [[ONGUserClient sharedInstance] authenticatedUserProfile];
        if (user == nil) {
            [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeNoUserAuthenticated andMessage:OGCDVPluginErrDescriptionNoUserAuthenticated];
            return;
        }

        ONGAuthenticator *authenticator = [[ONGUserClient sharedInstance] preferredAuthenticator];
        NSDictionary *result = [OGCDVAuthenticatorsClientHelper dictionaryFromAuthenticator:authenticator];

        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                             messageAsDictionary:result]
                                    callbackId:command.callbackId];
    }];
}

- (void)setPreferred:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        ONGUserProfile *user = [[ONGUserClient sharedInstance] authenticatedUserProfile];
        if (user == nil) {
            [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeNoUserAuthenticated andMessage:OGCDVPluginErrDescriptionNoUserAuthenticated];
            return;
        }

        NSDictionary *options = command.arguments[0];
        NSSet<ONGAuthenticator *> *registeredAuthenticators = [[ONGUserClient sharedInstance] registeredAuthenticatorsForUser:user];
        ONGAuthenticator *authenticator = [OGCDVAuthenticatorsClientHelper authenticatorFromArguments:registeredAuthenticators options:options];

        if (authenticator == nil) {
            [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeNoSuchAuthenticator
                                    andMessage:OGCDVPluginErrDescriptionNoSuchAuthenticator];
            return;
        }

        [[ONGUserClient sharedInstance] setPreferredAuthenticator:authenticator];
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    }];
}

+ (ONGUserProfile *)userProfileFromCommand:(CDVInvokedUrlCommand *)command
{
    NSDictionary *options = command.arguments[0];
    NSString *profileId = options[OGCDVPluginKeyProfileId];
    return [OGCDVUserClientHelper getRegisteredUserProfile:profileId];
}

@end
