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

import com.nyansa.siem.api.ApiPaginatedFetch;
import com.nyansa.siem.util.ConfigProperties;
import com.nyansa.siem.util.SyslogLogger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

import static com.nyansa.siem.util.ConfigProperties.configProperties;
import static com.nyansa.siem.util.JsonUtil.jsonUtil;

public class ApiSyslogAdapter extends ApiOutputAdapter {

  private SyslogLogger syslogLogger;
  private ConfigProperties configProps;

  public ApiSyslogAdapter() {
    this(null, null);
  }

  public ApiSyslogAdapter(SyslogLogger inSyslogLogger, ConfigProperties inConfigProps) {
    if (inSyslogLogger == null) {
      inSyslogLogger = new SyslogLogger();
    }
    if (inConfigProps == null) {
      inConfigProps = configProperties();
    }
    syslogLogger = inSyslogLogger;
    configProps = inConfigProps;
  }

  @Override
  public <E> boolean processOne(final ApiPaginatedFetch<E, ?> apiFetch, final E elem) {
    // convert API element base on configured format and output to syslog
    final Map<String, String> elemProps = jsonUtil().dumpAsProperties(elem);
    if (elemProps != null) {
      String logBody = new StringSubstitutor(elemProps).replace(getOutputFormat(apiFetch));

      final String cefHeaderFormat = configProps.getOutputCEFHeader();
      if (StringUtils.isNotBlank(cefHeaderFormat)) {
        // if a CEF header is configured, resolve variables and use original log body as CEF Extension
        final Map<String, String> cefProps = new HashMap<>();
        cefProps.put("cefSignatureId", apiFetch.getSignatureId(elem));
        cefProps.put("cefName", apiFetch.getCEFName(elem));
        cefProps.put("cefSeverity", apiFetch.getSeverity(elem));
        cefProps.put("cefExtension", logBody);
        logBody = new StringSubstitutor(cefProps).replace(cefHeaderFormat);
      }

      return syslogLogger.send(logBody);
    }
    return false;
  }

  private String getOutputFormat(final ApiPaginatedFetch apiFetch) {
    String outputFormat = configProps.getOutputFormat(apiFetch.fetchId());
    if (StringUtils.isBlank(outputFormat)) {
      outputFormat = apiFetch.defaultLogOutputFormat();
    }
    return outputFormat;
  }
}
