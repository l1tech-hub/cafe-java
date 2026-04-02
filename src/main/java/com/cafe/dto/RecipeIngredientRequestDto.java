package com.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO запроса ингредиента для рецепта")
public class RecipeIngredientRequestDto {

  @Schema(description = "ID продукта",
      example = "10",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Long productId;

  @Schema(description = "Количество",
      example = "2.5",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Double quantity;

  public Long getProductId() {
    return productId;
  }

  public Double getQuantity() {
    return quantity;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }
}
