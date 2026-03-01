package com.example.demo.mapper;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;


/**
 * Класс для преобразования Product в ProductDto.
 */
public class ProductMapper {

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
