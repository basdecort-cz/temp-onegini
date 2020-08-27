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

@import OneginiSDKiOS;
#import "OGCDVPushMobileAuthRequestClient.h"

@interface OGCDVPushMobileAuthOperation : NSOperation<OGCDVPluginMobileAuthenticationRequestDelegate> {
}

@property (nonatomic, copy) void (^confirmationChallengeConfirmationBlock)(BOOL confirmRequest);
@property (atomic, retain) NSString *completeOperationCallbackId;
@property (atomic, retain) ONGMobileAuthRequest *mobileAuthenticationRequest;
@property (atomic, retain) NSString *mobileAuthenticationMethod;
@property (atomic, retain) ONGPinChallenge *pinChallenge;
@property (atomic, retain) ONGFingerprintChallenge *fingerprintChallenge;
@property (nonatomic, assign) BOOL _executing;
@property (nonatomic, assign) BOOL _finished;

- (id)initWithConfirmationChallenge:(void (^)(BOOL confirmRequest))confirmation
                         forRequest:(ONGMobileAuthRequest *)request
                          forMethod:(NSString *)method;
- (id)initWithPinChallenge:(ONGPinChallenge *)challenge
                forRequest:(ONGMobileAuthRequest *)request
                 forMethod:(NSString *)method;
- (id)initWithFingerprintChallenge:(ONGFingerprintChallenge *)challenge
                        forRequest:(ONGMobileAuthRequest *)request
                         forMethod:(NSString *)method;
- (void)sendChallenge:(NSString *)callbackId;

@end
