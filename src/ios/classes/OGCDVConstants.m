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

#import "OGCDVConstants.h"

NSString *const OGCDVPluginKeyMaxFailureCount = @"maxFailureCount";
NSString *const OGCDVPluginKeyPin = @"pin";
NSString *const OGCDVPluginKeyPinLength = @"pinLength";
NSString *const OGCDVPluginKeyProfileId = @"profileId";
NSString *const OGCDVPluginKeyTransactionId = @"transactionId";
NSString *const OGCDVPluginKeyRemainingFailureCount = @"remainingFailureCount";
NSString *const OGCDVPluginKeyScopes = @"scopes";
NSString *const OGCDVPluginKeyResourceBaseURL = @"resourceBaseURL";
NSString *const OGCDVPluginKeyAuthenticatorType = @"authenticatorType";
NSString *const OGCDVPluginKeyAuthenticatorId = @"authenticatorId";
NSString *const OGCDVPluginKeyAuthenticatorIsPreferred = @"isPreferred";
NSString *const OGCDVPluginKeyAuthenticatorIsRegistered = @"isRegistered";
NSString *const OGCDVPluginKeyAuthenticatorName = @"name";
NSString *const OGCDVPluginKeyMethod = @"method";
NSString *const OGCDVPluginKeyType = @"type";
NSString *const OGCDVPluginKeyMessage = @"message";
NSString *const OGCDVPluginKeyEvent = @"pluginEvent";
NSString *const OGCDVPluginKeyAccept = @"accept";
NSString *const OGCDVPluginKeyPrompt = @"iosPrompt";
NSString *const OGCDVPluginKeyOtp = @"otp";
NSString *const OGCDVPluginKeyErrorCode = @"code";
NSString *const OGCDVPluginKeyErrorDescription = @"description";
NSString *const OGCDVPluginKeyTimestamp = @"timestamp";
NSString *const OGCDVPluginKeyTTL = @"timeToLiveSeconds";
NSString *const OGCDVPluginKeyData = @"data";
NSString *const OGCDVPluginKeyCustomInfoData = @"customInfoData";
NSString *const OGCDVPluginKeyCustomInfoStatus = @"customInfoStatus";



NSString *const OGCDVPluginKeyIdentityProviderId = @"identityProviderId";
NSString *const OGCDVPluginKeyIdentityProviderName = @"identityProviderName";

NSString *const OGCDVPluginClassPushMobileAuthenticationClient = @"OneginiPushMobileAuthClient";
NSString *const OGCDVPluginClassPushMobileAuthRequestClient = @"OneginiPushMobileAuthRequestClient";

NSString *const OGCDVPluginAuthenticatorTypePin = @"PIN";
NSString *const OGCDVPluginAuthenticatorTypeTouchId = @"Fingerprint";

NSString *const OGCDVPluginEventSuccess = @"onSuccess";
NSString *const OGCDVPluginEventConfirmationRequest = @"onConfirmationRequest";
NSString *const OGCDVPluginEventPinRequest = @"onPinRequest";
NSString *const OGCDVPluginEventCreatePinRequest = @"onCreatePinRequest";
NSString *const OGCDVPluginEventFingerprintRequest = @"onFingerprintRequest";
NSString *const OGCDVPluginEventCustomRegistrationInitChallege = @"onCustomRegistrationInitRequest";
NSString *const OGCDVPluginEventCustomRegistrationFinishChallege = @"onCustomRegistrationCompleteRequest";

NSString *const OGCDVDidReceiveRegistrationCallbackURLNotification = @"OGCDVDidReceiveRegistrationCallbackURLNotification";

NSString *const OGCDVPluginErrDescriptionInternalError = @"Onegini: Internal plugin error";
NSString *const OGCDVPluginErrDescriptionProfileNotRegistered = @"Onegini: No registered user found.";
NSString *const OGCDVPluginErrDescriptionNoUserAuthenticated = @"Onegini: No user authenticated.";
NSString *const OGCDVPluginErrDescriptionNoSuchAuthenticator = @"Onegini: No such authenticator found";
NSString *const OGCDVPluginErrDescriptionCreatePinNoRegistrationInProgress = @"Onegini: createPin called, but no registration in progress.";
NSString *const OGCDVPluginErrDescriptionProvidePinNoAuthenticationInProgress = @"Onegini: providePin called, but no pin authentication in progress.";
NSString *const OGCDVPluginErrDescriptionFingerprintNoAuthenticationInProgress = @"Onegini: received reply for fingerprint authentication, but no fingerprint authentication in progress.";
NSString *const OGCDVPluginErrDescriptionInvalidMobileAuthenticationMethod = @"Onegini: Invalid mobile authentication method";
NSString *const OGCDVPluginErrDescriptionIncorrectPin = @"Onegini: Incorrect Pin. Check the maxFailureCount and remainingFailureCount properties for details.";
NSString *const OGCDVPluginErrDescriptionInvalidFetchAuthMethod = @"Onegini: invalid authentication method for resource.fetch";

int const OGCDVPluginErrCodePluginInternalError = 8000;
int const OGCDVPluginErrCodeIllegalArgument = 8002;

int const OGCDVPluginErrCodeProfileNotRegistered = 8003;
int const OGCDVPluginErrCodeNoUserAuthenticated = 8005;
int const OGCDVPluginErrCodeNoSuchAuthenticator = 8006;
int const OGCDVPluginErrCodeCreatePinNoRegistrationInProgress = 8007;
int const OGCDVPluginErrCodeProvidePinNoAuthenticationInProgress = 8008;
int const OGCDVPluginErrCodeFingerprintNoAuthenticationInProgress = 8009;
int const OGCDVPluginErrCodeInvalidMobileAuthenticationMethod = 8010;
int const OGCDVPluginErrCodeIoException = 8011;
int const OGCDVPluginErrCodeIncorrectPin = 8012;
