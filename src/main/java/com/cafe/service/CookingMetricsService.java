package com.cafe.service;

import com.cafe.dto.DishCookStatDto;
import com.cafe.dto.ProductSpentDto;
import com.cafe.entity.Dish;
import com.cafe.entity.DishCookStatistics;
import com.cafe.entity.Ingredient;
import com.cafe.repository.DishCookStatisticsRepository;
import com.cafe.repository.DishRepository;
import jakarta.annotation.PostConstruct;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CookingMetricsService {

  private final DishCookStatisticsRepository statisticsRepository;
  private final DishRepository dishRepository;

  private final Map<Long, Integer> unsafeCounters = new HashMap<>();

  private final Map<Long, AtomicInteger> dishCounters = new ConcurrentHashMap<>();

  public CookingMetricsService(DishCookStatisticsRepository statisticsRepository,
      DishRepository dishRepository) {
    this.statisticsRepository = statisticsRepository;
    this.dishRepository = dishRepository;
  }

  @PostConstruct
  void loadCountersFromDatabase() {
    statisticsRepository.findAll().forEach(row ->
        dishCounters.put(row.getDishId(), new AtomicInteger(row.getCookCount()))
    );
  }

  public void unsafeIncrement(Long dishId) {
    Integer current = unsafeCounters.getOrDefault(dishId, 0);
    unsafeCounters.put(dishId, current + 1);
  }

  public int getUnsafe(Long dishId) {
    return unsafeCounters.getOrDefault(dishId, 0);
  }

  @Transactional
  public void increment(Long dishId) {
    statisticsRepository.upsertIncrementCookCount(dishId);
    DishCookStatistics updated = statisticsRepository.findByDishId(dishId)
        .orElseThrow();
    dishCounters.put(dishId, new AtomicInteger(updated.getCookCount()));
  }

  public int getCount(Long dishId) {
    return dishCounters.getOrDefault(dishId, new AtomicInteger(0)).get();
  }

  public List<DishCookStatDto> listCookStatistics() {
    return statisticsRepository.findAll().stream()
        .sorted(Comparator.comparing(DishCookStatistics::getDishId))
        .map(row -> {
          Dish dish = dishRepository.findById(row.getDishId()).orElse(null);
          String dishName = dish != null ? dish.getName() : "—";
          double price = dish != null && dish.getPrice() != null ? dish.getPrice() : 0.0;
          double weightG = dish != null && dish.getWeight() != null ? dish.getWeight() : 0.0;
          int count = row.getCookCount();
          double totalPrice = price * count;
          double totalMassKg = weightG * count / 1000.0;
          return new DishCookStatDto(
              row.getDishId(),
              dishName,
              count,
              dish != null ? dish.getPrice() : null,
              dish != null ? dish.getWeight() : null,
              totalPrice,
              totalMassKg
          );
        })
        .toList();
  }

  /**
   * По каждому продукту из рецептов: граммы ингредиента на порцию × число приготовлений, сумма в кг.
   */
  public List<ProductSpentDto> listSpentProductsKilograms() {
    Map<Long, Double> gramsByProductId = new TreeMap<>();
    Map<Long, String> nameByProductId = new HashMap<>();

    for (DishCookStatistics row : statisticsRepository.findAll()) {
      int cookCount = row.getCookCount();
      if (cookCount <= 0) {
        continue;
      }
      dishRepository.findWithRecipeGraphById(row.getDishId()).ifPresent(dish -> {
        if (dish.getRecipe() == null) {
          return;
        }
        for (Ingredient ing : dish.getRecipe().getIngredients()) {
          if (ing.getProduct() == null || ing.getQuantity() == null) {
            continue;
          }
          long productId = ing.getProduct().getId();
          nameByProductId.put(productId, ing.getProduct().getName());
          gramsByProductId.merge(productId, ing.getQuantity() * cookCount, Double::sum);
        }
      });
    }

    return gramsByProductId.entrySet().stream()
        .map(e -> new ProductSpentDto(
            nameByProductId.getOrDefault(e.getKey(), "—"),
            e.getValue() / 1000.0
        ))
        .sorted(Comparator.comparing(ProductSpentDto::getProductName, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }
}
