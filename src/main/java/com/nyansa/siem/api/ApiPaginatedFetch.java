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

import com.fasterxml.jackson.databind.JsonNode;
import com.nyansa.siem.api.adapters.ApiOutputAdapter;
import com.nyansa.siem.api.models.PaginatedResults;
import com.nyansa.siem.util.AgentDB;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.nyansa.siem.util.ConfigProperties.configProperties;
import static com.nyansa.siem.util.JsonUtil.jsonUtil;

/**
 * Abstract class to represent a GraphQL API fetch against a query of a paginated data type.
 * Defining common logic for performing paginated data fetches, data bindings, and output.
 *
 * @param <E> The individual entity type of the corresponding API data type
 * @param <T> The wrapping paginated data type of the corresponding API data type
 */
public abstract class ApiPaginatedFetch<E, T extends PaginatedResults<E>> {
  private final Logger logger = LogManager.getLogger(this.getClass());

  /**
   * Override in subclass to define an unique identifier for this API fetch, for the purpose of
   * progress tracking, logging etc.
   * E.g. "iotOutlierList_all"
   *
   * @return the unique fetch ID
   */
  public abstract String fetchId();

  /**
   * Override in subclass to specify the GraphQL API endpoint.
   * E.g. "iotOutlierList"
   *
   * @return the API endpoint
   */
  public abstract String apiEndpoint();

  /**
   * Override in subclass to provide the GraphQL API query string.
   * By default the query string is read from files under /queries/${apiEndpoint}.graphql .
   *
   * @return the API query string
   */
  public String apiQuery() {
    final String queryFileName = apiEndpoint() + ".graphql";
    final StringBuilder queryStr = new StringBuilder();

    try {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(
          ApiPaginatedFetch.class.getClassLoader().getResourceAsStream(queryFileName)));

      String line;
      while ((line = reader.readLine()) != null) {
        queryStr.append(line);
      }
      return queryStr.toString();
    } catch (Exception e) {
      logger.error("Caught exception: {}", ExceptionUtils.getStackTrace(e));
      throw new IllegalArgumentException("Error reading API query string under /queries/" + queryFileName);
    }
  }

  /**
   * Override in subclass to provide additional GraphQL API query variables.
   * By default $page: Int, $fromDate: Date are supplied.
   *
   * @return the API query variables as key value pairs
   */
  public Map<String, Object> apiVariables(final int page, final Date fromDate) {
    Map<String, Object> queryVars = new HashMap<>();
    queryVars.put("page", page);
    queryVars.put("fromDate", toDateTimeString(fromDate));
    return queryVars;
  }

  /**
   * Override in subclass to provide the default log output format string of this API fetch.
   * E.g. "uuid=${uuid} model=${model} time=${time}"
   *
   * @return the log format string
   */
  public abstract String defaultLogOutputFormat();

  /**
   * Override in subclass to provide the Class object of the paginated type T.
   * E.g. IoTOutlierList.class
   *
   * @return the Class of type T
   */
  protected abstract Class<T> getClazz();

  /**
   * Specify logic in subclass to return a Syslog CEF signature ID based on the API element.
   * E.g. elem.getOutlierReason()
   *
   * @param elem  the API element
   * @return the signature ID of the element
   */
  public abstract String getSignatureId(E elem);

  /**
   * Specify logic in subclass to return a Syslog CEF name based on the API element.
   * E.g. apiEndpoint()
   *
   * @param elem  the API element
   * @return the CEF name of the element
   */
  public abstract String getCEFName(E elem);

  /**
   * Specify logic in subclass to return a severity level based on the API element. The level should
   * be between 1 (least severe) to 10 (most severe).
   * E.g. "5"
   *
   * @param elem  the API element
   * @return the severity level of the element as a string
   */
  public abstract String getSeverity(E elem);


  /**
   * Fetch latest API data based on progress tracked in a local database, and output to the adapter
   * provided.
   *
   * @param db            the agent database instance storing progress for each fetchId
   * @param outputAdapter the adapter instance to receive the API elements output
   * @return total number of API elements processed successfully
   */
  public int fetchLatest(final AgentDB db, final ApiOutputAdapter outputAdapter, final HttpHost httpProxyHost) {
    final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
    httpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy()); // follow redirects for POST requests
    if (httpProxyHost != null) {
      httpClientBuilder.setProxy(httpProxyHost);
    }
    final CloseableHttpClient httpClient = httpClientBuilder.build();
    int curPageNum = 1;
    int totalCount = 0;

    // determine the timestamp to start reading new data from
    Timestamp fromTs = db.getLastReadTs(fetchId());
    if (fromTs == null || isTimestampStale(fromTs)) {
      fromTs = new Timestamp(System.currentTimeMillis() - configProperties().getDefaultLookbackSecs() * 1000);
    }

    // start fetching and processing data page by page
    T page = fetchPage(httpClient, curPageNum, fromTs);
    while (page != null && curPageNum < page.getPageCount()) {
      totalCount += processPage(page, outputAdapter);
      curPageNum += 1;
      page = fetchPage(httpClient, curPageNum, fromTs);
    }

    if (page != null) {
      // process the last page
      totalCount += processPage(page, outputAdapter);
      // persist now as the new last read timestamp
      db.setLastReadTs(fetchId(), new Timestamp(System.currentTimeMillis()));
    }

    try {
      httpClient.close();
    } catch (IOException e) {
      logger.error("Caught HTTP exception: {}", ExceptionUtils.getStackTrace(e));
    }
    return totalCount;
  }

  T fetchPage(final HttpClient httpClient, final int pageNum, final Timestamp fromTs) {
    final Date fromDate = new Date(fromTs.getTime() + 1);
    final Map<String, Object> apiQuery = new HashMap<>();
    apiQuery.put("query", apiQuery());
    apiQuery.put("variables", apiVariables(pageNum, fromDate));
    final String apiQueryJson = jsonUtil().dump(apiQuery);
    assert(apiQueryJson != null);

    final HttpPost postReq = new HttpPost(configProperties().getApiUrl());
    postReq.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    postReq.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
    postReq.addHeader(HttpHeaders.ACCEPT_ENCODING, "application/json");
    postReq.addHeader("api-token", configProperties().getApiToken());
    postReq.setEntity(new StringEntity(apiQueryJson, ContentType.APPLICATION_JSON));

    try {
      logger.debug("Fetching API data for {}, query {} ...", fetchId(), apiQueryJson);

      final HttpResponse resp = httpClient.execute(postReq);
      final int httpStatus = resp.getStatusLine().getStatusCode();
      final String httpReason = resp.getStatusLine().getReasonPhrase();

      logger.debug("HTTP status: {} {}, length {}", httpStatus, httpReason, resp.getEntity().getContentLength());

      if (httpStatus == 200) {
        JsonNode jsonNode = jsonUtil().parseTree(resp.getEntity().getContent());
        if (jsonNode != null && !jsonNode.isNull()) {
          JsonNode respData = jsonNode.get("data");
          if (respData != null && !respData.isNull()) {
            T paginatedResults = jsonUtil().parse(respData.get(apiEndpoint()), getClazz());
            if (paginatedResults != null) {
              return paginatedResults;
            }
          }
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

  private int processPage(T page, ApiOutputAdapter outputAdapter) {
    int count = page.getResults().size();
    logger.debug("{} items fetched, total page {}, total items {}", count, page.getPageCount(), page.getTotalCount());
    for (E elem : page.getResults()) {
      if (!outputAdapter.processOne(this, elem)) {
        --count;
      }
    }
    return count;
  }

  private boolean isTimestampStale(Timestamp ts) {
    return (System.currentTimeMillis() - ts.getTime()) > (14 * 24 * 60 * 60 * 1000); // older than two weeks
  }

  private DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

  private String toDateTimeString(final Date date) {
    return isoDateFormat.format(date);
  }
}
