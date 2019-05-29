package com.nyansa.siem.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SyslogLogger {
  private static final Logger syslogLogger = LogManager.getLogger("syslog");
  private static final Logger logger = LogManager.getLogger(SyslogLogger.class);

  public static boolean send(final String message) {
    syslogLogger.log(Level.getLevel("SYSLOG"), message);
    logger.trace("Sent SYSLOG: " + message);
    return true;
  }
}
