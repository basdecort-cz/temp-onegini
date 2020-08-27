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

#import "OGCDVUserRegistrationClient.h"
#import "OGCDVUserClientHelper.h"
#import "OGCDVConstants.h"
#import "OGCDVIdentityProvidersClientHelper.h"
#import "OneginiConfigModel.h"

static OGCDVUserRegistrationClient *sharedInstance;
NSString *const preferenceSFSafariViewController = @"SFSafariViewController";
NSString *const preferenceDisabled = @"disabled";
NSString *const eventRegistrationRequest = @"onRegistrationRequest";
NSString *const keyPreferenceWebView = @"oneginiwebview";
NSString *const keyURL = @"url";

@interface OGCDVUserRegistrationClient ()<SFSafariViewControllerDelegate, WKNavigationDelegate>

@property (nonatomic, copy) NSString *callbackId;
@property (nonatomic, copy) NSString *userId;
@property (nonatomic) ONGCreatePinChallenge *createPinChallenge;
@property (nonatomic) ONGBrowserRegistrationChallenge *browserRegistrationChallenge;
@property (nonatomic) ONGCustomRegistrationChallenge *customRegistrationChallenge;

@end

@implementation OGCDVUserRegistrationClient {
    WKWebView *registrationWebView;
}

+ (id)sharedInstance
{
    return sharedInstance;
}

- (void)pluginInitialize
{
    sharedInstance = self;
}

- (void)start:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        self.callbackId = command.callbackId;
        NSArray *optionalScopes;
        ONGIdentityProvider *identityProvider = nil;
        if (command.arguments.count > 0) {
            NSDictionary *options = command.arguments[0];
            if (options[OGCDVPluginKeyIdentityProviderId] != nil && ![options[OGCDVPluginKeyIdentityProviderId] isKindOfClass:[NSNull class]]) {
                NSPredicate *predicate = [NSPredicate predicateWithFormat:@"identifier == %@", command.arguments[0][@"identityProviderId"]];
                NSSet *filteredSet = [[[ONGUserClient sharedInstance] identityProviders] filteredSetUsingPredicate:predicate];
                identityProvider = filteredSet.anyObject;
            }
            optionalScopes = options[OGCDVPluginKeyScopes];
        }
        
        [[ONGUserClient sharedInstance] registerUserWithIdentityProvider:identityProvider scopes:optionalScopes delegate:self];
    }];
}

- (void)createPin:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        self.callbackId = command.callbackId;
        NSDictionary *options = command.arguments[0];
        NSString *pin = options[OGCDVPluginKeyPin];

        if (self.createPinChallenge) {
            [self.createPinChallenge.sender respondWithCreatedPin:pin challenge:self.createPinChallenge];
        } else {
            [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeCreatePinNoRegistrationInProgress
                                    andMessage:OGCDVPluginErrDescriptionCreatePinNoRegistrationInProgress];
        }
    }];
}

- (void)getUserProfiles:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        NSArray<ONGUserProfile *> *profiles = [[ONGUserClient sharedInstance] userProfiles].allObjects;

        NSMutableArray *result = [[NSMutableArray alloc] initWithCapacity:profiles.count];
        for (ONGUserProfile *profile in profiles) {
            [result addObject:@{OGCDVPluginKeyProfileId: profile.profileId}];
        }

        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:result] callbackId:command.callbackId];
    }];
}

- (void)isUserRegistered:(CDVInvokedUrlCommand *)command
{
    NSDictionary *options = command.arguments[0];
    NSString *profileId = options[OGCDVPluginKeyProfileId];

    BOOL isRegistered = [OGCDVUserClientHelper getRegisteredUserProfile:profileId] != nil;

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:isRegistered] callbackId:command.callbackId];
}

- (void)cancelFlow:(CDVInvokedUrlCommand *)command
{
    if (self.browserRegistrationChallenge) {
        [self.browserRegistrationChallenge.sender cancelChallenge:self.browserRegistrationChallenge];
    }

    if (self.createPinChallenge) {
        [self.createPinChallenge.sender cancelChallenge:self.createPinChallenge];
    }
}

- (void)respondToRegistrationRequest:(CDVInvokedUrlCommand *)command
{
    NSDictionary *options = command.arguments[0];
    NSString *urlString = options[@"url"];
    NSURL *url = [NSURL URLWithString:urlString];

    if (!self.browserRegistrationChallenge) {
#ifdef DEBUG
        NSLog(@"OneginiPlugin - WARNING: tried to reply to registration challenge, but no registration challenge is active");
#endif
        return;
    }

    [self.commandDelegate runInBackground:^{
        [self handleRegistrationCallbackURL:url];
    }];
}

- (void)sendRegistrationRequestEvent:(NSURL *)url
{
    NSDictionary *message = @{
        OGCDVPluginKeyEvent: eventRegistrationRequest,
        keyURL: url.absoluteString
    };
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:message];
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
}

- (void)handleRegistrationCallbackNotification:(NSNotification *)notification
{
    NSURL *url = (NSURL *)notification.object;
    if ([url.absoluteString hasPrefix:[OneginiConfigModel configuration][@"ONGRedirectURL"]]) {
        [self handleRegistrationCallbackURL:url];
    }
}

- (void)handleRegistrationCallbackURL:(NSURL *)url
{
    if (self.browserRegistrationChallenge) {
        [self.browserRegistrationChallenge.sender respondWithURL:url challenge:self.browserRegistrationChallenge];
    }
}

- (void)openURLWithWKWebView:(NSURL *)url
{
    WKWebViewConfiguration *wkWebViewConfiguration = [[WKWebViewConfiguration alloc] init];
    NSURLRequest *urlRequest = [NSURLRequest requestWithURL:url];

    dispatch_async(dispatch_get_main_queue(), ^{
        registrationWebView = [[WKWebView alloc] initWithFrame:self.viewController.view.frame configuration:wkWebViewConfiguration];
        registrationWebView.navigationDelegate = self;
        [registrationWebView loadRequest:urlRequest];

        CATransition *transition = [CATransition new];
        transition.type = kCATransitionMoveIn;
        transition.subtype = kCATransitionFromRight;
        [self.viewController.view addSubview:registrationWebView];
        [self.viewController.view.layer addAnimation:transition forKey:@"transition"];
    });
}

- (void)openURLWithSafariViewController:(NSURL *)url
{
    SFSafariViewController *safariViewController = [[SFSafariViewController alloc] initWithURL:url];
    safariViewController.delegate = self;

    dispatch_async(dispatch_get_main_queue(), ^{
        [self.viewController presentViewController:safariViewController animated:true completion:nil];
    });
}

#pragma mark - WKNavigationDelegate

- (void)                webView:(WKWebView *)webView
decidePolicyForNavigationAction:(WKNavigationAction *)navigationAction
                decisionHandler:(void (^)(WKNavigationActionPolicy))decisionHandler
{
    NSString *redirectURL = [[[ONGClient sharedInstance] configModel] redirectURL];

    if ([navigationAction.request.URL.absoluteString hasPrefix:redirectURL]) {
        decisionHandler(WKNavigationActionPolicyCancel);
        [self handleRegistrationCallbackURL:navigationAction.request.URL];

        CATransition *transition = [CATransition new];
        transition.type = kCATransitionMoveIn;
        transition.subtype = kCATransitionFromLeft;
        [registrationWebView removeFromSuperview];
        [self.viewController.view.layer addAnimation:transition forKey:@"transition"];
        registrationWebView = nil;
    } else {
        decisionHandler(WKNavigationActionPolicyAllow);
    }
}

#pragma mark - SFSafariViewControllerDelegate

- (void)safariViewControllerDidFinish:(SFSafariViewController *)controller
{
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    if (self.browserRegistrationChallenge) {
        [self.browserRegistrationChallenge.sender cancelChallenge:self.browserRegistrationChallenge];
    }
}

#pragma mark - ONGRegistrationDelegate

- (void)userClient:(ONGUserClient *)userClient didReceivePinRegistrationChallenge:(ONGCreatePinChallenge *)challenge
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.viewController dismissViewControllerAnimated:YES completion:nil];
    });

    self.createPinChallenge = challenge;

    NSMutableDictionary *result = [[NSMutableDictionary alloc] init];
    result[OGCDVPluginKeyEvent] = OGCDVPluginEventCreatePinRequest;
    result[OGCDVPluginKeyProfileId] = challenge.userProfile.profileId;
    result[OGCDVPluginKeyPinLength] = @(challenge.pinLength);

    if (challenge.error != nil) {
        result[OGCDVPluginKeyErrorCode] = @(challenge.error.code);
        result[OGCDVPluginKeyErrorDescription] = challenge.error.localizedDescription;
    }

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}

- (void)userClient:(ONGUserClient *)userClient didReceiveBrowserRegistrationChallenge:(nonnull ONGBrowserRegistrationChallenge *)challenge
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleRegistrationCallbackNotification:)
                                                 name:CDVPluginHandleOpenURLNotification
                                               object:nil];
    NSURL *url;
    self.browserRegistrationChallenge = challenge;
    BOOL webViewDisabled = [preferenceDisabled isEqualToString:self.commandDelegate.settings[keyPreferenceWebView]];
    BOOL hasSFSafariViewController = SFSafariViewController.class != nil;
    BOOL preferSFSafariViewController = [preferenceSFSafariViewController isEqualToString:self.commandDelegate.settings[keyPreferenceWebView]];

    if (self.userId == nil) {
        url = challenge.url;
    } else {
        url = [self addQueryParameterToUrl:challenge.url withQueryName:@"user_id" withQueryValue:self.userId];
    }

    [self sendRegistrationRequestEvent:url];

    if (webViewDisabled) {
        return;
    }

    if (hasSFSafariViewController && preferSFSafariViewController) {
        [self openURLWithSafariViewController:url];
    } else {
        [self openURLWithWKWebView:url];
    }
}

- (void)userClient:(ONGUserClient *)userClient didReceiveCustomRegistrationInitChallenge:(ONGCustomRegistrationChallenge *)challenge
{
    self.customRegistrationChallenge = challenge;
    
    NSMutableDictionary *result = [[NSMutableDictionary alloc] init];
    result[OGCDVPluginKeyEvent] = OGCDVPluginEventCustomRegistrationInitChallege;
    result[OGCDVPluginKeyCustomInfoData] = challenge.info.data;
    result[OGCDVPluginKeyCustomInfoStatus] = @(challenge.info.status);
    result[OGCDVPluginKeyIdentityProviderId] = challenge.identityProvider.identifier;

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}

- (void)respondToCustomRegistrationInitRequest:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        NSDictionary *options = command.arguments[0];
        if ([options[OGCDVPluginKeyAccept] boolValue]) {
            NSString *data = options[OGCDVPluginKeyData];
            if ([data isKindOfClass:[NSNull class]]) {
                data = nil;
            }
            [self.customRegistrationChallenge.sender respondWithData:data challenge:self.customRegistrationChallenge];
        } else {
            [self.customRegistrationChallenge.sender cancelChallenge:self.customRegistrationChallenge];
        }
    }];
}

- (void)userClient:(ONGUserClient *)userClient didReceiveCustomRegistrationFinishChallenge:(ONGCustomRegistrationChallenge *)challenge
{
    self.customRegistrationChallenge = challenge;
    
    NSMutableDictionary *result = [[NSMutableDictionary alloc] init];
    result[OGCDVPluginKeyEvent] = OGCDVPluginEventCustomRegistrationFinishChallege;
    result[OGCDVPluginKeyCustomInfoData] = challenge.info.data;
    result[OGCDVPluginKeyCustomInfoStatus] = @(challenge.info.status);
    result[OGCDVPluginKeyIdentityProviderId] = challenge.identityProvider.identifier;
    result[OGCDVPluginKeyProfileId] = challenge.userProfile.profileId;
    
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}

- (void)respondToCustomRegistrationCompleteRequest:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        NSDictionary *options = command.arguments[0];
        if ([options[OGCDVPluginKeyAccept] boolValue]) {
            NSString *data = options[OGCDVPluginKeyData];
            if ([data isKindOfClass:[NSNull class]]) {
                data = nil;
            }
            [self.customRegistrationChallenge.sender respondWithData:data challenge:self.customRegistrationChallenge];
        } else {
            [self.customRegistrationChallenge.sender cancelChallenge:self.customRegistrationChallenge];
        }
    }];
}

- (void)userClient:(ONGUserClient *)userClient didRegisterUser:(ONGUserProfile *)userProfile info:(ONGCustomInfo * _Nullable)info
{
    self.createPinChallenge = nil;
    self.browserRegistrationChallenge = nil;
    self.customRegistrationChallenge = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self name:OGCDVDidReceiveRegistrationCallbackURLNotification object:nil];

    NSDictionary *result = @{
        OGCDVPluginKeyEvent: OGCDVPluginEventSuccess,
        OGCDVPluginKeyProfileId: userProfile.profileId
    };
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result] callbackId:self.callbackId];
}

- (void)userClient:(ONGUserClient *)userClient didFailToRegisterWithError:(NSError *)error
{
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    [self sendErrorResultForCallbackId:self.callbackId withError:error];
}

- (NSURL *)addQueryParameterToUrl:(NSURL *)url withQueryName:(NSString *)name withQueryValue:(NSString *)value
{
    NSURLComponents *components = [[NSURLComponents alloc] initWithURL:url resolvingAgainstBaseURL:false];
    NSURLQueryItem *newQueryItem = [[NSURLQueryItem alloc] initWithName:name value:value];
    NSMutableArray *queryItems = [NSMutableArray arrayWithCapacity:[components.queryItems count] + 1];
    for (NSURLQueryItem *qi in components.queryItems) {
        if (![qi.name isEqual:newQueryItem.name]) {
            [queryItems addObject:qi];
        }
    }
    [queryItems addObject:newQueryItem];
    [components setQueryItems:queryItems];
    return [components URL];
}

@end
