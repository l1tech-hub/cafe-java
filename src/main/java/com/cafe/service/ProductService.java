package com.cafe.service;

import com.cafe.dto.ProductDto;
import com.cafe.entity.Product;
import com.cafe.mapper.ProductMapper;
import com.cafe.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Сервис для операций с продуктами.
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

  public List<Product> findByName(String name) {
    return repository.findByName(name);
  }
}

