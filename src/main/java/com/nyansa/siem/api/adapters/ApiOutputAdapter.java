package com.nyansa.siem.api.adapters;

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

/**
 * Adapter interface for processing API output data elements.
 */
public interface ApiOutputAdapter {

  /**
   * Specify logic in subclass to process one API element and emit to an output.
   *
   * @param apiFetch the API fetch instance provided for reference
   * @param elem     the API data element to process
   * @param <E>      the generic type captured for the element
   * @return true if the element is processed successfully, false otherwise
   */
  public <E> boolean processOne(final ApiPaginatedFetch<E, ?> apiFetch, final E elem);
}
