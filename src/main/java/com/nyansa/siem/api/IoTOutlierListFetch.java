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
