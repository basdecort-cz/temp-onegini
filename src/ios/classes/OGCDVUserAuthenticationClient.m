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

#import "OGCDVUserAuthenticationClient.h"
#import "OGCDVUserClientHelper.h"
#import "OGCDVConstants.h"
#import "OGCDVAuthenticatorsClientHelper.h"

static int const ARG_INDEX_AUTHENTICATOR = 1;

@implementation OGCDVUserAuthenticationClient

- (void)getAuthenticatedUserProfile:(CDVInvokedUrlCommand *)command
{
    ONGUserProfile *authenticatedUserProfile = [[ONGUserClient sharedInstance] authenticatedUserProfile];
    if (authenticatedUserProfile == nil) {
        [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeNoUserAuthenticated andMessage:OGCDVPluginErrDescriptionNoUserAuthenticated];
    } else {
        NSDictionary *result = @{OGCDVPluginKeyProfileId: authenticatedUserProfile.profileId};
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result] callbackId:command.callbackId];
    }
}

- (void)getImplicitlyAuthenticatedUserProfile:(CDVInvokedUrlCommand *)command
{
    ONGUserProfile *implicitlyAuthenticatedUserProfile = [[ONGUserClient sharedInstance] implicitlyAuthenticatedUserProfile];
    if (implicitlyAuthenticatedUserProfile == nil) {
        [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeNoUserAuthenticated andMessage:OGCDVPluginErrDescriptionNoUserAuthenticated];
    } else {
        NSDictionary *result = @{OGCDVPluginKeyProfileId: implicitlyAuthenticatedUserProfile.profileId};
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result] callbackId:command.callbackId];
    }
}

- (void)authenticateImplicitly:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        NSDictionary *options = command.arguments[0];
        NSString *profileId = options[OGCDVPluginKeyProfileId];
        NSArray *scopes = options[OGCDVPluginKeyScopes];

        ONGUserProfile *user = [OGCDVUserClientHelper getRegisteredUserProfile:profileId];
        if (user == nil) {
            [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeProfileNotRegistered
                                    andMessage:OGCDVPluginErrDescriptionProfileNotRegistered];
            return;
        }

        [[ONGUserClient sharedInstance] implicitlyAuthenticateUser:user scopes:scopes completion:^(BOOL success, NSError *_Nonnull error) {
            if (error != nil || !success) {
                [self sendErrorResultForCallbackId:command.callbackId withError:error];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            }
        }];
    }];
}

- (void)authenticate:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        NSDictionary *options = command.arguments[0];
        NSString *profileId = options[OGCDVPluginKeyProfileId];

        self.authenticationCallbackId = command.callbackId;

        ONGUserProfile *user = [OGCDVUserClientHelper getRegisteredUserProfile:profileId];
        if (user == nil) {
            [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeProfileNotRegistered
                                    andMessage:OGCDVPluginErrDescriptionProfileNotRegistered];
            return;
        }


        if ([self shouldUseSpecifiedAuthenticator:command.arguments]) {
            ONGAuthenticator *authenticator = [self getSpecifiedAuthenticator:command.arguments forUserProfile:user];
            if (authenticator == nil) {
                [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeNoSuchAuthenticator
                                        andMessage:OGCDVPluginErrDescriptionNoSuchAuthenticator];
                return;
            }

            [[ONGUserClient sharedInstance] authenticateUserWithAuthenticator:authenticator profile:user delegate:self];
        } else {
            [[ONGUserClient sharedInstance] authenticateUser:user delegate:self];
        }
    }];
}

- (BOOL)shouldUseSpecifiedAuthenticator:(NSArray *)args
{
    return args.count > ARG_INDEX_AUTHENTICATOR;
}

- (ONGAuthenticator *)getSpecifiedAuthenticator:(NSArray *)args forUserProfile:(ONGUserProfile *)profile
{
    NSDictionary *options = args[ARG_INDEX_AUTHENTICATOR];
    if (![options isKindOfClass:[NSDictionary class]]) {
        return nil;
    }

    NSSet<ONGAuthenticator *> *authenticators = [[ONGUserClient sharedInstance] allAuthenticatorsForUser:profile];
    return [OGCDVAuthenticatorsClientHelper authenticatorFromArguments:authenticators options:options];
}

- (void)providePin:(CDVInvokedUrlCommand *)command
{
    if (!self.pinChallenge) {
        [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeProvidePinNoAuthenticationInProgress
                                andMessage:OGCDVPluginErrDescriptionProvidePinNoAuthenticationInProgress];
        return;
    }
    [self.commandDelegate runInBackground:^{
        NSDictionary *options = command.arguments[0];
        NSString *pin = options[OGCDVPluginKeyPin];
        [self.pinChallenge.sender respondWithPin:pin challenge:self.pinChallenge];
    }];
}

- (void)respondToFingerprintRequest:(CDVInvokedUrlCommand *)command
{
    if (!self.fingerprintChallenge) {
        [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeFingerprintNoAuthenticationInProgress
                                andMessage:OGCDVPluginErrDescriptionFingerprintNoAuthenticationInProgress];
        return;
    }

    [self.commandDelegate runInBackground:^{
        NSDictionary *options = command.arguments[0];
        BOOL shouldNotAccept = ![options[OGCDVPluginKeyAccept] boolValue];
        NSString *prompt = options[OGCDVPluginKeyPrompt];

        if (shouldNotAccept) {
            [self.fingerprintChallenge.sender cancelChallenge:self.fingerprintChallenge];
            return;
        }

        if (prompt == nil) {
            [self.fingerprintChallenge.sender respondWithDefaultPromptForChallenge:self.fingerprintChallenge];
        } else {
            [self.fingerprintChallenge.sender respondWithPrompt:prompt challenge:self.fingerprintChallenge];
        }
    }];
}

- (void)fallbackToPin:(CDVInvokedUrlCommand *)command
{
    if (self.fingerprintChallenge) {
        [self.commandDelegate runInBackground:^{
            [self.fingerprintChallenge.sender respondWithPinFallbackForChallenge:self.fingerprintChallenge];
        }];
    }
}

- (void)logout:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        [[ONGUserClient sharedInstance] logoutUser:^(ONGUserProfile *_Nonnull userProfile, NSError *_Nullable error) {
            if (error == nil) {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            } else {
                [self sendErrorResultForCallbackId:command.callbackId withError:error];
            }
        }];
    }];
}

- (void)cancelFlow:(CDVInvokedUrlCommand *)command
{
    if (self.fingerprintChallenge) {
        [self.fingerprintChallenge.sender cancelChallenge:self.fingerprintChallenge];
    } else if (self.pinChallenge) {
        [self.pinChallenge.sender cancelChallenge:self.pinChallenge];
    }
}

@end
