package com.cafe.service.cache;

import java.util.Objects;

public class RecipeCacheKey {

  private final Long dishId;

  public RecipeCacheKey(Long dishId) {
    this.dishId = dishId;
  }

  public Long getDishId() {
    return dishId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RecipeCacheKey)) {
      return false;
    }
    return Objects.equals(dishId, ((RecipeCacheKey) o).dishId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dishId);
  }
}
