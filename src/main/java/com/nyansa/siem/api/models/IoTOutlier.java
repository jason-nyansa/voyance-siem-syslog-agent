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
import java.util.List;

public class IoTOutlier {
  private String uuid;
  private String model;
  private Date time;
  private String outlierType;
  private String outlierCategory;
  private String outlierReason;
  private String outlierValue;
  private List<String> locationNames;

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public String getOutlierType() {
    return outlierType;
  }

  public void setOutlierType(String outlierType) {
    this.outlierType = outlierType;
  }

  public String getOutlierCategory() {
    return outlierCategory;
  }

  public void setOutlierCategory(String outlierCategory) {
    this.outlierCategory = outlierCategory;
  }

  public String getOutlierReason() {
    return outlierReason;
  }

  public void setOutlierReason(String outlierReason) {
    this.outlierReason = outlierReason;
  }

  public String getOutlierValue() {
    return outlierValue;
  }

  public void setOutlierValue(String outlierValue) {
    this.outlierValue = outlierValue;
  }

  public List<String> getLocationNames() {
    return locationNames;
  }

  public void setLocationNames(List<String> locationNames) {
    this.locationNames = locationNames;
  }
}
