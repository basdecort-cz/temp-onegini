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

#import "CDVPlugin+OGCDV.h"
#import "OGCDVConstants.h"

@implementation CDVPlugin (OGCDV)

- (void)sendErrorResultForCallbackId:(NSString *)callbackId withMessage:(NSString *)errorMessage
{
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:@{OGCDVPluginKeyErrorDescription: errorMessage}]
                                callbackId:callbackId];
}

- (void)sendErrorResultForCallbackId:(NSString *)callbackId withErrorCode:(int)code andMessage:(NSString *)errorMessage
{
    NSDictionary *result = @{
        OGCDVPluginKeyErrorCode: @(code),
        OGCDVPluginKeyErrorDescription: errorMessage
    };
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:result] callbackId:callbackId];
}

- (void)sendErrorResultForCallbackId:(NSString *)callbackId withError:(NSError *)error
{
    NSString *errorMessage;
    int errorCode;
    if (error == nil) {
        errorMessage = OGCDVPluginErrDescriptionInternalError;
        errorCode = OGCDVPluginErrCodePluginInternalError;
    } else {
        errorMessage = [NSString stringWithFormat:@"%@\n%@", error.localizedDescription, error.localizedRecoverySuggestion];
        errorCode = (int) error.code;
    }
    [self sendErrorResultForCallbackId:callbackId withErrorCode:errorCode andMessage:errorMessage];
}

@end
