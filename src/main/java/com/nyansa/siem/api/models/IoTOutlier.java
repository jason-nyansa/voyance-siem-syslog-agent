package com.nyansa.siem.api.models;

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
