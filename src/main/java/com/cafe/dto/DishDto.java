package com.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO блюда")
public class DishDto {

  @Schema(description = "ID блюда",
      example = "1")
  private Long id;

  @Schema(description = "Название блюда",
      example = "Паста Карбонара",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "Цена блюда",
      example = "250.0",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Double price;

  @Schema(description = "Вес блюда (в граммах)",
      example = "300.0",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Double weight;

  @Schema(description = "ID рецепта",
      example = "5",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Long recipeId;

  public DishDto() {
  }

  public DishDto(Long id, String name, Double price, Double weight, Long recipeId) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.weight = weight;
    this.recipeId = recipeId;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Double getPrice() {
    return price;
  }

  public Long getRecipeId() {
    return recipeId;
  }

  public Double getWeight() {
    return weight;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public void setRecipeId(Long recipeId) {
    this.recipeId = recipeId;
  }

  public void setWeight(Double weight) {
    this.weight = weight;
  }
}
