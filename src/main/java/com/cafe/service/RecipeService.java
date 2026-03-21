package com.cafe.service;

import com.cafe.dto.CreateRecipeDto;
import com.cafe.dto.IngredientDto;
import com.cafe.dto.RecipeDto;
import com.cafe.dto.RecipeIngredientRequestDto;
import com.cafe.entity.Dish;
import com.cafe.entity.Ingredient;
import com.cafe.entity.Product;
import com.cafe.entity.Recipe;
import com.cafe.mapper.RecipeMapper;
import com.cafe.repository.DishRepository;
import com.cafe.repository.IngredientRepository;
import com.cafe.repository.ProductRepository;
import com.cafe.repository.RecipeRepository;
import com.cafe.service.cache.RecipeCache;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeService {

  private static final String DISH_NOT_FOUND_MSG = "Dish not found";

  private final RecipeRepository recipeRepository;
  private final IngredientRepository ingredientRepository;
  private final ProductRepository productRepository;
  private final DishRepository dishRepository;

  private final RecipeCache recipeCache;

  public RecipeService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository,
      ProductRepository productRepository, DishRepository dishRepository, RecipeCache recipeCache) {
    this.recipeRepository = recipeRepository;
    this.ingredientRepository = ingredientRepository;
    this.productRepository = productRepository;
    this.dishRepository = dishRepository;
    this.recipeCache = recipeCache;
  }

  @Transactional
  public RecipeDto createRecipe(RecipeDto dto) {
    Dish dish = dishRepository.findById(dto.getDishId())
        .orElseThrow(() -> new EntityNotFoundException(DISH_NOT_FOUND_MSG));

    Recipe recipe = RecipeMapper.toEntity(dto, dish);
    Recipe saved = recipeRepository.save(recipe);

    recipeCache.clearAll();

    return RecipeMapper.toDto(saved);
  }

  @Transactional
  public Recipe createRecipeWithIngredients(CreateRecipeDto request) {
    Recipe recipe = new Recipe();
    recipe.setName(request.getName());
    recipe.setInstructions(request.getInstructions());

    if (request.getDishId() != null) {
      Dish dish = dishRepository.findById(request.getDishId())
          .orElseThrow(() -> new EntityNotFoundException(DISH_NOT_FOUND_MSG));
      recipe.setDish(dish);
    }

    recipeRepository.save(recipe);

    for (IngredientDto dto : request.getIngredients()) {
      saveIngredient(recipe, dto.getProductId(), dto.getQuantity());
    }

    recipeCache.clearAll();

    return recipe;
  }

  public List<RecipeDto> getAll() {
    List<RecipeDto> cached = recipeCache.getAll();
    if (cached != null) {
      return cached;
    }

    List<RecipeDto> result = recipeRepository.findAll().stream().map(RecipeMapper::toDto).toList();

    recipeCache.putAll(result);

    return result;
  }

  public Page<RecipeDto> getAllPaged(Pageable pageable) {
    Page<RecipeDto> cached = recipeCache.getPage(pageable);
    if (cached != null) {
      return cached;
    }

    Page<RecipeDto> page = recipeRepository.findAllPageable(pageable).map(RecipeMapper::toDto);

    recipeCache.putPage(pageable, page);

    return page;
  }

  public RecipeDto getById(Long id) {
    RecipeDto cached = recipeCache.getById(id);
    if (cached != null) {
      return cached;
    }

    Recipe recipe = recipeRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

    RecipeDto dto = RecipeMapper.toDto(recipe);

    recipeCache.putById(id, dto);

    return dto;
  }

  @Transactional
  public RecipeDto updateRecipe(Long id, RecipeDto updatedRecipe) {
    Recipe recipe = recipeRepository.findById(id).orElseThrow();

    recipe.setName(updatedRecipe.getName());
    recipe.setInstructions(updatedRecipe.getInstructions());

    if (updatedRecipe.getDishId() != null) {
      Dish dish = dishRepository.findById(updatedRecipe.getDishId())
          .orElseThrow(() -> new EntityNotFoundException(DISH_NOT_FOUND_MSG));
      recipe.setDish(dish);
    }

    recipeCache.evictById(id);
    recipeCache.clearAllPages();
    recipeCache.clearAllList();

    Recipe saved = recipeRepository.save(recipe);
    return RecipeMapper.toDto(saved);
  }

  @Transactional
  public void deleteRecipe(Long id) {
    Recipe recipe = recipeRepository.findById(id).orElseThrow();

    Dish dish = recipe.getDish();
    if (dish != null) {
      dish.setRecipe(null);
    }

    recipeRepository.delete(recipe);

    recipeCache.evictById(id);
    recipeCache.clearAll();
  }

  @Transactional
  public Recipe addIngredients(Long recipeId, List<RecipeIngredientRequestDto> ingredientsDto) {
    Recipe recipe = recipeRepository.findById(recipeId)
        .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

    for (RecipeIngredientRequestDto dto : ingredientsDto) {
      saveIngredient(recipe, dto.getProductId(), dto.getQuantity());
    }

    recipeCache.evictById(recipeId);
    recipeCache.clearAll();

    return recipe;
  }

  private void saveIngredient(Recipe recipe, Long productId, Double quantity) {
    Product product = productRepository.findById(productId).orElseThrow(
        () -> new EntityNotFoundException("Product with id " + productId + " not found"));

    Ingredient ingredient = new Ingredient();
    ingredient.setRecipe(recipe);
    ingredient.setProduct(product);
    ingredient.setQuantity(quantity);

    ingredientRepository.save(ingredient);
  }
}
