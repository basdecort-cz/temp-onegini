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

#import "OGCDVPendingPushMobileAuthRequestClientHelper.h"
#import "OGCDVConstants.h"
#import "ONGPendingMobileAuthRequest+IntenalSetters.h"

@implementation OGCDVPendingPushMobileAuthRequestClientHelper

+ (NSDictionary *)dictionaryFromPendingMobileAuthRequest:(ONGPendingMobileAuthRequest *)pendingMobileAuthRequest
{
    NSDictionary *dictionary = @{
                                 OGCDVPluginKeyProfileId: pendingMobileAuthRequest.userProfile.profileId,
                                 OGCDVPluginKeyTransactionId: pendingMobileAuthRequest.transactionId,
                                 OGCDVPluginKeyMessage: pendingMobileAuthRequest.message,
                                 OGCDVPluginKeyTimestamp: [NSNumber numberWithInteger:(NSInteger)[pendingMobileAuthRequest.date timeIntervalSince1970]],
                                 OGCDVPluginKeyTTL: pendingMobileAuthRequest.timeToLive
                                 };
    return dictionary;
}

+ (ONGPendingMobileAuthRequest *)pendingMobileAuthRequestFromDictionary:(NSDictionary *)dictionary
{
    ONGPendingMobileAuthRequest *pendingMobileAuthRequest = [[ONGPendingMobileAuthRequest alloc] init];
    pendingMobileAuthRequest.message = dictionary[OGCDVPluginKeyMessage];
    pendingMobileAuthRequest.transactionId = dictionary[OGCDVPluginKeyTransactionId];
    pendingMobileAuthRequest.userProfile = [ONGUserProfile profileWithId: dictionary[OGCDVPluginKeyProfileId]];
    pendingMobileAuthRequest.timeToLive = dictionary[OGCDVPluginKeyTTL];
    pendingMobileAuthRequest.date = [NSDate dateWithTimeIntervalSince1970:[dictionary[OGCDVPluginKeyTimestamp] doubleValue]];
    return pendingMobileAuthRequest;
}

@end
