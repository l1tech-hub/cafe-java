package com.cafe.dto;

public class IngredientMissingDto {

  private Long ingredientId;
  private String productName;
  private Double required;
  private Double available;
  private Double missing;

  public IngredientMissingDto(Long ingredientId,
      String productName,
      Double required,
      Double available,
      Double missing) {
    this.ingredientId = ingredientId;
    this.productName = productName;
    this.required = required;
    this.available = available;
    this.missing = missing;
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

  public Double getRequired() {
    return required;
  }

  public void setRequired(Double required) {
    this.required = required;
  }

  public Double getAvailable() {
    return available;
  }

  public void setAvailable(Double available) {
    this.available = available;
  }

  public Double getMissing() {
    return missing;
  }

  public void setMissing(Double missing) {
    this.missing = missing;
  }
}



