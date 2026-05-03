package com.cafe.service;

import com.cafe.dto.BatchOrder;
import com.cafe.dto.DishDto;
import com.cafe.entity.Batch;
import com.cafe.entity.Dish;
import com.cafe.entity.Ingredient;
import com.cafe.entity.Recipe;
import com.cafe.exception.ExpiredProductsNotAllowedException;
import com.cafe.exception.InsufficientQuantityException;
import com.cafe.exception.InvalidDataException;
import com.cafe.exception.ResourceInUseException;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.mapper.DishMapper;
import com.cafe.repository.BatchRepository;
import com.cafe.repository.DishRepository;
import com.cafe.repository.RecipeRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DishService {

  private final DishRepository dishRepository;
  private final RecipeRepository recipeRepository;
  private final BatchRepository batchRepository;
  private final CookingMetricsService metricsService;

  public DishService(DishRepository dishRepository,
      RecipeRepository recipeRepository,
      BatchRepository batchRepository,
      CookingMetricsService metricsService) {
    this.dishRepository = dishRepository;
    this.recipeRepository = recipeRepository;
    this.batchRepository = batchRepository;
    this.metricsService = metricsService;
  }

  @Transactional
  public DishDto create(DishDto dto) {

    validateDish(dto);

    Recipe recipe = null;

    if (dto.getRecipeId() != null) {
      recipe = recipeRepository.findById(dto.getRecipeId())
          .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", dto.getRecipeId()));
    }

    Dish dish = DishMapper.toEntity(dto, recipe);

    if (recipe != null) {
      recipe.setDish(dish);
    }
    Dish saved = dishRepository.save(dish);
    return DishMapper.toDto(saved);
  }

  public DishDto getById(Long id) {

    Dish dish = dishRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Dish", "id", id));

    return DishMapper.toDto(dish);
  }

  public List<DishDto> getAll() {

    return dishRepository.findAll()
        .stream()
        .map(DishMapper::toDto)
        .toList();
  }

  @Transactional
  public DishDto update(Long id, DishDto dto) {

    validateDish(dto);

    Dish dish = dishRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Dish", "id", id));

    dish.setName(dto.getName());
    dish.setPrice(dto.getPrice());
    dish.setWeight(dto.getWeight());

    if (dto.getRecipeId() != null) {
      Recipe newRecipe = recipeRepository.findById(dto.getRecipeId())
          .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", dto.getRecipeId()));

      Recipe oldRecipe = dish.getRecipe();
      if (oldRecipe != null && !oldRecipe.getId().equals(newRecipe.getId())) {
        oldRecipe.setDish(null);
      }

      dish.setRecipe(newRecipe);
      newRecipe.setDish(dish);
    } else {

      Recipe oldRecipe = dish.getRecipe();
      if (oldRecipe != null) {
        oldRecipe.setDish(null);
        dish.setRecipe(null);
      }
    }
    return DishMapper.toDto(dishRepository.save(dish));
  }

  @Transactional
  public void delete(@NonNull Long id) {

    Dish dish = dishRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Dish", "id", id));

    if (dish.getRecipe() != null) {
      throw new ResourceInUseException("Dish", "delete", "it has an associated recipe");
    }

    dishRepository.deleteById(id);
  }

  public List<DishDto> searchByName(String name) {

    return dishRepository.findByNameContainingIgnoreCase(name)
        .stream()
        .map(DishMapper::toDto)
        .toList();
  }

  @Transactional
  public void cook(@NonNull Long dishId, boolean allowExpiredProducts, BatchOrder batchOrder) {

    Dish dish = dishRepository.findWithRecipeGraphById(dishId)
        .orElseThrow(() -> new ResourceNotFoundException("Dish", "id", dishId));

    Recipe recipe = dish.getRecipe();
    if (recipe == null) {
      throw new InvalidDataException("recipe", null, "Dish has no recipe");
    }

    LocalDate today = LocalDate.now();

    for (Ingredient ingredient : recipe.getIngredients()) {
      cookIngredient(ingredient, today, allowExpiredProducts, batchOrder);
    }

    metricsService.increment(dishId);
  }

  private void cookIngredient(Ingredient ingredient,
      LocalDate today,
      boolean allowExpiredProducts,
      BatchOrder batchOrder) {

    double required = ingredient.getQuantity();

    List<Batch> batches = getOrderedBatches(
        ingredient.getProduct().getId(),
        today,
        allowExpiredProducts,
        batchOrder
    );

    ConsumptionResult result = consume(batches, required, today);

    if (result.remaining > 0) {
      throw new InsufficientQuantityException(
          ingredient.getProduct().getName(),
          result.remaining
      );
    }

    if (!allowExpiredProducts && result.usedExpired) {
      throw new ExpiredProductsNotAllowedException(
          ingredient.getProduct().getName()
      );
    }
  }

  private List<Batch> getOrderedBatches(Long productId,
      LocalDate today,
      boolean allowExpiredProducts,
      BatchOrder batchOrder) {

    List<Batch> positive = batchRepository.findByProductId(productId).stream()
        .filter(b -> b.getQuantity() != null && b.getQuantity() > 0)
        .toList();

    return BatchSelectionHelper.orderForCooking(positive, today, allowExpiredProducts, batchOrder);
  }

  private ConsumptionResult consume(List<Batch> batches,
      double required,
      LocalDate today) {

    double remaining = required;
    boolean usedExpired = false;

    for (Batch batch : batches) {

      double available = batch.getQuantity();

      if (remaining > 0 && available > 0) {

        if (batch.getExpiryDate().isBefore(today)) {
          usedExpired = true;
        }

        double used = Math.min(available, remaining);

        batch.setQuantity(available - used);
        remaining -= used;
      }
    }

    return new ConsumptionResult(remaining, usedExpired);
  }

  private static class ConsumptionResult {

    final double remaining;
    final boolean usedExpired;

    ConsumptionResult(double remaining, boolean usedExpired) {
      this.remaining = remaining;
      this.usedExpired = usedExpired;
    }
  }

  private void validateDish(DishDto dto) {

    if (dto.getPrice() < 0) {
      throw new InvalidDataException("price", dto.getPrice(), "must be >= 0");
    }

    if (dto.getWeight() <= 0) {
      throw new InvalidDataException("weight", dto.getWeight(), "must be > 0");
    }
  }
}
