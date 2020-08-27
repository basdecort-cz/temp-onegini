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
#import "OGCDVConstants.h"

@implementation OGCDVAuthenticationDelegateHandler {
}

#pragma mark - ONGAuthenticationDelegate

- (void)userClient:(ONGUserClient *)userClient didAuthenticateUser:(ONGUserProfile *)userProfile info:(nullable ONGCustomInfo *)customAuthInfo
{
    self.pinChallenge = nil;
    self.fingerprintChallenge = nil;
    NSDictionary *result = @{
        OGCDVPluginKeyEvent: OGCDVPluginEventSuccess
    };

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result] callbackId:self.authenticationCallbackId];
}

- (void)userClient:(ONGUserClient *)userClient didFailToAuthenticateUser:(ONGUserProfile *)userProfile error:(NSError *)error
{
    [self sendErrorResultForCallbackId:self.authenticationCallbackId withError:error];
    self.pinChallenge = nil;
    self.fingerprintChallenge = nil;
}

- (void)userClient:(ONGUserClient *)userClient didReceivePinChallenge:(ONGPinChallenge *)challenge
{
    self.pinChallenge = challenge;
    self.fingerprintChallenge = nil;

    NSMutableDictionary *result = [[NSMutableDictionary alloc] init];
    result[OGCDVPluginKeyEvent] = OGCDVPluginEventPinRequest;
    result[OGCDVPluginKeyMaxFailureCount] = @(challenge.maxFailureCount);
    result[OGCDVPluginKeyRemainingFailureCount] = @(challenge.remainingFailureCount);

    if (challenge.error && challenge.error.code == ONGAuthenticationErrorInvalidPin) {
        result[OGCDVPluginKeyErrorCode] = @(OGCDVPluginErrCodeIncorrectPin);
        result[OGCDVPluginKeyErrorDescription] = OGCDVPluginErrDescriptionIncorrectPin;
    }

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.authenticationCallbackId];
}

- (void)userClient:(ONGUserClient *)userClient didReceiveFingerprintChallenge:(ONGFingerprintChallenge *)challenge
{
    self.pinChallenge = nil;
    self.fingerprintChallenge = challenge;

    NSDictionary *result = @{
        OGCDVPluginKeyEvent: OGCDVPluginEventFingerprintRequest
    };
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.authenticationCallbackId];
}
@end
