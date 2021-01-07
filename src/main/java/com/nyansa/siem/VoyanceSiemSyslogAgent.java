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

import com.nyansa.siem.api.*;
import com.nyansa.siem.api.adapters.ApiOutputAdapter;
import com.nyansa.siem.api.adapters.ApiSyslogAdapter;
import com.nyansa.siem.api.adapters.ApiZmqJsonAdapter;
import com.nyansa.siem.api.models.AggregatedWindow;
import com.nyansa.siem.util.AgentDB;
import com.nyansa.siem.util.ConfigProperties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.nyansa.siem.util.ConfigProperties.configProperties;

public class VoyanceSiemSyslogAgent {
  private static final Logger logger = LogManager.getLogger(VoyanceSiemSyslogAgent.class);

  private AgentDB agentDb;
  private ApiOutputAdapter outputAdapter;
  private ConfigProperties configProps;
  private ScheduledExecutorService executorService;
  private List<ApiPaginatedFetch> apiFetches;
  private HttpHost httpProxyHost;

  public static final List<ApiPaginatedFetch> AllAvailableApiFetches = Arrays.asList(
      new IoTOutlierListFetch(),
      new IoTDeviceStatsListFetch(AggregatedWindow.Last3Hours),
      new IoTDeviceStatsListFetch(AggregatedWindow.Last24Hours),
      new IoTDeviceStatsListFetch(AggregatedWindow.Last7Days),
      new IoTDeviceStatsListFetch(AggregatedWindow.Last14Days),
      new IoTGroupStatsListFetch(AggregatedWindow.Last3Hours),
      new IoTGroupStatsListFetch(AggregatedWindow.Last24Hours),
      new IoTGroupStatsListFetch(AggregatedWindow.Last7Days),
      new IoTGroupStatsListFetch(AggregatedWindow.Last14Days),
      new DeviceListFetch(),
      new ApplicationListFetch(AggregatedWindow.Last3Hours),
      new ApplicationListFetch(AggregatedWindow.Last24Hours),
      new DeviceEventListFetch(),
      new VcoEnterpriseEventFetch()
  );

  private VoyanceSiemSyslogAgent() throws SQLException {
    this(null, null, null);
  }

  private VoyanceSiemSyslogAgent(ApiOutputAdapter inOutputAdapter) throws SQLException {
    this(null, inOutputAdapter, null);
  }

  VoyanceSiemSyslogAgent(AgentDB inAgentDb, ApiOutputAdapter inOutputAdapter, ConfigProperties inConfigProps) throws SQLException {
    if (inAgentDb == null) {
      inAgentDb = new AgentDB();
    }
    if (inOutputAdapter == null) {
      inOutputAdapter = new ApiSyslogAdapter();
    }
    if (inConfigProps == null) {
      inConfigProps = configProperties();
    }
    agentDb = inAgentDb;
    outputAdapter = inOutputAdapter;
    configProps = inConfigProps;
    executorService = Executors.newScheduledThreadPool(configProps.getApiPullThreads());
    apiFetches = configProps.getApiFetchesEnabled();
    httpProxyHost = configProps.getHttpProxy();
  }

  public static void main(String[] args) throws SQLException {
    if (args.length > 0) {
      switch (args[0]) {
        case "validate": {
          configProperties().validateAll();
          System.out.println("config.properties validated, no issues found.");
          break;
        }
        case "show_apis": {
          System.out.println("All available APIs:");
          AllAvailableApiFetches.forEach(api -> System.out.println(api.fetchId()));
          break;
        }
      }
      System.exit(0);
    }

    configProperties().validateAll();
    final ApiZmqJsonAdapter zmqJsonAdapter = new ApiZmqJsonAdapter();
    VoyanceSiemSyslogAgent agent = new VoyanceSiemSyslogAgent(zmqJsonAdapter);
    int status = agent.start();
    System.exit(status);
  }

  int start() {
    logger.info("VoyanceSiemSyslogAgent started");

    try {
      List<Future<?>> futures = new ArrayList<>();

      for (ApiPaginatedFetch api : apiFetches) {
        long pullFreqSecs = configProps.getApiPullFreqSecs(api.fetchId());
        long initDelaySecs = 0;
        Timestamp lastRead = agentDb.getLastReadTs(api.fetchId());
        if (lastRead != null) {
          initDelaySecs = (lastRead.getTime() + (pullFreqSecs * 1000) - System.currentTimeMillis()) / 1000;
          initDelaySecs = Math.max(initDelaySecs, 0);
        }
        futures.add(executorService.scheduleWithFixedDelay(() -> fetchApi(api), initDelaySecs, pullFreqSecs, TimeUnit.SECONDS));
        logger.info("Scheduled {} every {} seconds, after a delay of {} seconds", api.fetchId(), pullFreqSecs, initDelaySecs);
      }

      boolean terminated = false;
      while (!terminated) {
        for (Future f : futures) {
          if (f.isDone()) {
            f.get(); // propagate exception if any
          }
        }
        terminated = awaitTermination();
      }

      logger.info("VoyanceSiemSyslogAgent terminated");
      return 0;
    } catch (Exception e) {
      logger.error("Caught fatal exception, aborting: {}", ExceptionUtils.getStackTrace(e));
      return 1;
    } finally {
      if (agentDb != null) {
        agentDb.close();
      }
    }
  }

  boolean awaitTermination() throws InterruptedException {
    return executorService.awaitTermination(60, TimeUnit.SECONDS);
  }

  private void fetchApi(final ApiPaginatedFetch api) {
    logger.info("Fetching {} ...", api.fetchId());
    int sentCount = api.fetchLatest(agentDb, outputAdapter, httpProxyHost);
    logger.info("{} {} lines sent", api.fetchId(), sentCount);
  }
}
