package com.cafe.dto;

import java.util.ArrayList;
import java.util.List;

public class RecipeDto {

  private Long id;
  private String name;
  private String instructions;
  private Long dishId;

  private List<IngredientDto> ingredients = new ArrayList<>();


  public RecipeDto() {}

  public RecipeDto(Long id, String name, String instructions, Long dishId) {
    this.id = id;
    this.name = name;
    this.instructions = instructions;
    this.dishId = dishId;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getInstructions() {
    return instructions;
  }

  public List<IngredientDto> getIngredients() {
    return ingredients;
  }

  public Long getDishId() {
    return dishId;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setInstructions(String instructions) {
    this.instructions = instructions;
  }

  public void setIngredients(List<IngredientDto> ingredients) {
    this.ingredients = ingredients;
  }

  public void setDishId(Long dishId) {
    this.dishId = dishId;
  }
}