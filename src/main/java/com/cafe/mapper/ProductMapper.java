package com.cafe.mapper;

import com.cafe.dto.ProductDto;
import com.cafe.entity.Product;

public class ProductMapper {

  private ProductMapper() {
  }

  public static ProductDto toDto(Product product) {
    return new ProductDto(
        product.getId(),
        product.getName()
    );
  }

  public static Product toEntity(ProductDto dto) {
    Product product = new Product();
    product.setName(dto.getName());
    return product;
  }
}