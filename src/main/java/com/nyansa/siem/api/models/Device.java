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

public class Device {
  private String uuid;
  private String macAddr;
  private String ipAddress;
  private String hostname;
  private String userName;
  private String description;
  private Boolean isActive;
  private Boolean isWireless;
  private String apMacAddr;
  private String apName;
  private String apGroup;
  private Integer rfBand;
  private Integer radioChannel;
  private String chWidth;
  private String protocol;
  private String essid;
  private String bssid;
  private String network;
  private Integer noiseOnAp;
  private Boolean isBandStrOnAp;
  private Boolean isDfsOnAp;
  private String apModel;
  private Integer snrDb;
  private String radioTechType;
  private Boolean is5ghzCapable;
  private Boolean isDfsCapable;
  private Boolean isOnDualBandAp;
  private Boolean isLbOnAp;
  private Long apDwellTimeMs;
  private String controllerIp;
  private DeviceTypeDetails deviceTypeDetails;
  private Boolean isIotDevice;
  private Boolean isCritical;
  private Date lastUpdated;
  private Date createdAt;
  private Date wannaCryLastTime;
  private String radioTechTypeDescription;
  private String radioTechTypeChannelWidth;
  private List<String> locationNames;
  private List<DeviceEvent> attributeChanges;

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

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty(value = "isActive")
  public Boolean getActive() {
    return isActive;
  }

  public void setActive(Boolean active) {
    isActive = active;
  }

  @JsonProperty(value = "isWireless")
  public Boolean getWireless() {
    return isWireless;
  }

  public void setWireless(Boolean wireless) {
    isWireless = wireless;
  }

  public String getApMacAddr() {
    return apMacAddr;
  }

  public void setApMacAddr(String apMacAddr) {
    this.apMacAddr = apMacAddr;
  }

  public String getApName() {
    return apName;
  }

  public void setApName(String apName) {
    this.apName = apName;
  }

  public String getApGroup() {
    return apGroup;
  }

  public void setApGroup(String apGroup) {
    this.apGroup = apGroup;
  }

  public Integer getRfBand() {
    return rfBand;
  }

  public void setRfBand(Integer rfBand) {
    this.rfBand = rfBand;
  }

  public Integer getRadioChannel() {
    return radioChannel;
  }

  public void setRadioChannel(Integer radioChannel) {
    this.radioChannel = radioChannel;
  }

  public String getChWidth() {
    return chWidth;
  }

  public void setChWidth(String chWidth) {
    this.chWidth = chWidth;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getEssid() {
    return essid;
  }

  public void setEssid(String essid) {
    this.essid = essid;
  }

  public String getBssid() {
    return bssid;
  }

  public void setBssid(String bssid) {
    this.bssid = bssid;
  }

  public String getNetwork() {
    return network;
  }

  public void setNetwork(String network) {
    this.network = network;
  }

  public Integer getNoiseOnAp() {
    return noiseOnAp;
  }

  public void setNoiseOnAp(Integer noiseOnAp) {
    this.noiseOnAp = noiseOnAp;
  }

  @JsonProperty(value = "isBandStrOnAp")
  public Boolean getBandStrOnAp() {
    return isBandStrOnAp;
  }

  public void setBandStrOnAp(Boolean bandStrOnAp) {
    isBandStrOnAp = bandStrOnAp;
  }

  @JsonProperty(value = "isDfsOnAp")
  public Boolean getDfsOnAp() {
    return isDfsOnAp;
  }

  public void setDfsOnAp(Boolean dfsOnAp) {
    isDfsOnAp = dfsOnAp;
  }

  public String getApModel() {
    return apModel;
  }

  public void setApModel(String apModel) {
    this.apModel = apModel;
  }

  public Integer getSnrDb() {
    return snrDb;
  }

  public void setSnrDb(Integer snrDb) {
    this.snrDb = snrDb;
  }

  public String getRadioTechType() {
    return radioTechType;
  }

  public void setRadioTechType(String radioTechType) {
    this.radioTechType = radioTechType;
  }

  @JsonProperty(value = "is5ghzCapable")
  public Boolean getIs5ghzCapable() {
    return is5ghzCapable;
  }

  public void setIs5ghzCapable(Boolean is5ghzCapable) {
    this.is5ghzCapable = is5ghzCapable;
  }

  @JsonProperty(value = "isDfsCapable")
  public Boolean getDfsCapable() {
    return isDfsCapable;
  }

  public void setDfsCapable(Boolean dfsCapable) {
    isDfsCapable = dfsCapable;
  }

  @JsonProperty(value = "isOnDualBandAp")
  public Boolean getOnDualBandAp() {
    return isOnDualBandAp;
  }

  public void setOnDualBandAp(Boolean onDualBandAp) {
    isOnDualBandAp = onDualBandAp;
  }

  @JsonProperty(value = "isLbOnAp")
  public Boolean getLbOnAp() {
    return isLbOnAp;
  }

  public void setLbOnAp(Boolean lbOnAp) {
    isLbOnAp = lbOnAp;
  }

  public Long getApDwellTimeMs() {
    return apDwellTimeMs;
  }

  public void setApDwellTimeMs(Long apDwellTimeMs) {
    this.apDwellTimeMs = apDwellTimeMs;
  }

  public String getControllerIp() {
    return controllerIp;
  }

  public void setControllerIp(String controllerIp) {
    this.controllerIp = controllerIp;
  }

  public DeviceTypeDetails getDeviceTypeDetails() {
    return deviceTypeDetails;
  }

  public void setDeviceTypeDetails(DeviceTypeDetails deviceTypeDetails) {
    this.deviceTypeDetails = deviceTypeDetails;
  }

  @JsonProperty(value = "isIotDevice")
  public Boolean getIotDevice() {
    return isIotDevice;
  }

  public void setIotDevice(Boolean iotDevice) {
    isIotDevice = iotDevice;
  }

  @JsonProperty(value = "isCritical")
  public Boolean getCritical() {
    return isCritical;
  }

  public void setCritical(Boolean critical) {
    isCritical = critical;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getWannaCryLastTime() {
    return wannaCryLastTime;
  }

  public void setWannaCryLastTime(Date wannaCryLastTime) {
    this.wannaCryLastTime = wannaCryLastTime;
  }

  public String getRadioTechTypeDescription() {
    return radioTechTypeDescription;
  }

  public void setRadioTechTypeDescription(String radioTechTypeDescription) {
    this.radioTechTypeDescription = radioTechTypeDescription;
  }

  public String getRadioTechTypeChannelWidth() {
    return radioTechTypeChannelWidth;
  }

  public void setRadioTechTypeChannelWidth(String radioTechTypeChannelWidth) {
    this.radioTechTypeChannelWidth = radioTechTypeChannelWidth;
  }

  public List<String> getLocationNames() {
    return locationNames;
  }

  public void setLocationNames(List<String> locationNames) {
    this.locationNames = locationNames;
  }

  public List<DeviceEvent> getAttributeChanges() {
    return attributeChanges;
  }

  public void setAttributeChanges(List<DeviceEvent> attributeChanges) {
    this.attributeChanges = attributeChanges;
  }
}

class DeviceTypeDetails {
  private String osAndVersion;
  private String deviceClass;
  private String model;
  private String browser;
  private String userAgent;

  public String getOsAndVersion() {
    return osAndVersion;
  }

  public void setOsAndVersion(String osAndVersion) {
    this.osAndVersion = osAndVersion;
  }

  public String getDeviceClass() {
    return deviceClass;
  }

  public void setDeviceClass(String deviceClass) {
    this.deviceClass = deviceClass;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getBrowser() {
    return browser;
  }

  public void setBrowser(String browser) {
    this.browser = browser;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }
}
