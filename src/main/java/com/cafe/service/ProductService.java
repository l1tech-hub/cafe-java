package com.cafe.service;

import com.cafe.dto.ProductDto;
import com.cafe.entity.Product;
import com.cafe.mapper.ProductMapper;
import com.cafe.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductRepository repository;

  public ProductService(ProductRepository repository) {
    this.repository = repository;
  }

  public Product add(String name, boolean state) {

    Product product = new Product();
    product.setName(name);
    product.setState(state);

    return repository.save(product);
  }

  public ProductDto getById(Long id) {

    Product product = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Product not found"));

    return ProductMapper.toDto(product);
  }

  public List<ProductDto> getAll() {

    return repository.findAll()
        .stream()
        .map(ProductMapper::toDto)
        .toList();
  }

  public List<Product> findByName(String name) {
    return repository.findByNameContainingIgnoreCase(name);
  }

  public ProductDto update(Long id, ProductDto dto) {

    Product product = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Product not found"));

    product.setName(dto.getName());
    product.setState(dto.getState());

    Product updated = repository.save(product);

    return ProductMapper.toDto(updated);
  }

  public void delete(Long id) {

    if (!repository.existsById(id)) {
      throw new EntityNotFoundException("Product not found");
    }

    repository.deleteById(id);
  }
}