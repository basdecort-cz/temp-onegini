/*
 * Copyright (c) 2017-2018 Onegini B.V.
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

package com.onegini.mobile.sdk.cordova.util;

import static com.onegini.mobile.sdk.cordova.customregistration.CustomIdentityProvider.ONE_STEP;
import static com.onegini.mobile.sdk.cordova.customregistration.CustomIdentityProvider.TWO_STEP;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;
import com.onegini.mobile.sdk.cordova.customregistration.CustomIdentityProvider;
import com.onegini.mobile.sdk.cordova.customregistration.CustomIdentityProvider.FlowType;

public class ApplicationConfigurationParser {

  private static final String TAG = ApplicationConfigurationParser.class.getSimpleName();

  private static final String CONFIG_FILE_NAME = "config";
  private static final String CONFIG_FILE_EXTENSION = "xml";

  private static final String CUSTOM_IDP = "custom-idp";
  private static final String CUSTOM_IDP_ID = "id";
  private static final String CUSTOM_IDP_FLOW_TYPE = "flowType";

  private final Context context;

  public ApplicationConfigurationParser(final Context context) {
    this.context = context;
  }

  public Set<CustomIdentityProvider> parserCustomIdentityProviders() {
    final Set<CustomIdentityProvider> identityProviders = new HashSet<CustomIdentityProvider>();

    final XmlResourceParser xmlResourceParser = getXmlParser();
    try {
      int eventType = xmlResourceParser.getEventType();
      while (eventType != XmlResourceParser.END_DOCUMENT) {
        if (XmlResourceParser.START_TAG == eventType) {
          if (CUSTOM_IDP.equals(xmlResourceParser.getName())) {
            final String id = xmlResourceParser.getAttributeValue(null, CUSTOM_IDP_ID);
            final @FlowType String flowType = xmlResourceParser.getAttributeValue(null, CUSTOM_IDP_FLOW_TYPE);
            if (isCustomIdentityProviderValid(id, flowType)) {
              identityProviders.add(new CustomIdentityProvider(id, flowType));
            }
          }
        }
        eventType = xmlResourceParser.next();
      }
    } catch (XmlPullParserException e) {
      Log.w(TAG, "Parsing the list of custom identity providers has failed: " + e.getMessage());
    } catch (IOException e) {
      Log.w(TAG, "Parsing the list of custom identity providers has failed: " + e.getMessage());
    } finally {
      xmlResourceParser.close();
    }
    return identityProviders;
  }

  private XmlResourceParser getXmlParser() {
    final int configResourceId = context.getResources().getIdentifier(CONFIG_FILE_NAME, CONFIG_FILE_EXTENSION, context.getPackageName());
    return context.getResources().getXml(configResourceId);
  }

  private boolean isCustomIdentityProviderValid(final String id, final String flowType) {
    return isIdValid(id) && isFlowTypeValid(flowType);
  }

  private boolean isIdValid(final String id) {
    return id != null && !id.equals("");
  }

  private boolean isFlowTypeValid(final String flowType) {
    return ONE_STEP.equalsIgnoreCase(flowType) || TWO_STEP.equalsIgnoreCase(flowType);
  }
}
