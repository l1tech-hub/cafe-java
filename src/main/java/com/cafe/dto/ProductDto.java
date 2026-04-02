package com.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO продукта")
public class ProductDto {

  @Schema(description = "ID продукта",
      example = "1")
  private Long id;

  @Schema(description = "Название продукта",
      example = "Молоко",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  public ProductDto() {
  }

  public ProductDto(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

}