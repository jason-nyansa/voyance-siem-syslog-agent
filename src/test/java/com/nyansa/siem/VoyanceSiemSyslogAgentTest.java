package com.nyansa.siem;

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

import com.nyansa.siem.api.ApiPaginatedFetch;
import com.nyansa.siem.api.adapters.ApiOutputAdapter;
import com.nyansa.siem.util.AgentDB;
import com.nyansa.siem.util.ConfigProperties;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class VoyanceSiemSyslogAgentTest {
  private static final Logger logger = LogManager.getLogger(VoyanceSiemSyslogAgentTest.class);

  private final String api1FetchId = "api_1";
  private final String api2FetchId = "api_2";

  @Mock
  private AgentDB mockDb;

  @Mock
  private ApiOutputAdapter mockOutputAdapter;

  @Mock
  private ConfigProperties mockConfigProps;

  @Mock
  private ApiPaginatedFetch mockApi1;

  @Mock
  private ApiPaginatedFetch mockApi2;

  @Spy
  private VoyanceSiemSyslogAgent testAgent;

  private final HttpHost mockHttpProxyHost = null;

  @BeforeEach
  void setup() throws SQLException {
    mockDb = mock(AgentDB.class);
    mockOutputAdapter = mock(ApiOutputAdapter.class);
    mockConfigProps = mock(ConfigProperties.class);

    mockApi1 = mock(ApiPaginatedFetch.class);
    mockApi2 = mock(ApiPaginatedFetch.class);
    when(mockApi1.fetchId()).thenReturn(api1FetchId);
    when(mockApi2.fetchId()).thenReturn(api2FetchId);

    when(mockConfigProps.getApiPullThreads()).thenReturn(2);
    when(mockConfigProps.getApiFetchesEnabled()).thenReturn(Arrays.asList(mockApi1, mockApi2));

    testAgent = spy(new VoyanceSiemSyslogAgent(mockDb, mockOutputAdapter, mockConfigProps));
  }

  @Test
  void testStart() throws InterruptedException {
    final int initCount = 10000;
    CountDownLatch cdl = new CountDownLatch(initCount);

    when(mockConfigProps.getApiPullFreqSecs(api1FetchId)).thenReturn(1L); // API 1 fires every second
    when(mockConfigProps.getApiPullFreqSecs(api2FetchId)).thenReturn(3L); // API 2 fires every 3 seconds

    final Timestamp t0 = new Timestamp(System.currentTimeMillis());
    when(mockDb.getLastReadTs(api2FetchId)).thenReturn(t0); // API 2 will have a initial delay of 3 seconds

    when(mockApi1.fetchLatest(mockDb, mockOutputAdapter, mockHttpProxyHost)).thenAnswer((Answer) invocation -> {
      long before = cdl.getCount();
      cdl.countDown();
      long after = cdl.getCount();
      logger.info("API 1 count to: " + after);
      return (int)(before - after);
    });

    when(mockApi2.fetchLatest(mockDb, mockOutputAdapter, mockHttpProxyHost)).thenAnswer((Answer) invocation -> {
      long before = cdl.getCount();
      for (int i = 0; i < initCount / 2; ++i) {
        cdl.countDown();
      }
      long after = cdl.getCount();
      logger.info("API 2 count to: " + after);
      return (int)(before - after);
    });

    doReturn(true).when(testAgent).awaitTermination();

    testAgent.start();
    cdl.await(1, TimeUnit.SECONDS);
    long curCount = cdl.getCount();
    assertTrue(curCount > (initCount - 5) && curCount < initCount); // API 1 immediately counted down

    cdl.await();
    long elapsedSecs = (System.currentTimeMillis() - t0.getTime()) / 1000L;
    logger.info("Elapsed secs: " + elapsedSecs);
    assertTrue(elapsedSecs > 3L && elapsedSecs < 9L); // API 2 should have fired at least twice
  }
}
