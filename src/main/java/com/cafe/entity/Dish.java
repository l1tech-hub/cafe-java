package com.cafe.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Dish {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private Double price;

  @OneToOne(mappedBy = "dish", cascade = CascadeType.ALL)
  private Recipe recipe;

  public Dish() {
    //
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

  public Recipe getRecipe() {
    return recipe;
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

  public void setRecipe(Recipe recipe) {
    this.recipe = recipe;
    if (recipe != null) {
      recipe.setDish(this); // синхронизируем владельца
    }
  }
}