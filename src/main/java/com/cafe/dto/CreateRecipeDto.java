package com.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "DTO для создания рецепта с ингредиентами")
public class CreateRecipeDto {

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

  @Schema(description = "Список ингредиентов")
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
