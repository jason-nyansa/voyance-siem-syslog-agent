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

import com.nyansa.siem.api.adapters.ApiOutputAdapter;
import com.nyansa.siem.api.models.IoTOutlier;
import com.nyansa.siem.api.models.IoTOutlierList;
import com.nyansa.siem.util.AbstractDBTest;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class ApiPaginatedFetchTest extends AbstractDBTest {
  private final String uuid = "192.168.0.1";
  private final String model = "phone";

  @Spy
  private IoTOutlierListFetch testApiFetch;

  @Mock
  private ApiOutputAdapter mockOutputAdapter;

  private IoTOutlier elem;

  private final HttpHost mockHttpProxyHost = null;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this);

    elem = new IoTOutlier();
    elem.setUuid(uuid);
    elem.setModel(model);

    when(mockOutputAdapter.processOne(eq(testApiFetch), any())).thenReturn(true);
  }

  @Test
  void testFetchLatest_multiPages() {
    final int pageCount = 3;
    final int totalCount = 17;
    final Timestamp t0 = new Timestamp(System.currentTimeMillis());

    agentDb.setLastReadTs(testApiFetch.fetchId(), t0);
    ArgumentCaptor<Timestamp> fromTsArg = ArgumentCaptor.forClass(Timestamp.class);

    doThrow(new IllegalArgumentException("don't expect page number to hit 0")).when(testApiFetch).fetchPage(any(HttpClient.class), eq(0), fromTsArg.capture());
    doReturn(makePage(1, 7, pageCount, totalCount)).when(testApiFetch).fetchPage(any(HttpClient.class), eq(1), fromTsArg.capture());
    doReturn(makePage(2, 7, pageCount, totalCount)).when(testApiFetch).fetchPage(any(HttpClient.class), eq(2), fromTsArg.capture());
    doReturn(makePage(3, 3, pageCount, totalCount)).when(testApiFetch).fetchPage(any(HttpClient.class), eq(3), fromTsArg.capture());
    doThrow(new IllegalArgumentException("don't expect page number to hit 4")).when(testApiFetch).fetchPage(any(HttpClient.class), eq(4), fromTsArg.capture());

    int processedCount = testApiFetch.fetchLatest(agentDb, mockOutputAdapter, mockHttpProxyHost);

    assertEquals(totalCount, processedCount);
    assertEquals(t0, fromTsArg.getValue());

    final Timestamp t1 = agentDb.getLastReadTs(testApiFetch.fetchId());
    assertTrue(t1.getTime() > t0.getTime());
  }

  @Test
  void testFetchLatest_singlePage() {
    final int pageCount = 1;
    final int totalCount = 19;
    final Timestamp t0 = new Timestamp(System.currentTimeMillis());

    agentDb.setLastReadTs(testApiFetch.fetchId(), t0);

    doThrow(new IllegalArgumentException("don't expect page number to hit 0")).when(testApiFetch).fetchPage(any(HttpClient.class), eq(0), any());
    doReturn(makePage(1, 19, pageCount, totalCount)).when(testApiFetch).fetchPage(any(HttpClient.class), eq(1), any());
    doThrow(new IllegalArgumentException("don't expect page number to hit 2")).when(testApiFetch).fetchPage(any(HttpClient.class), eq(2), any());

    int processedCount = testApiFetch.fetchLatest(agentDb, mockOutputAdapter, mockHttpProxyHost);
    assertEquals(totalCount, processedCount);
  }

  private IoTOutlierList makePage(final int pageNum, final int pageSize, final int pageCount, final int totalCount) {
    final IoTOutlierList page = new IoTOutlierList();
    final List<IoTOutlier> elems = new ArrayList<>();
    for (int i = 0; i < pageSize; ++i) {
      elems.add(elem);
    }
    page.setIoTOutliers(elems);
    page.setPage(pageNum);
    page.setPageSize(pageSize);
    page.setPageCount(pageCount);
    page.setTotalCount(totalCount);
    return page;
  }
}
