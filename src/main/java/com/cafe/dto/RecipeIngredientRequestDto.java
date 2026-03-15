package com.cafe.dto;

public class RecipeIngredientRequestDto {

  private Long productId;
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
