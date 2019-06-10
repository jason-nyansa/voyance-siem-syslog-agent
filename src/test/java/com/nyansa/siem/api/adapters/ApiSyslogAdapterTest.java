package com.nyansa.siem.api.adapters;

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

import com.nyansa.siem.api.IoTOutlierListFetch;
import com.nyansa.siem.api.models.IoTOutlier;
import com.nyansa.siem.util.ConfigProperties;
import com.nyansa.siem.util.SyslogLogger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ApiSyslogAdapterTest {
  private final String uuid = "192.168.0.1";
  private final String model = "phone";

  @Mock
  private SyslogLogger mockSyslogLogger;

  @Mock
  private ConfigProperties mockConfigProps;

  @Mock
  private IoTOutlierListFetch mockApiFetch;

  @InjectMocks
  private ApiSyslogAdapter testAdapter;

  private IoTOutlier elem;
  private List<String> logBuffer;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this);

    elem = new IoTOutlier();
    elem.setUuid(uuid);
    elem.setModel(model);

    logBuffer = new ArrayList<>();
    ArgumentCaptor<String> logArg = ArgumentCaptor.forClass(String.class);
    when(mockSyslogLogger.send(logArg.capture())).thenAnswer((Answer) invocation -> {
      logBuffer.add(logArg.getValue());
      return true;
    });

    when(mockApiFetch.fetchId()).thenCallRealMethod();
  }

  @Test
  void testProcessOne_noOverrides() {
    when(mockConfigProps.getOutputCEFHeader()).thenReturn(null);
    when(mockConfigProps.getOutputFormat(mockApiFetch.fetchId())).thenReturn(null);
    when(mockApiFetch.defaultLogOutputFormat()).thenReturn("uuid=${uuid} model=${model} time=${time}");

    testAdapter.processOne(mockApiFetch, elem);
    assertEquals(1, logBuffer.size());
    final String expected = String.format("uuid=\"%s\" model=\"%s\" time=null", uuid, model);
    assertEquals(expected, logBuffer.get(0));
  }

  @Test
  void testProcessOne_formatOverride() {
    when(mockConfigProps.getOutputCEFHeader()).thenReturn(null);
    when(mockConfigProps.getOutputFormat(mockApiFetch.fetchId())).thenReturn("model=${model} uuid=${uuid}");
    when(mockApiFetch.defaultLogOutputFormat()).thenReturn("uuid=${uuid} model=${model} time=${time}");

    testAdapter.processOne(mockApiFetch, elem);
    assertEquals(1, logBuffer.size());
    final String expected = String.format("model=\"%s\" uuid=\"%s\"", model, uuid);
    assertEquals(expected, logBuffer.get(0));
  }

  @Test
  void testProcessOne_cefOverride() {
    when(mockConfigProps.getOutputCEFHeader()).thenReturn("CEF:0|Nyansa|voyance-siem-syslog-agent|1.0|${cefSignatureId}|${cefName}|${cefSeverity}|${cefExtension}");
    when(mockConfigProps.getOutputFormat(mockApiFetch.fetchId())).thenReturn(null);
    when(mockApiFetch.defaultLogOutputFormat()).thenReturn("uuid=${uuid} model=${model} time=${time}");
    when(mockApiFetch.getSignatureId(any(IoTOutlier.class))).thenReturn("SIG_ID");
    when(mockApiFetch.getCEFName(any(IoTOutlier.class))).thenReturn("CEF_NAME");
    when(mockApiFetch.getSeverity(any(IoTOutlier.class))).thenReturn("10");

    testAdapter.processOne(mockApiFetch, elem);
    assertEquals(1, logBuffer.size());
    final String extension = String.format("uuid=\"%s\" model=\"%s\" time=null", uuid, model);
    final String expected = "CEF:0|Nyansa|voyance-siem-syslog-agent|1.0|SIG_ID|CEF_NAME|10|" + extension;
    assertEquals(expected, logBuffer.get(0));
  }
}
