package com.cafe.dto;

import java.time.LocalDate;

public class BatchDto {

  private Long id;
  private Long productId;
  private Double price;
  private LocalDate manufactureDate;
  private LocalDate expiryDate;

  public BatchDto() {
  }

  public BatchDto(Long id, Long productId, Double price,
      LocalDate manufactureDate, LocalDate expiryDate) {
    this.id = id;
    this.productId = productId;
    this.price = price;
    this.manufactureDate = manufactureDate;
    this.expiryDate = expiryDate;
  }

  public Long getId() {
    return id;
  }

  public Long getProductId() {
    return productId;
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

  public void setId(Long id) {
    this.id = id;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
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
}