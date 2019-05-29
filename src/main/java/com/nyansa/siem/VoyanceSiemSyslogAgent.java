package com.nyansa.siem;

import com.nyansa.siem.api.ApiPaginatedFetch;
import com.nyansa.siem.api.IoTOutlierListFetch;
import com.nyansa.siem.api.adapters.ApiOutputAdapter;
import com.nyansa.siem.api.adapters.ApiSyslogAdapter;
import com.nyansa.siem.util.AgentDB;
import com.nyansa.siem.util.ConfigProperties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VoyanceSiemSyslogAgent {
  private static final Logger logger = LogManager.getLogger(VoyanceSiemSyslogAgent.class);

  private static AgentDB agentDb;
  private static ApiOutputAdapter syslogAdapter;

  public static void main(String[] args) {
    logger.info("VoyanceSiemSyslogAgent started");

    try {
      ConfigProperties.validateAll();
      agentDb = new AgentDB();
      syslogAdapter = new ApiSyslogAdapter();
      final IoTOutlierListFetch iotOutliersFetch = new IoTOutlierListFetch();

      while (true) {
        fetchApi(iotOutliersFetch);

        Thread.sleep(ConfigProperties.getApiPullFreqSecs() * 1000);
      }
    } catch (Exception e) {
      logger.error("Caught fatal exception, aborting: {}", ExceptionUtils.getStackTrace(e));
      System.exit(1);
    } finally {
      if (agentDb != null) {
        agentDb.close();
      }
    }
  }

  private static void fetchApi(final ApiPaginatedFetch apiFetch) {
    logger.info("Fetching {} ...", apiFetch.fetchId());
    int sentCount = apiFetch.fetchLatest(agentDb, syslogAdapter);
    logger.info("{} {} lines sent", apiFetch.fetchId(), sentCount);
  }
}
