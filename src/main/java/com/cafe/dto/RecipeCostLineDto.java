package com.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Строка расчёта стоимости ингредиента")
public class RecipeCostLineDto {

  @Schema(description = "ID ингредиента")
  private Long ingredientId;

  @Schema(description = "Название продукта")
  private String productName;

  @Schema(description = "Требуемое количество (на все порции)")
  private Double quantity;

  @Schema(description = "Стоимость по выбранным партиям, руб.")
  private Double cost;

  public RecipeCostLineDto() {
  }

  public RecipeCostLineDto(Long ingredientId, String productName, Double quantity, Double cost) {
    this.ingredientId = ingredientId;
    this.productName = productName;
    this.quantity = quantity;
    this.cost = cost;
  }

  public Long getIngredientId() {
    return ingredientId;
  }

  public void setIngredientId(Long ingredientId) {
    this.ingredientId = ingredientId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Double getQuantity() {
    return quantity;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }

  public Double getCost() {
    return cost;
  }

  public void setCost(Double cost) {
    this.cost = cost;
  }
}
