package com.cafe.service;

import com.cafe.dto.ProductDto;
import com.cafe.entity.Product;
import com.cafe.exception.InvalidDataException;
import com.cafe.exception.ResourceInUseException;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.mapper.ProductMapper;
import com.cafe.repository.IngredientRepository;
import com.cafe.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private static final String PRODUCT_MSG = "Product";

  private final ProductRepository repository;
  private final IngredientRepository ingredientRepository;

  public ProductService(ProductRepository repository, IngredientRepository ingredientRepository) {
    this.repository = repository;
    this.ingredientRepository = ingredientRepository;
  }

  public Product add(String name) {

    if (name != null && name.isBlank()) {
      throw new InvalidDataException("name", name, "must not be blank");
    }

    Product product = new Product();
    product.setName(name);

    return repository.save(product);
  }

  public ProductDto getById(Long id) {

    Product product = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_MSG, "id", id));

    return ProductMapper.toDto(product);
  }

  public List<ProductDto> getAll() {

    return repository.findAll()
        .stream()
        .map(ProductMapper::toDto)
        .toList();
  }

  public List<ProductDto> findByName(String name) {

    if (name != null && name.isBlank()) {
      throw new InvalidDataException("name", name, "must not be blank");
    }

    return repository.findByNameContainingIgnoreCase(name)
        .stream()
        .map(ProductMapper::toDto)
        .toList();
  }

  public ProductDto update(Long id, ProductDto dto) {

    Product product = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_MSG, "id", id));

    if (dto.getName() != null && dto.getName().isBlank()) {
      throw new InvalidDataException("name", dto.getName(), "must not be blank");
    }

    if (dto.getName() != null) {
      product.setName(dto.getName());
    }

    Product updated = repository.save(product);

    return ProductMapper.toDto(updated);
  }

  public void delete(Long id) {

    Product product = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_MSG, "id", id));

    if (ingredientRepository.existsByProductId(id)) {
      throw new ResourceInUseException(PRODUCT_MSG, "delete", "it is used in ingredients");
    }

    repository.delete(product);
  }
}
