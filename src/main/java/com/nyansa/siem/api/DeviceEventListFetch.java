package com.nyansa.siem.api;

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

import com.nyansa.siem.api.models.DeviceEvent;
import com.nyansa.siem.api.models.DeviceEventList;

import java.util.Date;
import java.util.Map;

public class DeviceEventListFetch extends ApiPaginatedFetch<DeviceEvent, DeviceEventList> {
  @Override
  public String fetchId() {
    return "deviceEventList_all";
  }

  @Override
  public String apiEndpoint() {
    return "deviceList";
  }

  @Override
  public String graphqlQueryFileName() {
    return "deviceEventList.graphql";
  }

  @Override
  public Map<String, Object> apiVariables(final int page, final Date fromDate) {
    Map<String, Object> queryVars = super.apiVariables(page, fromDate);
    final Date eventFromDate = new Date(System.currentTimeMillis() - 600000); // last 10 minutes
    queryVars.put("eventFromDate", eventFromDate);
    return queryVars;
  }

  @Override
  public String defaultLogOutputFormat() {
    return "uuid=${uuid} macAddr=${macAddr} ipAddress=${ipAddress} description=${description} sampleTime=${sampleTime} attribute=${attribute} oldValue=${oldValue} newValue=${newValue}";
  }

  @Override
  protected Class<DeviceEventList> getClazz() {
    return DeviceEventList.class;
  }

  @Override
  public String getSignatureId(DeviceEvent elem) {
    return elem.getAttribute();
  }

  @Override
  public String getCEFName(DeviceEvent elem) {
    return "deviceEventList";
  }

  @Override
  public String getSeverity(DeviceEvent elem) {
    return "5"; // lack of a better way to determine severity
  }
}
