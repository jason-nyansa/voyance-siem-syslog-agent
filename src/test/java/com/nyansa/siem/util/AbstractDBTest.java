package com.nyansa.siem.util;

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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.sql.SQLException;

public abstract class AbstractDBTest {
  protected static AgentDB agentDb = null;
  private static final String TEST_DB_NAME = "voyance-agent-test";

  @BeforeAll
  static void createDB() throws SQLException {
    agentDb = new AgentDB(TEST_DB_NAME);
  }

  @AfterAll
  static void destroyDB() {
    if (agentDb != null) {
      agentDb.close();
    }
    final File dbFile = new File(TEST_DB_NAME + ".mv.db");
    if (dbFile.exists()) {
      dbFile.delete();
    }
  }
}
