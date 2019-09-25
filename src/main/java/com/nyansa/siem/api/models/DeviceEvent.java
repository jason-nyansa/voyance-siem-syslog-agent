package com.nyansa.siem.api.models;

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

import java.util.Date;

public class DeviceEvent {
  private String uuid;
  private String macAddr;
  private String ipAddress;
  private String description;

  private Date sampleTime;
  private String attribute;
  private String oldValue;
  private String newValue;

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getMacAddr() {
    return macAddr;
  }

  public void setMacAddr(String macAddr) {
    this.macAddr = macAddr;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getSampleTime() {
    return sampleTime;
  }

  public void setSampleTime(Date sampleTime) {
    this.sampleTime = sampleTime;
  }

  public String getAttribute() {
    return attribute;
  }

  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }

  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }
}
