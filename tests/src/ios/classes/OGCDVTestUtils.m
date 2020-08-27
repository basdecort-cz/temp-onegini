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

#import "OGCDVTestUtils.h"
#import "OGCDVUserRegistrationClient.h"

NSString *const PARAM_USER_ID = @"userId";

@implementation OGCDVTestUtils

- (void)setUserId:(CDVInvokedUrlCommand *)command
{
    NSDictionary *options = command.arguments[0];
    NSString *userId = options[PARAM_USER_ID];
    [[OGCDVUserRegistrationClient sharedInstance] setUserId:userId];

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

- (void)setPreference:(CDVInvokedUrlCommand *)command
{
    NSString *key = [command.arguments[0] lowercaseString];
    NSString *value = command.arguments[1];

    assert(key != nil);
    assert(value != nil);

    [self.commandDelegate.settings setValue:value forKey:key];
}

- (void)getRedirectUrl:(CDVInvokedUrlCommand *)command
{
    NSDictionary *options = command.arguments[0];
    NSURL *url = [NSURL URLWithString:options[@"url"]];

    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    [request setHTTPMethod:@"GET"];

    NSURLSession *session = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]
                                                          delegate:self
                                                     delegateQueue:[NSOperationQueue mainQueue]];

    NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:request
                                                completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
                                                    if (error) {
                                                        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                                                                                 messageAsString:@"Could not get redirect URL"]
                                                                                    callbackId:command.callbackId];
                                                        return;
                                                    }

                                                    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
                                                    NSString *redirectUrl = [httpResponse allHeaderFields][@"Location"];
                                                    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                                                                             messageAsString:redirectUrl]
                                                                                callbackId:command.callbackId];
                                                }];
    [dataTask resume];
}

- (void)        URLSession:(NSURLSession *)session
                      task:(NSURLSessionTask *)task
willPerformHTTPRedirection:(NSHTTPURLResponse *)response
                newRequest:(NSURLRequest *)request
         completionHandler:(void (^)(NSURLRequest *_Nullable))completionHandler
{
    completionHandler(nil);
}

@end
