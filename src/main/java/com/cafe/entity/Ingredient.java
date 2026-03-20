package com.cafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Ingredient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Double quantity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipe_id")
  private Recipe recipe;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;


  public Ingredient() {
    //
  }

  public Long getId() {
    return id;
  }

  public Double getQuantity() {
    return quantity;
  }

  public Recipe getRecipe() {
    return recipe;
  }

  public Product getProduct() {
    return product;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }

  public void setRecipe(Recipe recipe) {
    this.recipe = recipe;
  }

  public void setProduct(Product product) {
    this.product = product;
  }
}
