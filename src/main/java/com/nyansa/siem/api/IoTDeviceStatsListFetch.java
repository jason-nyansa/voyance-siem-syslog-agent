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

import com.nyansa.siem.api.models.AggregatedWindow;
import com.nyansa.siem.api.models.IoTDeviceStats;
import com.nyansa.siem.api.models.IoTDeviceStatsList;

import java.util.Date;
import java.util.Map;

public class IoTDeviceStatsListFetch extends ApiPaginatedFetch<IoTDeviceStats, IoTDeviceStatsList> {
  private AggregatedWindow aggWindow;

  public IoTDeviceStatsListFetch(AggregatedWindow aggWindow) {
    this.aggWindow = aggWindow;
  }

  @Override
  public String fetchId() {
    return "iotDeviceStatsList_" + aggWindow.getId();
  }

  @Override
  public String apiEndpoint() {
    return "iotDeviceStatsList";
  }

  @Override
  public Map<String, Object> apiVariables(final int page, final Date fromDate) {
    Map<String, Object> queryVars = super.apiVariables(page, fromDate);
    queryVars.put("aggWindow", aggWindow.getId());
    return queryVars;
  }

  @Override
  public String defaultLogOutputFormat() {
    return "uuid=${uuid} model=${model} deviceClass=${deviceClass} isCritical=${isCritical} aggWindow=${aggWindow} aggUpdated=${aggUpdated} totalBytes=${totalBytes} rxBytes=${rxBytes} txBytes=${txBytes} avgBytesPerSec=${avgBytesPerSec} avgRxBytesPerSec=${avgRxBytesPerSec} avgTxBytesPerSec=${avgTxBytesPerSec} totalTimeSecs=${totalTimeSecs} numHosts=${numHosts} numInternalHosts=${numInternalHosts} numExternalHosts=${numExternalHosts} essids=${essids} vlans=${vlans} suspiciousHosts=${suspiciousHosts} highRiskHosts=${highRiskHosts} hostsGeo=${hostsGeo}";
  }

  @Override
  protected Class<IoTDeviceStatsList> getClazz() {
    return IoTDeviceStatsList.class;
  }

  @Override
  public String getSignatureId(IoTDeviceStats elem) {
    return "device_iot_stats_" + elem.getAggWindow() ;
  }

  @Override
  public String getCEFName(IoTDeviceStats elem) {
    return apiEndpoint();
  }

  @Override
  public String getSeverity(IoTDeviceStats elem) {
    return "5"; // these stats are informational only
  }
}
