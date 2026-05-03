package com.cafe.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class CookingMetricsService {

  private final Map<Long, Integer> unsafeCounters = new HashMap<>();

  private final Map<Long, AtomicInteger> dishCounters = new ConcurrentHashMap<>();


  public void unsafeIncrement(Long dishId) {
    Integer current = unsafeCounters.getOrDefault(dishId, 0);
    unsafeCounters.put(dishId, current + 1);
  }

  public int getUnsafe(Long dishId) {
    return unsafeCounters.getOrDefault(dishId, 0);
  }


  public void increment(Long dishId) {
    dishCounters
        .computeIfAbsent(dishId, id -> new AtomicInteger(0))
        .incrementAndGet();
  }

  public int getCount(Long dishId) {
    return dishCounters.getOrDefault(dishId, new AtomicInteger(0)).get();
  }
}
