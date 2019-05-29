package com.nyansa.siem.api.models;

import java.util.List;

public class IoTOutlierList extends PaginatedResults<IoTOutlier> {
  private List<IoTOutlier> ioTOutliers;

  public List<IoTOutlier> getIoTOutliers() {
    return ioTOutliers;
  }

  public void setIoTOutliers(List<IoTOutlier> ioTOutliers) {
    this.ioTOutliers = ioTOutliers;
  }

  public List<IoTOutlier> getResults() {
    return getIoTOutliers();
  }
}
