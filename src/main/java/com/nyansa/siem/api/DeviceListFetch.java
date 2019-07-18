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

import com.nyansa.siem.api.models.Device;
import com.nyansa.siem.api.models.DeviceList;

public class DeviceListFetch extends ApiPaginatedFetch<Device, DeviceList> {
  @Override public String fetchId() {
    return "deviceList_updated";
  }

  @Override public String apiEndpoint() {
    return "deviceList";
  }

  @Override public String defaultLogOutputFormat() {
    return "uuid=${uuid} macAddr=${macAddr} ipAddress=${ipAddress} hostname=${hostname} userName=${userName} description=${description} isActive=${isActive} isWireless=${isWireless} apMacAddr=${apMacAddr} apName=${apName} apGroup=${apGroup} rfBand=${rfBand} radioChannel=${radioChannel} chWidth=${chWidth} protocol=${protocol} essid=${essid} bssid=${bssid} network=${network} noiseOnAp=${noiseOnAp} isBandStrOnAp=${isBandStrOnAp} isDfsOnAp=${isDfsOnAp} apModel=${apModel} snrDb=${snrDb} radioTechType=${radioTechType} is5ghzCapable=${is5ghzCapable} isDfsCapable=${isDfsCapable} isOnDualBandAp=${isOnDualBandAp} isLbOnAp=${isLbOnAp} apDwellTimeMs=${apDwellTimeMs} controllerIp=${controllerIp} deviceTypeDetails=${deviceTypeDetails} isIotDevice=${isIotDevice} isCritical=${isCritical} lastUpdated=${lastUpdated} createdAt=${createdAt} wannaCryLastTime=${wannaCryLastTime} radioTechTypeDescription=${radioTechTypeDescription} radioTechTypeChannelWidth=${radioTechTypeChannelWidth} locationNames=${locationNames}";
  }

  @Override protected Class<DeviceList> getClazz() {
    return DeviceList.class;
  }

  @Override public String getSignatureId(Device elem) {
    return "devices";
  }

  @Override public String getCEFName(Device elem) {
    return apiEndpoint();
  }

  @Override public String getSeverity(Device elem) {
    return "5"; // these stats are informational only
  }
}
