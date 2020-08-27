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

#import "OGCDVPendingPushMobileAuthRequestClient.h"
#import "OGCDVConstants.h"
#import "OGCDVPendingPushMobileAuthRequestClientHelper.h"
#import "OGCDVPushMobileAuthRequestClient.h"

@implementation OGCDVPendingPushMobileAuthRequestClient {
}

- (void)fetch:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        [[ONGUserClient sharedInstance] pendingPushMobileAuthRequests:^(NSArray<ONGPendingMobileAuthRequest *> * _Nullable pendingTransactions, NSError * _Nullable error) {
            if (error) {
                [self sendErrorResultForCallbackId:command.callbackId withError:error];
            } else {
                NSMutableArray *result = [[NSMutableArray alloc] initWithCapacity:pendingTransactions.count];
                for (ONGPendingMobileAuthRequest *pendingMobileAuthRequest in pendingTransactions) {
                    [result addObject:[OGCDVPendingPushMobileAuthRequestClientHelper dictionaryFromPendingMobileAuthRequest:pendingMobileAuthRequest]];
                }
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:result] callbackId:command.callbackId];
            }
        }];
    }];
}

- (void)handle:(CDVInvokedUrlCommand *)command
{
    NSDictionary *options = command.arguments[0];
    ONGPendingMobileAuthRequest *pendingMobileAuthRequest = [OGCDVPendingPushMobileAuthRequestClientHelper pendingMobileAuthRequestFromDictionary:options];
    [[ONGUserClient sharedInstance] handlePendingPushMobileAuthRequest:pendingMobileAuthRequest delegate:[OGCDVPushMobileAuthRequestClient sharedInstance]];
}

@end
