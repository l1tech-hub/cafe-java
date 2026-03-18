package com.cafe.mapper;

import com.cafe.dto.DishDto;
import com.cafe.entity.Dish;
import com.cafe.entity.Recipe;

public class DishMapper {
  private DishMapper() {
    //
  }

  public static DishDto toDto(Dish dish) {

    Long recipeId = null;

    if (dish.getRecipe() != null) {
      recipeId = dish.getRecipe().getId();
    }

    return new DishDto(
        dish.getId(),
        dish.getName(),
        dish.getPrice(),
        dish.getWeight(),
        recipeId
    );
  }

  public static Dish toEntity(DishDto dto, Recipe recipe) {

    Dish dish = new Dish();

    dish.setId(dto.getId());
    dish.setName(dto.getName());
    dish.setPrice(dto.getPrice());
    dish.setWeight(dto.getWeight());
    dish.setRecipe(recipe);

    return dish;
  }
}