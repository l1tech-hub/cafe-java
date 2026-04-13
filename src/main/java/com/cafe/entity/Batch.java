package com.cafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "batch")
public class Batch {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Double price;
  private Double quantity;

  private LocalDate manufactureDate;
  private LocalDate expiryDate;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  public Batch() {
  }

  public Batch(Double price, LocalDate manufactureDate, LocalDate expiryDate, Double quantity) {
    this.price = price;
    this.manufactureDate = manufactureDate;
    this.expiryDate = expiryDate;
    this.quantity = quantity;
  }

  public Long getId() {
    return id;
  }

  public Double getPrice() {
    return price;
  }

  public LocalDate getManufactureDate() {
    return manufactureDate;
  }

  public LocalDate getExpiryDate() {
    return expiryDate;
  }

  public Product getProduct() {
    return product;
  }

  public Double getQuantity() {
    return quantity;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public void setManufactureDate(LocalDate manufactureDate) {
    this.manufactureDate = manufactureDate;
  }

  public void setExpiryDate(LocalDate expiryDate) {
    this.expiryDate = expiryDate;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
