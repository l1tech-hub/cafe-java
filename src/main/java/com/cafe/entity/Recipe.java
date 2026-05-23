package com.cafe.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Recipe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(columnDefinition = "TEXT")
  private String instructions;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Ingredient> ingredients = new ArrayList<>();

  @OneToOne
  @JoinColumn(name = "dish_id")
  private Dish dish;

  public Recipe() {
    //
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getInstructions() {
    return instructions;
  }

  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public Dish getDish() {
    return dish;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setInstructions(String cookingMethod) {
    this.instructions = cookingMethod;
  }

  public void setIngredients(List<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }

  public void setDish(Dish dish) {
    this.dish = dish;
  }
}
