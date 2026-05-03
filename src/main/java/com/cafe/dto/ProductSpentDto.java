package com.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Расход продукта по рецептам и числу приготовлений блюд")
public class ProductSpentDto {

  @Schema(description = "Название продукта")
  private String productName;

  @Schema(description = "Затрачено, кг")
  private double spentKilograms;

  public ProductSpentDto() {
  }

  public ProductSpentDto(String productName, double spentKilograms) {
    this.productName = productName;
    this.spentKilograms = spentKilograms;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public double getSpentKilograms() {
    return spentKilograms;
  }

  public void setSpentKilograms(double spentKilograms) {
    this.spentKilograms = spentKilograms;
  }
}
