package com.nyansa.siem.api;

/*-
 * #%L
 * VoyanceSiemSyslogAgent
 * %%
 * Copyright (C) 2019 - 2021 Nyansa, Inc.
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

import com.nyansa.siem.api.models.VcoEnterpriseEvent;
import com.nyansa.siem.api.models.VcoEnterpriseEventList;

public class VcoEnterpriseEventFetch extends VcoApiPaginatedFetch<VcoEnterpriseEvent, VcoEnterpriseEventList> {
  @Override
  public String fetchId() {
    return "vcoEnterpriseEvents";
  }

  @Override
  public String apiEndpoint() {
    return "/event/getEnterpriseEvents";
  }

  @Override
  public String defaultLogOutputFormat() {
    return "id=${id} eventTime=${eventTime} event=${event} category=${category} severity=${severity} message=${message} enterpriseUsername=${enterpriseUsername} edgeName=${edgeName} segmentName=${segmentName} detail=${detail}";
  }

  @Override
  protected Class<VcoEnterpriseEventList> getClazz() {
    return VcoEnterpriseEventList.class;
  }

  @Override
  public String getSignatureId(VcoEnterpriseEvent elem) {
    return elem.getCategory();
  }

  @Override
  public String getCEFName(VcoEnterpriseEvent elem) {
    return elem.getEvent();
  }

  @Override
  public String getSeverity(VcoEnterpriseEvent elem) {
    return elem.getSeverity();
  }
}
