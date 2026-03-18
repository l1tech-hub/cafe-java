package com.cafe.dto;

public class DishDto {

  private Long id;
  private String name;
  private Double price;
  private Double weight;
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