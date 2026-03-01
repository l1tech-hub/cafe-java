package com.example.demo.service;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service layer for managing products.
 * Contains business logic related to product operations.
 */
@Service
public class ProductService {

  private final ProductRepository repository;

  public ProductService(ProductRepository repository) {
    this.repository = repository;
  }

  public Product add(String name, Double price) {
    Product product = new Product(name, price);
    return repository.save(product);
  }

  public ProductDto getById(Long id) {
    Product product = repository.findById(id).orElse(null);
    return product != null ? ProductMapper.toDto(product) : null;
  }

  /**
   * Возвращает все продукты.
   */
  public List<ProductDto> getAll() {
    return repository.findAll()
        .stream()
        .map(ProductMapper::toDto)
        .toList();
  }
}

