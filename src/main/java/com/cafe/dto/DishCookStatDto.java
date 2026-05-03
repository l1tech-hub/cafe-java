package com.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Сколько раз приготовили блюдо")
public class DishCookStatDto {

  @Schema(description = "ID блюда")
  private Long dishId;

  @Schema(description = "Название блюда")
  private String dishName;

  @Schema(description = "Число успешных приготовлений")
  private int cookCount;

  @Schema(description = "Цена одной порции блюда, руб.")
  private Double dishPrice;

  @Schema(description = "Вес одной порции блюда, г")
  private Double dishWeightGrams;

  @Schema(description = "Стоимость всех приготовленных порций (порций × цена), руб.")
  private Double totalIterationsPrice;

  @Schema(description = "Масса всех приготовленных порций (порций × вес порции), кг")
  private Double totalPortionsMassKilograms;

  public DishCookStatDto() {
  }

  public DishCookStatDto(Long dishId, String dishName, int cookCount,
      Double dishPrice, Double dishWeightGrams, Double totalIterationsPrice,
      Double totalPortionsMassKilograms) {
    this.dishId = dishId;
    this.dishName = dishName;
    this.cookCount = cookCount;
    this.dishPrice = dishPrice;
    this.dishWeightGrams = dishWeightGrams;
    this.totalIterationsPrice = totalIterationsPrice;
    this.totalPortionsMassKilograms = totalPortionsMassKilograms;
  }

  public Long getDishId() {
    return dishId;
  }

  public void setDishId(Long dishId) {
    this.dishId = dishId;
  }

  public String getDishName() {
    return dishName;
  }

  public void setDishName(String dishName) {
    this.dishName = dishName;
  }

  public int getCookCount() {
    return cookCount;
  }

  public void setCookCount(int cookCount) {
    this.cookCount = cookCount;
  }

  public Double getDishPrice() {
    return dishPrice;
  }

  public void setDishPrice(Double dishPrice) {
    this.dishPrice = dishPrice;
  }

  public Double getDishWeightGrams() {
    return dishWeightGrams;
  }

  public void setDishWeightGrams(Double dishWeightGrams) {
    this.dishWeightGrams = dishWeightGrams;
  }

  public Double getTotalIterationsPrice() {
    return totalIterationsPrice;
  }

  public void setTotalIterationsPrice(Double totalIterationsPrice) {
    this.totalIterationsPrice = totalIterationsPrice;
  }

  public Double getTotalPortionsMassKilograms() {
    return totalPortionsMassKilograms;
  }

  public void setTotalPortionsMassKilograms(Double totalPortionsMassKilograms) {
    this.totalPortionsMassKilograms = totalPortionsMassKilograms;
  }
}
