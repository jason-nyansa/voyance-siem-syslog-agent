package com.nyansa.siem.api.adapters;

import com.nyansa.siem.api.ApiPaginatedFetch;

public abstract class ApiOutputAdapter {

  public abstract <E> boolean processOne(final ApiPaginatedFetch<E, ?> apiFetch, final E elem);
}
