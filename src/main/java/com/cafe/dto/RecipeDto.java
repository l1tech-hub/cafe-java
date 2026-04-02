package com.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "DTO рецепта")
public class RecipeDto {

  @Schema(description = "ID рецепта",
      example = "1")
  private Long id;

  @Schema(description = "Название рецепта",
      example = "Паста Карбонара",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "Инструкции приготовления",
      example = "Смешать ингредиенты и готовить 10 минут",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String instructions;

  @Schema(description = "ID блюда",
      example = "5",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Long dishId;

  @Schema(description = "Список ингредиентов рецепта")
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