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

import com.nyansa.siem.api.models.PaginatedResults;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import static com.nyansa.siem.util.ConfigProperties.configProperties;
import static com.nyansa.siem.util.JsonUtil.jsonUtil;

public abstract class VcoApiPaginatedFetch<E, T extends PaginatedResults<E>> extends ApiPaginatedFetch<E, T> {
  private final Logger logger = LogManager.getLogger(this.getClass());

  public String queryFileName() {
    return fetchId() + ".txt";
  }

  @Override
  public String graphqlQueryFileName() {
    return queryFileName();
  }

  @Override
  T fetchPage(HttpClient httpClient, int pageNum, Timestamp fromTs, Timestamp toTs) {
    final String apiQueryJson = String.format(apiQuery(), fromTs.getTime() - 60000, toTs.getTime() - 60000);
    assert(apiQueryJson != null);
    final String apiUrl = configProperties().getApiUrl() + apiEndpoint();

    final HttpPost postReq = new HttpPost(apiUrl);
    postReq.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    postReq.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
    postReq.addHeader(HttpHeaders.ACCEPT_ENCODING, "application/json");
    postReq.addHeader("Authorization", configProperties().getApiToken());
    postReq.setEntity(new StringEntity(apiQueryJson, ContentType.APPLICATION_JSON));

    try {
      logger.debug("Fetching API data for {}, query {} ...", fetchId(), apiQueryJson);

      final HttpResponse resp = httpClient.execute(postReq);
      final int httpStatus = resp.getStatusLine().getStatusCode();
      final String httpReason = resp.getStatusLine().getReasonPhrase();

      logger.debug("HTTP status: {} {}, length {}", httpStatus, httpReason, resp.getEntity().getContentLength());

      if (httpStatus == 200) {
        T jsonNode = jsonUtil().parse(resp.getEntity().getContent(), getClazz());
        if (jsonNode != null) {
          return jsonNode;
        }
      }
      logger.warn("API fetch {} exception HTTP status: {} {}", fetchId(), httpStatus, httpReason);
      logger.warn("response: {}", EntityUtils.toString(resp.getEntity()));
      return null;
    } catch (IOException e) {
      logger.error("Caught HTTP exception: {}", ExceptionUtils.getStackTrace(e));
      return null;
    }
  }
}
