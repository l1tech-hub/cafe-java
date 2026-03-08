package com.cafe.dto;

public class IngredientDto {

  private Long id;
  private Long recipeId;
  private Long productId;
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