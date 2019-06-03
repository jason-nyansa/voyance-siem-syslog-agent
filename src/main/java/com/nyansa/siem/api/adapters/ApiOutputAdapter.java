package com.nyansa.siem.api.adapters;

import com.nyansa.siem.api.ApiPaginatedFetch;

/**
 * Abstract adapter class for processing API output data elements.
 */
public abstract class ApiOutputAdapter {

  /**
   * Specify logic in subclass to process one API element and emit to an output.
   *
   * @param apiFetch the API fetch instance provided for reference
   * @param elem     the API data element to process
   * @param <E>      the generic type captured for the element
   * @return true if the element is processed successfully, false otherwise
   */
  public abstract <E> boolean processOne(final ApiPaginatedFetch<E, ?> apiFetch, final E elem);
}
