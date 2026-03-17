package com.cafe.dto;

import java.util.List;

public class CreateRecipeDto {

  private String name;
  private String instructions;
  private Long dishId;
  private List<IngredientDto> ingredients;

  public CreateRecipeDto() {
    //
  }

  public String getName() {
    return name;
  }

  public String getInstructions() {
    return instructions;
  }

  public Long getDishId() {
    return dishId;
  }

  public List<IngredientDto> getIngredients() {
    return ingredients;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setInstructions(String instructions) {
    this.instructions = instructions;
  }

  public void setDishId(Long dishId) {
    this.dishId = dishId;
  }

  public void setIngredients(List<IngredientDto> ingredients) {
    this.ingredients = ingredients;
  }
}
