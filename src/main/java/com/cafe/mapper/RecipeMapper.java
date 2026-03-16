package com.cafe.mapper;

import com.cafe.dto.IngredientDto;
import com.cafe.dto.RecipeDto;
import com.cafe.entity.Dish;
import com.cafe.entity.Recipe;
import java.util.List;

public class RecipeMapper {
  private RecipeMapper() {
  }

  public static RecipeDto toDto(Recipe recipe) {

    Long recipeId = null;

    if (recipe.getDish() != null) {
      recipeId = recipe.getDish().getId();
    }

    RecipeDto dto = new RecipeDto(
        recipe.getId(),
        recipe.getName(),
        recipe.getInstructions(),
        recipeId
    );

    List<IngredientDto> ingredients = recipe.getIngredients()
        .stream()
        .map(IngredientMapper::toDto)
        .toList();

    dto.setIngredients(ingredients);

    return dto;
  }

  public static Recipe toEntity(RecipeDto dto, Dish dish) {

    Recipe recipe = new Recipe();

    recipe.setId(dto.getId());
    recipe.setName(dto.getName());
    recipe.setInstructions(dto.getInstructions());
    recipe.setDish(dish);

    return recipe;
  }
}
