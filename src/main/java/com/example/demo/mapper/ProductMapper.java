package com.example.demo.mapper;

import com.example.demo.entity.Product;
import com.example.demo.dto.ProductDto;

public class ProductMapper {

    public static ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }
}
