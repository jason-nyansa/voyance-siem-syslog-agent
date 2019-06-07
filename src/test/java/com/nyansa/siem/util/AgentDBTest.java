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

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class AgentDBTest extends AbstractDBTest {
  private static final String fetchId = "foobar";

  private final long t0InMs = System.currentTimeMillis();
  private final Timestamp t0 = new Timestamp(t0InMs);
  private final Timestamp t1 = new Timestamp(t0InMs + 1000);

  @Test
  void testLastReadTs() {
    assertNull(agentDb.getLastReadTs(fetchId));

    agentDb.setLastReadTs(fetchId, t0);
    assertEquals(t0, agentDb.getLastReadTs(fetchId));

    agentDb.setLastReadTs(fetchId, t1);
    assertEquals(t1, agentDb.getLastReadTs(fetchId));
  }
}
