package com.nyansa.siem.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.nyansa.siem.api.adapters.ApiOutputAdapter;
import com.nyansa.siem.api.models.PaginatedResults;
import com.nyansa.siem.util.AgentDB;
import com.nyansa.siem.util.ConfigProperties;
import com.nyansa.siem.util.JsonUtil;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public abstract class ApiPaginatedFetch<E, T extends PaginatedResults<E>> {
  private final Logger logger = LogManager.getLogger(this.getClass());

  public abstract String fetchId();

  public abstract String apiEndpoint();

  protected abstract String apiQuery(int pageNum, long fromTimestamp);

  public abstract String defaultOutputFormat();

  protected abstract Class<T> getClazz();

  public abstract String getSignatureId(E elem);

  public abstract String getCEFName(E elem);

  public abstract String getSeverity(E elem);


  public int fetchLatest(final AgentDB db, final ApiOutputAdapter outputAdapter) {
    final CloseableHttpClient httpClient = HttpClients.createDefault();
    int curPageNum = 1;
    int totalCount = 0;

    // determine the timestamp to start reading new data from
    Timestamp fromTs = db.getLastReadTs(fetchId());
    if (fromTs == null || isTimestampStale(fromTs)) {
      fromTs = new Timestamp(System.currentTimeMillis() - ConfigProperties.getDefaultLookbackSecs() * 1000);
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

  private T fetchPage(final HttpClient httpClient, final int pageNum, final Timestamp fromTs) {
    final Map<String, Object> apiQuery = new HashMap<>();
    apiQuery.put("query", apiQuery(pageNum, fromTs.getTime() + 1));
    final String apiQueryJson = JsonUtil.dump(apiQuery);
    assert(apiQueryJson != null);

    final HttpPost postReq = new HttpPost(ConfigProperties.getApiUrl());
    postReq.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    postReq.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
    postReq.addHeader(HttpHeaders.ACCEPT_ENCODING, "application/json");
    postReq.addHeader("api-token", ConfigProperties.getApiToken());
    postReq.setEntity(new StringEntity(apiQueryJson, ContentType.APPLICATION_JSON));

    try {
      logger.debug("Fetching API data for {}, page {}, fromTime {} ...", fetchId(), pageNum, fromTs);

      final HttpResponse resp = httpClient.execute(postReq);
      final int httpStatus = resp.getStatusLine().getStatusCode();
      final String httpReason = resp.getStatusLine().getReasonPhrase();

      logger.debug("HTTP status: {} {}, length {}", httpStatus, httpReason, resp.getEntity().getContentLength());

      if (httpStatus == 200) {
        JsonNode jsonNode = JsonUtil.parseTree(resp.getEntity().getContent());
        if (jsonNode != null && !jsonNode.isNull()) {
          JsonNode respData = jsonNode.get("data");
          if (respData != null && !respData.isNull()) {
            T paginatedResults = JsonUtil.parse(respData.get(apiEndpoint()), getClazz());
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
}
