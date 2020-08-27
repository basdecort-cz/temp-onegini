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

#import "OGCDVDeviceAuthenticationClient.h"
#import "OGCDVConstants.h"

@implementation OGCDVDeviceAuthenticationClient {}

- (void)authenticate:(CDVInvokedUrlCommand*)command
{
  [self.commandDelegate runInBackground:^{
    NSArray<NSString*> *optionalScopes = nil;
    if (command.arguments.count > 0) {
      NSDictionary *options = command.arguments[0];
      optionalScopes = options[OGCDVPluginKeyScopes];
    }

    [[ONGDeviceClient sharedInstance] authenticateDevice:optionalScopes completion:^(BOOL success, NSError * _Nullable error) {
      if (error != nil || !success) {
        [self sendErrorResultForCallbackId:command.callbackId withError:error];
      } else {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
      }
    }];
  }];
}

@end
