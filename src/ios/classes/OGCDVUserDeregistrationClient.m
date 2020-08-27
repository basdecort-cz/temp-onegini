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

#import "OGCDVUserDeregistrationClient.h"
#import "OGCDVUserClientHelper.h"
#import "OGCDVConstants.h"

@implementation OGCDVUserDeregistrationClient {
}

- (void)deregister:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        NSDictionary *options = command.arguments[0];
        NSString *profileId = options[OGCDVPluginKeyProfileId];
        ONGUserProfile *user = [OGCDVUserClientHelper getRegisteredUserProfile:profileId];

        if (user == nil) {
            [self sendErrorResultForCallbackId:command.callbackId
                                 withErrorCode:OGCDVPluginErrCodeProfileNotRegistered
                                    andMessage:OGCDVPluginErrDescriptionProfileNotRegistered];
            return;
        }

        [[ONGUserClient sharedInstance] deregisterUser:user completion:^(BOOL deregistered, NSError *_Nullable error) {
            if (error != nil || !deregistered) {
                [self sendErrorResultForCallbackId:command.callbackId withError:error];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            }
        }];
    }];
}

@end
