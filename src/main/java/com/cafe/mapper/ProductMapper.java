package com.cafe.mapper;

import com.cafe.dto.ProductDto;
import com.cafe.entity.Product;


/**
 * Класс для преобразования Product в ProductDto.
 */
public class ProductMapper {

  private ProductMapper() {}

  /**
   * Метод для преобразования Product в ProductDto.
   */
  public static ProductDto toDto(Product product) {
    return new ProductDto(
        product.getId(),
        product.getName(),
        product.getPrice()
    );
  }
}
