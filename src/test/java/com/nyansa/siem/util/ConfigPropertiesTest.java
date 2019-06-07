package com.nyansa.siem.util;

/*-
 * #%L
 * VoyanceSiemSyslogAgent
 * %%
 * Copyright (C) 2019 Nyansa, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Properties;

import static com.nyansa.siem.util.ConfigProperties.PropertyNames.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConfigPropertiesTest {
  @Mock
  private Properties mockProps;

  @InjectMocks
  private ConfigProperties testCp;

  @BeforeEach
  void setup() {
    mockProps = mock(Properties.class);
    testCp = new ConfigProperties(mockProps);
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void testApiUrl() {
    when(mockProps.getProperty(API_URL)).thenReturn("");
    Throwable thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.getApiUrl());
    assertTrue(thrownEx.getMessage().startsWith(API_URL + " must present"));

    final String url = "https://foobar.nyansa.com/api/v2/graphql";
    when(mockProps.getProperty(API_URL)).thenReturn(url);
    assertEquals(url, testCp.getApiUrl());
  }

  @Test
  void testApiToken() {
    when(mockProps.getProperty(API_TOKEN)).thenReturn("");
    Throwable thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.getApiToken());
    assertTrue(thrownEx.getMessage().startsWith(API_TOKEN + " must present"));

    final String token = "Foo_baR";
    when(mockProps.getProperty(API_TOKEN)).thenReturn(token);
    assertEquals(token, testCp.getApiToken());
  }

  @Test
  void testApiPullFreqSecs() {
    when(mockProps.getProperty(API_PULL_FREQ, "60")).thenReturn("60"); // default
    assertEquals(60L, testCp.getApiPullFreqSecs());

    when(mockProps.getProperty(API_PULL_FREQ, "60")).thenReturn("30");
    Throwable thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.getApiPullFreqSecs());
    assertTrue(thrownEx.getMessage().startsWith(API_PULL_FREQ + " must be >="));

    when (mockProps.getProperty(API_PULL_FREQ, "60")).thenReturn("120");
    assertEquals(120L, testCp.getApiPullFreqSecs());

    when(mockProps.getProperty(API_PULL_FREQ, "60")).thenReturn("invalid");
    assertThrows(NumberFormatException.class, () -> testCp.getApiPullFreqSecs());
  }

  @Test
  void testDefaultLookbackSecs() {
    when(mockProps.getProperty(DEFAULT_LOOKBACK, "86400")).thenReturn("86400"); // default
    assertEquals(86400L, testCp.getDefaultLookbackSecs());

    when(mockProps.getProperty(DEFAULT_LOOKBACK, "86400")).thenReturn("invalid");
    assertThrows(NumberFormatException.class, () -> testCp.getDefaultLookbackSecs());
  }

  @Test
  void testOutputCEFHeader() {
    assertNull(testCp.getOutputCEFHeader()); // ok to be null

    String invalidHeader = "CEF:0|Nyansa|voyance-siem-syslog-agent|1.0|${cefSignatureId}|${cefName}|${cefSeverity}";
    when(mockProps.getProperty(OUTPUT_CEF_HEADER)).thenReturn(invalidHeader);
    Throwable thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.getOutputCEFHeader());
    assertTrue(thrownEx.getMessage().startsWith(OUTPUT_CEF_HEADER + " must conform to CEF format with 8 sections"));

    invalidHeader = "CEF:0|Nyansa|voyance-siem-syslog-agent|1.0|${cefSignatureId}|${cefName}|${cefSeverity}|${invalidVar}";
    when(mockProps.getProperty(OUTPUT_CEF_HEADER)).thenReturn(invalidHeader);
    thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.getOutputCEFHeader());
    assertTrue(thrownEx.getMessage().startsWith(OUTPUT_CEF_HEADER + " can only contain variables"));

    final String validHeader = "output.cef.header=CEF:0|Nyansa|voyance-siem-syslog-agent|1.0|${cefSignatureId}|${cefName}|${cefSeverity}|${cefExtension}";
    when(mockProps.getProperty(OUTPUT_CEF_HEADER)).thenReturn(validHeader);
    assertEquals(validHeader, testCp.getOutputCEFHeader());
  }

  @Test
  void testOutputDatetimeFormat() {
    assertNull(testCp.getOutputDatetimeFormat()); // ok to be null
  }

  @Test
  void testOutputFormat() {
    assertNull(testCp.getOutputFormat("fetchId")); // ok to be null
  }
}
