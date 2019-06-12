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
  protected String apiQuery(int pageNum, long fromTimestamp) {
    return String.format("{"
        + "  iotDeviceStatsList("
        + "    page: %d"
        + "    pageSize: 250"
        + "    aggWindow: %s"
        + "    sortBy: \"uuid\""
        + "    sortOrder: ASC"
        + "  ) {"
        + "    iotDeviceStats {"
        + "      uuid"
        + "      model"
        + "      deviceClass"
        + "      isCritical"
        + "      aggWindow"
        + "      aggUpdated"
        + "      totalBytes"
        + "      rxBytes"
        + "      txBytes"
        + "      avgBytesPerSec"
        + "      avgRxBytesPerSec"
        + "      avgTxBytesPerSec"
        + "      totalTimeSecs"
        + "      numHosts"
        + "      numInternalHosts"
        + "      numExternalHosts"
        + "      essids"
        + "      vlans"
        + "      suspiciousHosts"
        + "      highRiskHosts"
        + "      hostsGeo"
        + "      locationNames"
        + "    }"
        + "    page"
        + "    pageSize"
        + "    pageCount"
        + "    totalCount"
        + "  }"
        + "}", pageNum, aggWindow.getId());
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
