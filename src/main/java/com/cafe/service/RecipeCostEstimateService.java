package com.cafe.service;

import com.cafe.dto.BatchOrder;
import com.cafe.dto.RecipeCostEstimateDto;
import com.cafe.dto.RecipeCostLineDto;
import com.cafe.entity.Batch;
import com.cafe.entity.Ingredient;
import com.cafe.entity.Recipe;
import com.cafe.exception.InvalidDataException;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.repository.BatchRepository;
import com.cafe.repository.RecipeRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeCostEstimateService {

  private static final double GRAMS_PER_KG = 1000.0;

  private final RecipeRepository recipeRepository;
  private final BatchRepository batchRepository;

  public RecipeCostEstimateService(RecipeRepository recipeRepository,
      BatchRepository batchRepository) {
    this.recipeRepository = recipeRepository;
    this.batchRepository = batchRepository;
  }

  /**
   * Оценка стоимости по партиям с {@code expiryDate >= date}, порядок списания — {@code order}.
   * Согласовано с запросом «недостающих» ингредиентов (только непросроченные на дату).
   */
  @Transactional(readOnly = true)
  public RecipeCostEstimateDto estimate(Long recipeId, Double iterations, LocalDate date,
      BatchOrder order) {

    if (iterations == null || iterations <= 0) {
      throw new InvalidDataException("iterations", iterations, "must be > 0");
    }

    Recipe recipe = recipeRepository.findById(recipeId)
        .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", recipeId));

    List<RecipeCostLineDto> lines = new ArrayList<>();
    double total = 0.0;

    for (Ingredient ing : recipe.getIngredients()) {
      double required = ing.getQuantity() * iterations;
      double lineCost = simulateIngredientCost(ing.getProduct().getId(), required, date, order);
      lines.add(new RecipeCostLineDto(
          ing.getId(),
          ing.getProduct().getName(),
          required,
          lineCost
      ));
      total += lineCost;
    }

    return new RecipeCostEstimateDto(lines, total);
  }

  private double simulateIngredientCost(Long productId, double required, LocalDate date,
      BatchOrder order) {

    List<Batch> batches = new ArrayList<>(batchRepository.findByProductId(productId).stream()
        .filter(b -> b.getQuantity() != null && b.getQuantity() > 0)
        .filter(b -> !b.getExpiryDate().isBefore(date))
        .toList());
    BatchSelectionHelper.sortByOrder(batches, order);

    double remaining = required;
    double cost = 0.0;
    for (Batch b : batches) {
      if (remaining <= 0) {
        break;
      }
      double take = Math.min(b.getQuantity(), remaining);
      double pricePerKg = b.getPrice() != null ? b.getPrice() : 0.0;
      cost += take * (pricePerKg / GRAMS_PER_KG);
      remaining -= take;
    }
    return cost;
  }
}
