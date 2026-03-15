package com.cafe.dto;

import java.util.List;

public class CreateRecipeDto {

  private String instructions;            // инструкции рецепта
  private List<IngredientDto> ingredients; // список ингредиентов

  public CreateRecipeDto() { }

  public CreateRecipeDto(String instructions, List<IngredientDto> ingredients) {
    this.instructions = instructions;
    this.ingredients = ingredients;
  }

  public String getInstructions() {
    return instructions;
  }

  public void setInstructions(String instructions) {
    this.instructions = instructions;
  }

  public List<IngredientDto> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<IngredientDto> ingredients) {
    this.ingredients = ingredients;
  }
}