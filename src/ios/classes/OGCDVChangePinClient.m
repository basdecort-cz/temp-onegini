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

#import "OGCDVAuthenticationDelegateHandler.h"
#import "OGCDVChangePinClient.h"
#import "OGCDVConstants.h"

@implementation OGCDVChangePinClient {
}

- (void)start:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        self.startCallbackId = command.callbackId;
        [[ONGUserClient sharedInstance] changePin:self];
    }];
}

- (void)providePin:(CDVInvokedUrlCommand *)command
{
    if (!self.pinChallenge) {
        [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeProvidePinNoAuthenticationInProgress andMessage:OGCDVPluginErrDescriptionProvidePinNoAuthenticationInProgress];
        return;
    }
    [self.commandDelegate runInBackground:^{
        NSDictionary *options = command.arguments[0];
        NSString *pin = options[OGCDVPluginKeyPin];
        [self.pinChallenge.sender respondWithPin:pin challenge:self.pinChallenge];
    }];
}

- (void)createPin:(CDVInvokedUrlCommand *)command
{
    if (!self.createPinChallenge) {
        [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeCreatePinNoRegistrationInProgress andMessage:OGCDVPluginErrDescriptionCreatePinNoRegistrationInProgress];
        return;
    }

    [self.commandDelegate runInBackground:^{
        NSDictionary *options = command.arguments[0];
        NSString *pin = options[OGCDVPluginKeyPin];
        [self.createPinChallenge.sender respondWithCreatedPin:pin challenge:self.createPinChallenge];
    }];
}

- (void)cancelFlow:(CDVInvokedUrlCommand *)command
{
    if (self.pinChallenge) {
        [self.pinChallenge.sender cancelChallenge:self.pinChallenge];
    }

    if (self.createPinChallenge) {
        [self.createPinChallenge.sender cancelChallenge:self.createPinChallenge];
    }
}

#pragma mark - ONGChangePinDelegate

- (void)userClient:(ONGUserClient *)userClient didReceivePinChallenge:(ONGPinChallenge *)challenge
{
    self.pinChallenge = challenge;

    NSMutableDictionary *result = [[NSMutableDictionary alloc]init];
    result[OGCDVPluginKeyEvent] = OGCDVPluginEventPinRequest;
    result[OGCDVPluginKeyMaxFailureCount] = @(challenge.maxFailureCount);
    result[OGCDVPluginKeyRemainingFailureCount] = @(challenge.remainingFailureCount);

    if (challenge.error && challenge.error.code == ONGAuthenticationErrorInvalidPin) {
        result[OGCDVPluginKeyErrorCode] = @(OGCDVPluginErrCodeIncorrectPin);
        result[OGCDVPluginKeyErrorDescription] = OGCDVPluginErrDescriptionIncorrectPin;
    }

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.startCallbackId];
}

- (void)userClient:(ONGUserClient *)userClient didReceiveCreatePinChallenge:(ONGCreatePinChallenge *)challenge
{
    self.createPinChallenge = challenge;
    [self.viewController dismissViewControllerAnimated:YES completion:nil];

    NSMutableDictionary *result = [[NSMutableDictionary alloc] init];
    result[OGCDVPluginKeyEvent] = OGCDVPluginEventCreatePinRequest;
    result[OGCDVPluginKeyPinLength] = @(challenge.pinLength);

    if (challenge.error != nil) {
        result[OGCDVPluginKeyErrorCode] = @(challenge.error.code);
        result[OGCDVPluginKeyErrorDescription] = challenge.error.localizedDescription;
    }

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.startCallbackId];
}

- (void)userClient:(ONGUserClient *)userClient didChangePinForUser:(ONGUserProfile *)userProfile
{
    NSDictionary *result = @{
        OGCDVPluginKeyEvent: OGCDVPluginEventSuccess
    };

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.startCallbackId];

    self.createPinChallenge = nil;
    self.startCallbackId = nil;
}

- (void)userClient:(ONGUserClient *)userClient didFailToChangePinForUser:(ONGUserProfile *)userProfile error:(NSError *)error
{
    [self sendErrorResultForCallbackId:self.startCallbackId withError:error];
}

@end
