package com.cafe.service.cache;

import java.util.Objects;
import org.springframework.data.domain.Pageable;

public class RecipeCacheKey {

  private final int page;
  private final int size;
  private final String sort;
  private final Long dishId;

  public RecipeCacheKey(Pageable pageable, Long dishId) {
    this.page = pageable.getPageNumber();
    this.size = pageable.getPageSize();
    this.sort = pageable.getSort().toString();
    this.dishId = dishId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RecipeCacheKey)) {
      return false;
    }
    RecipeCacheKey that = (RecipeCacheKey) o;
    return page == that.page
        && size == that.size
        && Objects.equals(sort, that.sort)
        && dishId == that.dishId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, size, sort, dishId);
  }
}