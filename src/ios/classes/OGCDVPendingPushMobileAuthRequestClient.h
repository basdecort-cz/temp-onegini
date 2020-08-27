//  Copyright © 2017 Onegini. All rights reserved.

#import "CDVPlugin+OGCDV.h"
@import OneginiSDKiOS;

@interface OGCDVPendingPushMobileAuthRequestClient : CDVPlugin

- (void)fetch:(CDVInvokedUrlCommand *)command;
- (void)handle:(CDVInvokedUrlCommand *)command;

@end
