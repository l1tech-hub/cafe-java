package com.cafe.service.cache;

import com.cafe.dto.RecipeDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class RecipeCache {

  private final Map<Long, RecipeDto> byId = new HashMap<>();
  private final Map<RecipeCacheKey, Page<RecipeDto>> pages = new HashMap<>();
  private List<RecipeDto> all;

  public RecipeDto getById(Long id) {
    return byId.get(id);
  }

  public void putById(Long id, RecipeDto dto) {
    byId.put(id, dto);
  }

  public void evictById(Long id) {
    byId.remove(id);
  }

  public List<RecipeDto> getAll() {
    return all;
  }

  public void putAll(List<RecipeDto> list) {
    this.all = list;
  }

  public void clearAllList() {
    this.all = null;
  }

  public Page<RecipeDto> getPage(Pageable pageable, Long dishId) {
    return pages.get(new RecipeCacheKey(pageable, dishId));
  }

  public void putPage(Pageable pageable, Page<RecipeDto> page, Long dishId) {
    pages.put(new RecipeCacheKey(pageable, dishId), page);
  }

  public void clearAllPages() {
    pages.clear();
  }

  public void clearAll() {
    byId.clear();
    pages.clear();
    all = null;
  }
}
