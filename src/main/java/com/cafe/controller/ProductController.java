package com.cafe.controller;

import com.cafe.dto.ProductDto;
import com.cafe.entity.Product;
import com.cafe.service.ProductService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST для продуктов.
 * Даёт endpoints для CRUD.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @PostMapping
  public Product add(@RequestBody ProductDto dto) {
    return service.add(dto.getName(), dto.getPrice());
  }

  @GetMapping(value = "/{id}")
  public ProductDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @GetMapping
  public List<ProductDto> getAll() {
    return service.getAll();
  }

  @GetMapping("/search")
  public List<Product> searchByName(@RequestParam String name) {
    return service.findByName(name);
  }
}