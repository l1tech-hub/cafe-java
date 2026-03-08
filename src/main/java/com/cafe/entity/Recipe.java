package com.cafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe")
public class Recipe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "product_id")
  private Product product;

  public Recipe() {}

  public Long getId() {
    return id;
  }

  public Product getProduct() {
    return product;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setProduct(Product product) {
    this.product = product;
  }
}
