package com.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO недостающих ингредиентов")
public class IngredientMissingDto {

  @Schema(description = "ID ингредиента",
      example = "1")
  private Long ingredientId;

  @Schema(description = "Название продукта",
      example = "Молоко")
  private String productName;

  @Schema(description = "Требуемое количество",
      example = "10.0")
  private Double required;

  @Schema(description = "Доступное количество",
      example = "6.0")
  private Double available;

  @Schema(description = "Недостающее количество",
      example = "4.0")
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
