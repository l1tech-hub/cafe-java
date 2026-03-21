package com.cafe.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Batch> batches = new ArrayList<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<Ingredient> ingredients;

  public Product() {
  }

  public Product(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<Batch> getBatches() {
    return batches;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setBatches(List<Batch> batches) {
    this.batches = batches;
  }

  public void addBatch(Batch batch) {
    batches.add(batch);
    batch.setProduct(this);
  }
}