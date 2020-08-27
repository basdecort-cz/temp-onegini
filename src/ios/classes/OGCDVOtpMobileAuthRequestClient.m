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

#import "OGCDVOtpMobileAuthRequestClient.h"
#import "OGCDVConstants.h"
#import "CDVPlugin+OGCDV.h"

@implementation OGCDVOtpMobileAuthRequestClient {
}
- (void)start:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        NSDictionary *options = command.arguments[0];
        NSString *otp = options[OGCDVPluginKeyOtp];

        ONGUserProfile *user = [[ONGUserClient sharedInstance] authenticatedUserProfile];
        if (user == nil) {
            [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeNoUserAuthenticated andMessage:OGCDVPluginErrDescriptionNoUserAuthenticated];
            return;
        }
        self.callbackId = command.callbackId;

        [[ONGUserClient sharedInstance] handleOTPMobileAuthRequest:otp delegate:self];
    }];
}

- (void)replyToChallenge:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        NSDictionary *options = command.arguments[0];
        BOOL accept = [options[OGCDVPluginKeyAccept] boolValue];

        [self confirmationChallengeConfirmationBlock](accept);
    }];
}

- (void)finishRequest
{
    self.mobileAuthRequest = nil;
    self.confirmationChallengeConfirmationBlock = nil;
    self.callbackId = nil;
}

/* Delegate methods */

- (void)userClient:(ONGUserClient *)userClient didReceiveConfirmationChallenge:(void (^)(BOOL confirmRequest))confirmation forRequest:(ONGMobileAuthRequest *)request
{
    self.mobileAuthRequest = request;
    self.confirmationChallengeConfirmationBlock = confirmation;

    NSMutableDictionary *message = [[NSMutableDictionary alloc] init];
    message[OGCDVPluginKeyType] = request.type;
    message[OGCDVPluginKeyMessage] = request.message;
    message[OGCDVPluginKeyProfileId] = request.userProfile.profileId;
    message[OGCDVPluginKeyTransactionId] = request.transactionId;
    message[OGCDVPluginKeyEvent] = OGCDVPluginEventConfirmationRequest;

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:message];
    [pluginResult setKeepCallbackAsBool:YES];

    [[self commandDelegate] sendPluginResult:pluginResult callbackId:self.callbackId];
}

- (void)userClient:(ONGUserClient *)userClient didFailToHandleMobileAuthRequest:(ONGMobileAuthRequest *)request error:(NSError *)error
{
    [self sendErrorResultForCallbackId:self.callbackId withError:error];
    [self finishRequest];
}

- (void)userClient:(ONGUserClient *)userClient didHandleMobileAuthRequest:(ONGMobileAuthRequest *)request info:(ONGCustomInfo *_Nullable)customAuthenticatorInfo
{
    NSDictionary *result = @{
        OGCDVPluginKeyEvent: OGCDVPluginEventSuccess
    };
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
    [self finishRequest];
}

@end
