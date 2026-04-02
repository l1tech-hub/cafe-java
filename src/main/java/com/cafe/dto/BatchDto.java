package com.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "DTO партии продукта")
public class BatchDto {

  @Schema(description = "ID партии",
      example = "1")
  private Long id;

  @Schema(description = "ID продукта",
      example = "10",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Long productId;

  @Schema(description = "Цена за единицу",
      example = "100.5",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Double price;

  @Schema(description = "Количество",
      example = "50.0",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Double quantity;

  @Schema(description = "Дата производства",
      example = "2026-04-01",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalDate manufactureDate;

  @Schema(description = "Срок годности",
      example = "2026-05-01",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalDate expiryDate;

  public BatchDto() {
  }

  public BatchDto(Long id, Long productId, Double price, Double quantity,
      LocalDate manufactureDate, LocalDate expiryDate) {
    this.id = id;
    this.productId = productId;
    this.price = price;
    this.quantity = quantity;
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

  public Double getQuantity() {
    return quantity;
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

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }
}