package com.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO ингредиента")
public class IngredientDto {

  @Schema(description = "ID ингредиента",
      example = "1")
  private Long id;

  @Schema(description = "ID рецепта",
      example = "5",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Long recipeId;

  @Schema(description = "ID продукта",
      example = "10",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Long productId;

  @Schema(description = "Количество",
      example = "2.5",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Double quantity;

  public IngredientDto() {
  }

  public IngredientDto(Long id, Long recipeId, Long productId, Double quantity) {
    this.id = id;
    this.recipeId = recipeId;
    this.productId = productId;
    this.quantity = quantity;
  }

  public Long getId() {
    return id;
  }

  public Long getRecipeId() {
    return recipeId;
  }

  public Long getProductId() {
    return productId;
  }

  public Double getQuantity() {
    return quantity;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setRecipeId(Long recipeId) {
    this.recipeId = recipeId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }
}

