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

import com.nyansa.siem.api.models.AggregatedWindow;
import com.nyansa.siem.api.models.Application;
import com.nyansa.siem.api.models.ApplicationList;

import java.util.Date;
import java.util.Map;

public class ApplicationListFetch extends ApiPaginatedFetch<Application, ApplicationList> {
  private AggregatedWindow aggWindow;

  public ApplicationListFetch(AggregatedWindow aggWindow) {
    this.aggWindow = aggWindow;
  }

  @Override
  public String fetchId() {
    return "applicationList_" + aggWindow.getId();
  }

  @Override
  public String apiEndpoint() {
    return "applicationList";
  }

  @Override
  public Map<String, Object> apiVariables(final int page, final Date fromDate) {
    Map<String, Object> queryVars = super.apiVariables(page, fromDate);
    queryVars.put("aggWindow", aggWindow.getId());
    return queryVars;
  }

  @Override
  public String defaultLogOutputFormat() {
    return "appName=${appName} userCount=${userCount} totalBytes=${totalBytes} rxBytes=${rxBytes} txBytes=${txBytes} aggWindow=${aggWindow} aggUpdated=${aggUpdated}";
  }

  @Override
  protected Class<ApplicationList> getClazz() {
    return ApplicationList.class;
  }

  @Override
  public String getSignatureId(Application elem) {
    return "applications_" + elem.getAggWindow();
  }

  @Override
  public String getCEFName(Application elem) {
    return apiEndpoint();
  }

  @Override
  public String getSeverity(Application elem) {
    return "5"; // these stats are informational only
  }
}
