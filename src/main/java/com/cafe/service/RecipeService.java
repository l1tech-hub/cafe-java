package com.cafe.service;

import com.cafe.dto.CreateRecipeDto;
import com.cafe.dto.IngredientDto;
import com.cafe.dto.RecipeDto;
import com.cafe.dto.RecipeIngredientRequestDto;
import com.cafe.entity.Dish;
import com.cafe.entity.Ingredient;
import com.cafe.entity.Product;
import com.cafe.entity.Recipe;
import com.cafe.exception.InvalidDataException;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.mapper.RecipeMapper;
import com.cafe.repository.DishRepository;
import com.cafe.repository.IngredientRepository;
import com.cafe.repository.ProductRepository;
import com.cafe.repository.RecipeRepository;
import com.cafe.service.cache.RecipeCache;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeService {

  private static final String RECIPE_MSG = "Recipe";

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

    Dish dish = null;
    if (dto.getDishId() != null) {
      dish = dishRepository.findById(dto.getDishId())
          .orElseThrow(() -> new ResourceNotFoundException("Dish", "id", dto.getDishId()));
    }

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
          .orElseThrow(() -> new ResourceNotFoundException("Dish", "id", request.getDishId()));
      recipe.setDish(dish);
    }

    recipeRepository.save(recipe);

    if (request.getIngredients() != null) {
      for (IngredientDto dto : request.getIngredients()) {
        saveIngredient(recipe, dto.getProductId(), dto.getQuantity());
      }
    }

    recipeCache.clearAll();

    return recipe;
  }

  public List<RecipeDto> getAll() {
    List<RecipeDto> cached = recipeCache.getAll();
    if (cached != null) {
      return cached;
    }

    List<RecipeDto> result = recipeRepository.findAll()
        .stream()
        .map(RecipeMapper::toDto)
        .toList();

    recipeCache.putAll(result);
    return result;
  }

  public Page<RecipeDto> getAllPaged(Pageable pageable, Long dishId) {
    Page<RecipeDto> cached = recipeCache.getPage(pageable, dishId);
    if (cached != null) {
      return cached;
    }

    Page<RecipeDto> page = recipeRepository.findAllPageable(pageable, dishId)
        .map(RecipeMapper::toDto);

    recipeCache.putPage(pageable, page, dishId);
    return page;
  }

  public RecipeDto getById(Long id) {
    RecipeDto cached = recipeCache.getById(id);
    if (cached != null) {
      return cached;
    }

    Recipe recipe = recipeRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(RECIPE_MSG, "id", id));

    RecipeDto dto = RecipeMapper.toDto(recipe);
    recipeCache.putById(id, dto);

    return dto;
  }

  @Transactional
  public RecipeDto updateRecipe(Long id, RecipeDto updatedRecipe) {

    Recipe recipe = recipeRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(RECIPE_MSG, "id", id));

    recipe.setName(updatedRecipe.getName());
    recipe.setInstructions(updatedRecipe.getInstructions());

    if (updatedRecipe.getDishId() != null) {
      Dish dish = dishRepository.findById(updatedRecipe.getDishId())
          .orElseThrow(() -> new ResourceNotFoundException("Dish", "id",
              updatedRecipe.getDishId()));
      recipe.setDish(dish);
    } else {
      recipe.setDish(null);
    }

    recipeCache.evictById(id);
    recipeCache.clearAllPages();
    recipeCache.clearAllList();

    Recipe saved = recipeRepository.save(recipe);
    return RecipeMapper.toDto(saved);
  }

  @Transactional
  public void deleteRecipe(Long id) {

    Recipe recipe = recipeRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(RECIPE_MSG, "id", id));

    Dish dish = recipe.getDish();
    if (dish != null) {
      dish.setRecipe(null);
    }

    recipeRepository.delete(recipe);

    recipeCache.evictById(id);
    recipeCache.clearAll();
  }


  private Ingredient toIngredient(Recipe recipe, RecipeIngredientRequestDto dto) {

    if (dto.getProductId() == null) {
      throw new InvalidDataException("Product id must not be null", null, "");
    }

    if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
      throw new InvalidDataException("quantity", dto.getQuantity(), "must be > 0");
    }

    Product product = productRepository.findById(dto.getProductId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Product not found", "id", dto.getProductId()
        ));

    Ingredient ingredient = new Ingredient();
    ingredient.setRecipe(recipe);
    ingredient.setProduct(product);
    ingredient.setQuantity(dto.getQuantity());

    return ingredient;
  }

  @Transactional
  public Recipe addIngredients(Long recipeId, List<RecipeIngredientRequestDto> ingredientsDto) {

    Recipe recipe = recipeRepository.findById(recipeId)
        .orElseThrow(() -> new ResourceNotFoundException(RECIPE_MSG, "id", recipeId));

    if (ingredientsDto != null) {
      ingredientsDto.stream()
          .map(dto -> toIngredient(recipe, dto))
          .forEach(ingredientRepository::save);
    }

    recipeCache.evictById(recipeId);
    recipeCache.clearAll();

    return recipe;
  }

  private void saveIngredient(Recipe recipe, Long productId, Double quantity) {

    if (quantity == null || quantity <= 0) {
      throw new InvalidDataException("quantity", quantity, "must be > 0");
    }

    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

    Ingredient ingredient = new Ingredient();
    ingredient.setRecipe(recipe);
    ingredient.setProduct(product);
    ingredient.setQuantity(quantity);

    ingredientRepository.save(ingredient);
  }
}
