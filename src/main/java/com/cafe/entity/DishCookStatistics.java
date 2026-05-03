package com.cafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "dish_cook_statistics",
    uniqueConstraints = @UniqueConstraint(columnNames = "dish_id")
)
public class DishCookStatistics {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "dish_id", nullable = false)
  private Long dishId;

  @Column(name = "cook_count", nullable = false)
  private int cookCount;

  public DishCookStatistics() {
  }

  public DishCookStatistics(Long dishId, int cookCount) {
    this.dishId = dishId;
    this.cookCount = cookCount;
  }

  public Long getId() {
    return id;
  }

  public Long getDishId() {
    return dishId;
  }

  public void setDishId(Long dishId) {
    this.dishId = dishId;
  }

  public int getCookCount() {
    return cookCount;
  }

  public void setCookCount(int cookCount) {
    this.cookCount = cookCount;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
