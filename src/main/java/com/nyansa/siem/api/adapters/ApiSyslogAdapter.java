package com.nyansa.siem.api.adapters;

import com.nyansa.siem.api.ApiPaginatedFetch;
import com.nyansa.siem.util.ConfigProperties;
import com.nyansa.siem.util.JsonUtil;
import com.nyansa.siem.util.SyslogLogger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

public class ApiSyslogAdapter extends ApiOutputAdapter {

  @Override
  public <E> boolean processOne(final ApiPaginatedFetch<E, ?> apiFetch, final E elem) {
    // convert API element base on configured format and output to syslog
    final Map<String, String> elemProps = JsonUtil.dumpAsProperties(elem);
    if (elemProps != null) {
      String logBody = new StringSubstitutor(elemProps).replace(getOutputFormat(apiFetch));

      final String cefHeaderFormat = ConfigProperties.getOutputCEFHeader();
      if (StringUtils.isNotBlank(cefHeaderFormat)) {
        // if a CEF header is configured, resolve variables and use original log body as CEF Extension
        final Map<String, String> cefProps = new HashMap<>();
        cefProps.put("cefSignatureId", apiFetch.getSignatureId(elem));
        cefProps.put("cefName", apiFetch.getCEFName(elem));
        cefProps.put("cefSeverity", apiFetch.getSeverity(elem));
        cefProps.put("cefExtension", logBody);
        logBody = new StringSubstitutor(cefProps).replace(cefHeaderFormat);
      }

      return SyslogLogger.send(logBody);
    }
    return false;
  }

  private String getOutputFormat(final ApiPaginatedFetch apiFetch) {
    String outputFormat = ConfigProperties.getOutputFormat(apiFetch.fetchId());
    if (StringUtils.isBlank(outputFormat)) {
      outputFormat = apiFetch.defaultOutputFormat();
    }
    return outputFormat;
  }
}
