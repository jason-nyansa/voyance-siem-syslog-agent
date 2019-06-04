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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

public class JsonUtil {
  private static final Logger logger = LogManager.getLogger(JsonUtil.class);

  private static ObjectMapper mapper;
  private static ObjectMapper outMapper;
  static {
    mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    outMapper = mapper.copy();
    final String dfs = ConfigProperties.getOutputDatetimeFormat();
    if (dfs != null) {
      outMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      final DateFormat df = new SimpleDateFormat(dfs);
      final TimeZone utc = TimeZone.getTimeZone("UTC");
      df.setTimeZone(utc);
      outMapper.setDateFormat(df);
      outMapper.setTimeZone(utc);
    }
  }

  public static <T> T parse(final String str, final Class<T> clazz) {
    try {
      return mapper.readValue(str, clazz);
    } catch (IOException e) {
      logger.error("Caught JSON exception: {}", ExceptionUtils.getStackTrace(e));
      return null;
    }
  }

  public static <T> T parse(final InputStream is, final Class<T> clazz) {
    try {
      return mapper.readValue(is, clazz);
    } catch (IOException e) {
      logger.error("Caught JSON exception: {}", ExceptionUtils.getStackTrace(e));
      return null;
    }
  }

  public static <T> T parse(final JsonNode jsonNode, final Class<T> clazz) {
    try {
      return mapper.treeToValue(jsonNode, clazz);
    } catch (IOException e) {
      logger.error("Caught JSON exception: {}", ExceptionUtils.getStackTrace(e));
      return null;
    }
  }

  public static JsonNode parseTree(final InputStream is) {
    try {
      return mapper.readTree(is);
    } catch (IOException e) {
      logger.error("Caught JSON exception: {}", ExceptionUtils.getStackTrace(e));
      return null;
    }
  }

  public static String dump(final Object obj) {
    try {
      return outMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      logger.error("Caught JSON exception: {}", ExceptionUtils.getStackTrace(e));
      return null;
    }
  }

  public static Map<String, String> dumpAsProperties(final Object obj) {
    try {
      final JsonNode jsonNode = outMapper.valueToTree(obj);
      if (jsonNode.isObject()) {
        Map<String, String> props = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields();
        while (it.hasNext()) {
          Map.Entry<String, JsonNode> field = it.next();
          props.put(field.getKey(), outMapper.writeValueAsString(field.getValue()));
        }
        return props;
      }
      return null;
    } catch (JsonProcessingException e) {
      logger.error("Caught JSON exception: {}", ExceptionUtils.getStackTrace(e));
      return null;
    }
  }
}
