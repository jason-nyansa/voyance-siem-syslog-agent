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

import com.fasterxml.jackson.databind.JsonNode;
import com.nyansa.siem.api.models.IoTOutlier;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class JsonUtilTest {
  private final String uuid = "192.168.0.1";
  private final String model = "phone";
  private final long ts = System.currentTimeMillis();

  private static final String dateFormatStr = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  @Mock
  private ConfigProperties mockConfigProps;

  @InjectMocks
  private JsonUtil testJsonUtil;

  @BeforeEach
  void setup() {
    mockConfigProps = mock(ConfigProperties.class);
    when(mockConfigProps.getOutputDatetimeFormat()).thenReturn(dateFormatStr);
    testJsonUtil = new JsonUtil(mockConfigProps);
  }

  @Test
  void testParseFromString() {
    final String jsonStr = String.format("{ \"uuid\": \"%s\", \"model\": \"%s\", \"time\": %d }", uuid, model, ts);
    IoTOutlier obj = testJsonUtil.parse(jsonStr, IoTOutlier.class);
    assertNotNull(obj);
    assertEquals(uuid, obj.getUuid());
    assertEquals(model, obj.getModel());
    assertEquals(ts, obj.getTime().getTime());
    assertNull(obj.getOutlierValue());
    assertNull(obj.getLocationNames());

    final String jsonStrWithUnknownFields = String.format("{ \"uuid\": \"%s\", \"someUnknown\": %d }", uuid, 1234);
    obj = testJsonUtil.parse(jsonStrWithUnknownFields, IoTOutlier.class);
    assertNotNull(obj);
    assertEquals(uuid, obj.getUuid());

    final String invalidJsonStr = "abcd";
    obj = testJsonUtil.parse(invalidJsonStr, IoTOutlier.class);
    assertNull(obj);
  }

  @Test
  void testParseTree() {
    final String jsonStr = String.format("{ \"uuid\": \"%s\", \"model\": \"%s\", \"time\": %d }", uuid, model, ts);
    final JsonNode jsNode = testJsonUtil.parseTree(new ByteArrayInputStream(jsonStr.getBytes()));
    assertNotNull(jsNode);
    assertTrue(jsNode.isObject());
    assertTrue(jsNode.get("uuid").isTextual());
    assertEquals(uuid, jsNode.get("uuid").asText());
  }

  @Test
  void testDump() {
    final IoTOutlier obj = new IoTOutlier();
    obj.setUuid(uuid);
    obj.setModel(model);
    obj.setTime(new Date(ts));
    final String dumpedJsonStr = testJsonUtil.dump(obj);
    assertTrue(StringUtils.isNotBlank(dumpedJsonStr));

    // should be able to parse the dumped json string
    final IoTOutlier parsedObj = testJsonUtil.parse(dumpedJsonStr, IoTOutlier.class);
    assertNotNull(parsedObj);
    assertEquals(uuid, parsedObj.getUuid());
    assertEquals(model, parsedObj.getModel());
    assertEquals(ts, parsedObj.getTime().getTime());
  }

  @Test
  void testDumpAsProperties() {
    final IoTOutlier obj = new IoTOutlier();
    obj.setUuid(uuid);
    obj.setModel(model);
    obj.setTime(new Date(ts));
    obj.setLocationNames(Arrays.asList("a", "b", "c"));
    final Map<String, String> dumpedProps = testJsonUtil.dumpAsProperties(obj);
    assertNotNull(dumpedProps);
    assertFalse(dumpedProps.isEmpty());
    assertEquals("\"" + uuid + "\"", dumpedProps.get("uuid"));
    assertEquals("\"" + model + "\"", dumpedProps.get("model"));
    final SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    final String expectedDate = sdf.format(new Date(ts));
    assertEquals("\"" + expectedDate + "\"", dumpedProps.get("time"));
    assertEquals("[\"a\",\"b\",\"c\"]", dumpedProps.get("locationNames"));

    assertTrue(dumpedProps.containsKey("outlierType")); // assert nulls are getting dumped as well
    assertEquals("null", dumpedProps.get("outlierType"));
    assertTrue(dumpedProps.containsKey("outlierValue"));
    assertEquals("null", dumpedProps.get("outlierType"));
  }
}
