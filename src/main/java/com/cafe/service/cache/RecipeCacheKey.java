package com.cafe.service.cache;

import java.util.Objects;
import org.springframework.data.domain.Pageable;

public class RecipeCacheKey {

  private final int page;
  private final int size;
  private final String sort;

  public RecipeCacheKey(Pageable pageable) {
    this.page = pageable.getPageNumber();
    this.size = pageable.getPageSize();
    this.sort = pageable.getSort().toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RecipeCacheKey)) return false;
    RecipeCacheKey that = (RecipeCacheKey) o;
    return page == that.page &&
        size == that.size &&
        Objects.equals(sort, that.sort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, size, sort);
  }
}