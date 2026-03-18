package com.cafe.service;

import com.cafe.dto.DishDto;
import com.cafe.entity.Dish;
import com.cafe.entity.Recipe;
import com.cafe.mapper.DishMapper;
import com.cafe.repository.DishRepository;
import com.cafe.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DishService {

  private final DishRepository dishRepository;
  private final RecipeRepository recipeRepository;

  public DishService(DishRepository dishRepository,
      RecipeRepository recipeRepository) {
    this.dishRepository = dishRepository;
    this.recipeRepository = recipeRepository;
  }

  @Transactional
  public DishDto create(DishDto dto) {

    Recipe recipe = null;

    if (dto.getRecipeId() != null) {
      recipe = recipeRepository.findById(dto.getRecipeId())
          .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));
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
        .orElseThrow(() -> new EntityNotFoundException("Dish not found"));

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

    Dish dish = dishRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Dish not found"));

    dish.setName(dto.getName());
    dish.setPrice(dto.getPrice());
    dish.setWeight(dto.getWeight());

    if (dto.getRecipeId() != null) {
      Recipe newRecipe = recipeRepository.findById(dto.getRecipeId())
          .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

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
  public void delete(Long id) {
    dishRepository.deleteById(id);
  }

  public List<DishDto> searchByName(String name) {

    return dishRepository.findByNameContainingIgnoreCase(name)
        .stream()
        .map(DishMapper::toDto)
        .toList();
  }
}