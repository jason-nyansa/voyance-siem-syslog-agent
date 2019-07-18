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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class IoTGroupStats {
  private String model;
  private String deviceClass;
  private Boolean isCritical;
  private String aggWindow;
  private Date aggUpdated;
  private Integer numDevices;
  private Double totalBytes;
  private Double rxBytes;
  private Double txBytes;
  private Double avgBytesPerSec;
  private Double avgRxBytesPerSec;
  private Double avgTxBytesPerSec;
  private Double totalTimeSecs;
  private Integer numHosts;
  private Integer numInternalHosts;
  private Integer numExternalHosts;
  private Integer numInternalDevices;
  private Integer numExternalDevices;
  private List<String> essids;
  private List<String> vlans;
  private List<String> protocols;
  private List<String> locationNames;

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getDeviceClass() {
    return deviceClass;
  }

  public void setDeviceClass(String deviceClass) {
    this.deviceClass = deviceClass;
  }

  @JsonProperty(value = "isCritical")
  public Boolean isCritical() {
    return isCritical;
  }

  public void setCritical(Boolean critical) {
    isCritical = critical;
  }

  public String getAggWindow() {
    return aggWindow;
  }

  public void setAggWindow(String aggWindow) {
    this.aggWindow = aggWindow;
  }

  public Date getAggUpdated() {
    return aggUpdated;
  }

  public void setAggUpdated(Date aggUpdated) {
    this.aggUpdated = aggUpdated;
  }

  public Integer getNumDevices() {
    return numDevices;
  }

  public void setNumDevices(Integer numDevices) {
    this.numDevices = numDevices;
  }

  public Double getTotalBytes() {
    return totalBytes;
  }

  public void setTotalBytes(Double totalBytes) {
    this.totalBytes = totalBytes;
  }

  public Double getRxBytes() {
    return rxBytes;
  }

  public void setRxBytes(Double rxBytes) {
    this.rxBytes = rxBytes;
  }

  public Double getTxBytes() {
    return txBytes;
  }

  public void setTxBytes(Double txBytes) {
    this.txBytes = txBytes;
  }

  public Double getAvgBytesPerSec() {
    return avgBytesPerSec;
  }

  public void setAvgBytesPerSec(Double avgBytesPerSec) {
    this.avgBytesPerSec = avgBytesPerSec;
  }

  public Double getAvgRxBytesPerSec() {
    return avgRxBytesPerSec;
  }

  public void setAvgRxBytesPerSec(Double avgRxBytesPerSec) {
    this.avgRxBytesPerSec = avgRxBytesPerSec;
  }

  public Double getAvgTxBytesPerSec() {
    return avgTxBytesPerSec;
  }

  public void setAvgTxBytesPerSec(Double avgTxBytesPerSec) {
    this.avgTxBytesPerSec = avgTxBytesPerSec;
  }

  public Double getTotalTimeSecs() {
    return totalTimeSecs;
  }

  public void setTotalTimeSecs(Double totalTimeSecs) {
    this.totalTimeSecs = totalTimeSecs;
  }

  public Integer getNumHosts() {
    return numHosts;
  }

  public void setNumHosts(Integer numHosts) {
    this.numHosts = numHosts;
  }

  public Integer getNumInternalHosts() {
    return numInternalHosts;
  }

  public void setNumInternalHosts(Integer numInternalHosts) {
    this.numInternalHosts = numInternalHosts;
  }

  public Integer getNumExternalHosts() {
    return numExternalHosts;
  }

  public void setNumExternalHosts(Integer numExternalHosts) {
    this.numExternalHosts = numExternalHosts;
  }

  public Integer getNumInternalDevices() {
    return numInternalDevices;
  }

  public void setNumInternalDevices(Integer numInternalDevices) {
    this.numInternalDevices = numInternalDevices;
  }

  public Integer getNumExternalDevices() {
    return numExternalDevices;
  }

  public void setNumExternalDevices(Integer numExternalDevices) {
    this.numExternalDevices = numExternalDevices;
  }

  public List<String> getEssids() {
    return essids;
  }

  public void setEssids(List<String> essids) {
    this.essids = essids;
  }

  public List<String> getVlans() {
    return vlans;
  }

  public void setVlans(List<String> vlans) {
    this.vlans = vlans;
  }

  public List<String> getProtocols() {
    return protocols;
  }

  public void setProtocols(List<String> protocols) {
    this.protocols = protocols;
  }

  public List<String> getLocationNames() {
    return locationNames;
  }

  public void setLocationNames(List<String> locationNames) {
    this.locationNames = locationNames;
  }
}
