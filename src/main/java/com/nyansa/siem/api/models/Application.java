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

public class Application {
  private String appName;
  private String aggWindow;
  private Date aggUpdated;
  private Double userCount;
  private Double totalBytes;
  private Double rxBytes;
  private Double txBytes;

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
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

  public Double getUserCount() {
    return userCount;
  }

  public void setUserCount(Double userCount) {
    this.userCount = userCount;
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
}

