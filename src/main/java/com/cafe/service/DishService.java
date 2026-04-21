package com.cafe.service;

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
import java.util.ArrayList;
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
  public void cook(@NonNull Long dishId, boolean allowExpiredProducts) {
  
    Dish dish = dishRepository.findById(dishId)
        .orElseThrow(() -> new ResourceNotFoundException("Dish", "id", dishId));
  
    Recipe recipe = dish.getRecipe();
  
    if (recipe == null) {
      throw new InvalidDataException("recipe", null, "Dish has no recipe");
    }
  
    LocalDate today = LocalDate.now();
  
    for (Ingredient ingredient : recipe.getIngredients()) {
  
      double required = ingredient.getQuantity();
  
      List<Batch> allBatches = batchRepository
          .findByProductIdOrderByExpiryDateAsc(ingredient.getProduct().getId());
  
      List<Batch> expired = new ArrayList<>();
      List<Batch> valid = new ArrayList<>();
  
      for (Batch batch : allBatches) {
  
        if (batch.getQuantity() <= 0) {
          continue;
        }
  
        if (batch.getExpiryDate().isBefore(today)) {
          expired.add(batch);
        } else {
          valid.add(batch);
        }
      }
  
      List<Batch> ordered = new ArrayList<>();
  
      if (allowExpiredProducts) {
        ordered.addAll(expired);
        ordered.addAll(valid);
      } else {
        ordered.addAll(valid);
        ordered.addAll(expired);
      }
  
      double remaining = required;
      boolean usedExpired = false;
  
      for (Batch batch : ordered) {
  
        if (remaining <= 0) {
          break;
        }
  
        double available = batch.getQuantity();
        if (available <= 0) {
          continue;
        }
  
        boolean isExpired = batch.getExpiryDate().isBefore(today);
  
        if (isExpired) {
          usedExpired = true;
        }
  
        double used = Math.min(available, remaining);
  
        batch.setQuantity(available - used);
        remaining -= used;
      }
  
      if (remaining > 0) {
        throw new InsufficientQuantityException(ingredient.getProduct().getName(), remaining);
      }
  
      if (!allowExpiredProducts && usedExpired) {
        throw new ExpiredProductsNotAllowedException(ingredient.getProduct().getName());
      }
    }
  
    metricsService.increment(dishId);
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
