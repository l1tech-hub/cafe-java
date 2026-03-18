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
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeService {

  private final RecipeRepository recipeRepository;
  private final IngredientRepository ingredientRepository;
  private final ProductRepository productRepository;
  private final DishRepository dishRepository;

  public RecipeService(
      RecipeRepository recipeRepository,
      IngredientRepository ingredientRepository,
      ProductRepository productRepository,
      DishRepository dishRepository
  ) {
    this.recipeRepository = recipeRepository;
    this.ingredientRepository = ingredientRepository;
    this.productRepository = productRepository;
    this.dishRepository = dishRepository;
  }

  @Transactional
  public Recipe createRecipe(Recipe recipe) {
    return recipeRepository.save(recipe);
  }

  @Transactional
  public Recipe createRecipeWithIngredients(CreateRecipeDto request) {

    Recipe recipe = new Recipe();
    recipe.setName(request.getName());
    recipe.setInstructions(request.getInstructions());

    if (request.getDishId() != null) {
      Dish dish = dishRepository.findById(request.getDishId())
          .orElseThrow(() -> new EntityNotFoundException("Dish not found"));
      recipe.setDish(dish);
    }

    recipeRepository.save(recipe);



    for (IngredientDto dto : request.getIngredients()) {
      saveIngredient(recipe, dto.getProductId(), dto.getQuantity());
    }

    return recipe;
  }

  public List<RecipeDto> getAll() {
    return recipeRepository.findAll()
        .stream()
        .map(RecipeMapper::toDto)
        .toList();
  }

  public RecipeDto getById(Long id) {
    return RecipeMapper.toDto(recipeRepository.findById(id).orElseThrow());
  }

  @Transactional
  public Recipe updateRecipe(Long id, Recipe updatedRecipe) {

    Recipe recipe = recipeRepository.findById(id).orElseThrow();

    recipe.setName(updatedRecipe.getName());
    recipe.setInstructions(updatedRecipe.getInstructions());
    recipe.setDish(updatedRecipe.getDish());

    return recipeRepository.save(recipe);
  }

  @Transactional
  public void deleteRecipe(Long id) {

    Recipe recipe = recipeRepository.findById(id).orElseThrow();

    Dish dish = recipe.getDish();
    if (dish != null) {
      dish.setRecipe(null);
    }

    recipeRepository.delete(recipe);
  }

  @Transactional
  public Recipe addIngredients(Long recipeId, List<RecipeIngredientRequestDto> ingredientsDto) {

    Recipe recipe = recipeRepository.findById(recipeId)
        .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

    for (RecipeIngredientRequestDto dto : ingredientsDto) {
      saveIngredient(recipe, dto.getProductId(), dto.getQuantity());
    }

    return recipe;
  }

  private void saveIngredient(Recipe recipe, Long productId, Double quantity) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException(
            "Product with id " + productId + " not found"));

    Ingredient ingredient = new Ingredient();
    ingredient.setRecipe(recipe);
    ingredient.setProduct(product);
    ingredient.setQuantity(quantity);

    ingredientRepository.save(ingredient);
  }
}
