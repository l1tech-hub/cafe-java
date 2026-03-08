package com.cafe.mapper;

import com.cafe.dto.IngredientDto;
import com.cafe.entity.Ingredient;
import com.cafe.entity.Product;
import com.cafe.entity.Recipe;

public class IngredientMapper {
  private IngredientMapper() {
  }

  public static IngredientDto toDto(Ingredient ingredient) {

    return new IngredientDto(
        ingredient.getId(),
        ingredient.getRecipe().getId(),
        ingredient.getProduct().getId(),
        ingredient.getQuantity()
    );
  }

  public static Ingredient toEntity(IngredientDto dto,
      Product product,
      Recipe recipe) {

    Ingredient ingredient = new Ingredient();

    ingredient.setProduct(product);
    ingredient.setRecipe(recipe);
    ingredient.setQuantity(dto.getQuantity());

    return ingredient;
  }
}