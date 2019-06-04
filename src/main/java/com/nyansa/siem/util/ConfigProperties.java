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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigProperties {
  private static final Logger logger = LogManager.getLogger(ConfigProperties.class);

  private static Properties props;
  static {
    props = new Properties();
    try {
      props.load(ConfigProperties.class.getClassLoader().getResourceAsStream("config.properties"));
    } catch (IOException e) {
      logger.error("Caught exception: {}", ExceptionUtils.getStackTrace(e));
    }
  }

  static final class PropertyNames {
    static final String API_URL = "voyance.dev.api.url";
    static final String API_TOKEN = "voyance.dev.api.token";
    static final String API_PULL_FREQ = "api.pull.frequency.secs";
    static final String DEFAULT_LOOKBACK = "data.default.lookback.secs";
    static final String OUTPUT_CEF_HEADER = "output.cef.header";
    static final String OUTPUT_DATETIME_FORMAT = "output.datetime.format";
    static final String OUTPUT_FORMAT_PREIFX = "output.format.";
  }

  public static void validateAll() {
    getApiUrl();
    getApiToken();
    getApiPullFreqSecs();
    getDefaultLookbackSecs();
    getOutputCEFHeader();
    getOutputDatetimeFormat();
  }

  public static String getApiUrl() {
    return requiredProperty(PropertyNames.API_URL);
  }

  public static String getApiToken() {
    return requiredProperty(PropertyNames.API_TOKEN);
  }

  public static long getApiPullFreqSecs() {
    long freqSecs = Long.parseLong(props.getProperty(PropertyNames.API_PULL_FREQ, "60"));
    if (freqSecs < 60) {
      throw new IllegalArgumentException(PropertyNames.API_PULL_FREQ + " must be >= 60");
    }
    return freqSecs;
  }

  public static long getDefaultLookbackSecs() {
    return Long.parseLong(props.getProperty(PropertyNames.DEFAULT_LOOKBACK, "86400"));
  }

  private static String validatedCefHeader = null;
  public static String getOutputCEFHeader() {
    if (validatedCefHeader != null) {
      return validatedCefHeader;
    }
    final String cefHeader = props.getProperty(PropertyNames.OUTPUT_CEF_HEADER);
    if (cefHeader == null) { // null CEF header is allowed
      return null;
    }
    // validate CEF header
    if (cefHeader.split("\\|").length != 8) {
      throw new IllegalArgumentException(PropertyNames.OUTPUT_CEF_HEADER + " must conform to CEF format with 8 sections separated by \"|\"");
    }
    final Pattern varPattern = Pattern.compile("\\$\\{[^}]+}");
    final Matcher varMatcher = varPattern.matcher(cefHeader);
    final List<String> allowedVars = Arrays.asList("${cefSignatureId}", "${cefName}", "${cefSeverity}", "${cefExtension}");
    while (varMatcher.find()) {
      if (!allowedVars.contains(varMatcher.group())) {
        throw new IllegalArgumentException(PropertyNames.OUTPUT_CEF_HEADER + " can only contain variables " + String.join(", ", allowedVars));
      }
    }
    validatedCefHeader = cefHeader;
    return validatedCefHeader;
  }

  public static String getOutputDatetimeFormat() {
    return props.getProperty(PropertyNames.OUTPUT_DATETIME_FORMAT);
  }

  public static String getOutputFormat(final String fetchId) {
    return props.getProperty(PropertyNames.OUTPUT_FORMAT_PREIFX + fetchId);
  }


  private static String requiredProperty(String propName) {
    final String propValue = props.getProperty(propName);
    if (StringUtils.isBlank(propValue)) {
      throw new IllegalArgumentException(propName + " must present in config.properties and not be blank");
    }
    return propValue;
  }
}
