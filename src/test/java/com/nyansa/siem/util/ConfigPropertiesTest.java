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

import org.apache.http.HttpHost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Properties;

import static com.nyansa.siem.util.ConfigProperties.PropertyNames.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ConfigPropertiesTest {
  @Mock
  private Properties mockProps;

  @InjectMocks
  private ConfigProperties testCp;

  @BeforeEach
  void setup() {
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
  void testHttpProxy() {
    when(mockProps.getProperty(HTTP_PROXY)).thenReturn("");
    assertNull(testCp.getHttpProxy());

    when(mockProps.getProperty(HTTP_PROXY)).thenReturn("10.80.1.1:3128");
    HttpHost httpProxyHost = testCp.getHttpProxy();
    assertNotNull(httpProxyHost);
    assertEquals("10.80.1.1", httpProxyHost.getHostName());
    assertEquals(3128, httpProxyHost.getPort());

    when(mockProps.getProperty(HTTP_PROXY)).thenReturn("10.80.1.1");
    httpProxyHost = testCp.getHttpProxy();
    assertNotNull(httpProxyHost);
    assertEquals("10.80.1.1", httpProxyHost.getHostName());
    assertEquals(-1, httpProxyHost.getPort());

    when(mockProps.getProperty(HTTP_PROXY)).thenReturn("10.80.1.1:3128:80");
    Throwable thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.getHttpProxy());
    assertEquals(HTTP_PROXY + " must be in format hostname:port", thrownEx.getMessage());

    when(mockProps.getProperty(HTTP_PROXY)).thenReturn("10.80.1.1:foobar");
    thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.getHttpProxy());
    assertTrue(thrownEx.getMessage().endsWith("port is not a valid number"));
  }

  @Test
  void testApiFetchesEnabled() {
    when(mockProps.getProperty(API_FETCHES_ENABLED)).thenReturn("");
    Throwable thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.uncached().getApiFetchesEnabled());
    assertTrue(thrownEx.getMessage().startsWith(API_FETCHES_ENABLED + " must present"));

    String apisStr = "iotOutlierList_all,iotDeviceStatsList_last24h";
    when(mockProps.getProperty(API_FETCHES_ENABLED)).thenReturn(apisStr);
    assertEquals(2, testCp.uncached().getApiFetchesEnabled().size());

    apisStr = "iotOutlierList_all";
    when(mockProps.getProperty(API_FETCHES_ENABLED)).thenReturn(apisStr);
    assertEquals(1, testCp.uncached().getApiFetchesEnabled().size());

    apisStr = "iotOutlierList_all,invalid";
    when(mockProps.getProperty(API_FETCHES_ENABLED)).thenReturn(apisStr);
    thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.uncached().getApiFetchesEnabled());
    assertTrue(thrownEx.getMessage().startsWith(API_FETCHES_ENABLED + " contains invalid API fetch ID"));

    apisStr = "invalid";
    when(mockProps.getProperty(API_FETCHES_ENABLED)).thenReturn(apisStr);
    thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.uncached().getApiFetchesEnabled());
    assertTrue(thrownEx.getMessage().startsWith(API_FETCHES_ENABLED + " contains invalid API fetch ID"));
  }

  @Test
  void testApiPullFreqSecs() {
    when(mockProps.getProperty(eq(API_PULL_FREQ), any(String.class))).thenCallRealMethod(); // default
    assertEquals(60L, testCp.getApiPullFreqSecs());
    {
      // test per API fetch overrides
      final String fetchId = "fetchId";
      assertEquals(60L, testCp.getApiPullFreqSecs(fetchId));

      when(mockProps.getProperty(API_PULL_FREQ + "." + fetchId)).thenReturn("30");
      Throwable thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.getApiPullFreqSecs(fetchId));
      assertTrue(thrownEx.getMessage().startsWith(API_PULL_FREQ + "." + fetchId + " must be >="));

      when(mockProps.getProperty(API_PULL_FREQ + "." + fetchId)).thenReturn("120");
      assertEquals(120L, testCp.getApiPullFreqSecs(fetchId));
    }

    when(mockProps.getProperty(eq(API_PULL_FREQ), any(String.class))).thenReturn("30");
    Throwable thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.getApiPullFreqSecs());
    assertTrue(thrownEx.getMessage().startsWith(API_PULL_FREQ + " must be >="));

    when (mockProps.getProperty(eq(API_PULL_FREQ), any(String.class))).thenReturn("120");
    assertEquals(120L, testCp.getApiPullFreqSecs());

    when(mockProps.getProperty(eq(API_PULL_FREQ), any(String.class))).thenReturn("invalid");
    assertThrows(NumberFormatException.class, () -> testCp.getApiPullFreqSecs());
  }

  @Test
  void testApiPullThreads() {
    when(mockProps.getProperty(eq(API_PULL_THREADS), any(String.class))).thenCallRealMethod(); // default
    assertTrue(testCp.getApiPullThreads() >= 1);

    when(mockProps.getProperty(eq(API_PULL_THREADS), any(String.class))).thenReturn("invalid");
    assertThrows(NumberFormatException.class, () -> testCp.getApiPullThreads());
  }

  @Test
  void testDefaultLookbackSecs() {
    when(mockProps.getProperty(eq(DEFAULT_LOOKBACK), any(String.class))).thenCallRealMethod(); // default
    assertEquals(86400L, testCp.getDefaultLookbackSecs());

    when(mockProps.getProperty(eq(DEFAULT_LOOKBACK), any(String.class))).thenReturn("invalid");
    assertThrows(NumberFormatException.class, () -> testCp.getDefaultLookbackSecs());
  }

  @Test
  void testOutputCEFHeader() {
    assertNull(testCp.uncached().getOutputCEFHeader()); // ok to be null

    String invalidHeader = "CEF:0|Nyansa|voyance-siem-syslog-agent|1.0|${cefSignatureId}|${cefName}|${cefSeverity}";
    when(mockProps.getProperty(OUTPUT_CEF_HEADER)).thenReturn(invalidHeader);
    Throwable thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.uncached().getOutputCEFHeader());
    assertTrue(thrownEx.getMessage().startsWith(OUTPUT_CEF_HEADER + " must conform to CEF format with 8 sections"));

    invalidHeader = "CEF:0|Nyansa|voyance-siem-syslog-agent|1.0|${cefSignatureId}|${cefName}|${cefSeverity}|${invalidVar}";
    when(mockProps.getProperty(OUTPUT_CEF_HEADER)).thenReturn(invalidHeader);
    thrownEx = assertThrows(IllegalArgumentException.class, () -> testCp.uncached().getOutputCEFHeader());
    assertTrue(thrownEx.getMessage().startsWith(OUTPUT_CEF_HEADER + " can only contain variables"));

    final String validHeader = "output.cef.header=CEF:0|Nyansa|voyance-siem-syslog-agent|1.0|${cefSignatureId}|${cefName}|${cefSeverity}|${cefExtension}";
    when(mockProps.getProperty(OUTPUT_CEF_HEADER)).thenReturn(validHeader);
    assertEquals(validHeader, testCp.uncached().getOutputCEFHeader());
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
