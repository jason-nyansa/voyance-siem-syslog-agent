package com.nyansa.siem.api;

import com.nyansa.siem.api.models.IoTOutlier;
import com.nyansa.siem.api.models.IoTOutlierList;

public class IoTOutlierListFetch extends ApiPaginatedFetch<IoTOutlier, IoTOutlierList> {
  @Override
  public String fetchId() {
    return "iotOutlierList_all";
  }

  @Override
  public String apiEndpoint() {
    return "iotOutlierList";
  }

  @Override
  protected String apiQuery(int pageNum, long fromTimestamp) {
    return String.format("{"
        + "  iotOutlierList(page: %d, pageSize: 500, fromDate: %d, sortBy: \"time\", sortOrder: ASC) {"
        + "    ioTOutliers {"
        + "      uuid"
        + "      model"
        + "      time"
        + "      outlierType"
        + "      outlierCategory"
        + "      outlierReason"
        + "      outlierValue"
        + "      locationNames"
        + "      bcScore"
        + "    }"
        + "    page"
        + "    pageSize"
        + "    pageCount"
        + "    totalCount"
        + "  }"
        + "}", pageNum, fromTimestamp);
  }

  @Override
  public String defaultLogOutputFormat() {
    return "uuid=${uuid} model=${model} time=${time} outlierType=${outlierType} outlierCategory=${outlierCategory} outlierReason=${outlierReason} outlierValue=${outlierValue}";
  }

  @Override
  protected Class<IoTOutlierList> getClazz() {
    return IoTOutlierList.class;
  }

  @Override
  public String getSignatureId(IoTOutlier elem) {
    return elem.getOutlierReason();
  }

  @Override
  public String getCEFName(IoTOutlier elem) {
    return apiEndpoint();
  }

  @Override
  public String getSeverity(IoTOutlier elem) {
    return "5"; // lack of a better way to determine severity
  }
}
