package com.example.demo.controller;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST контролёр для продуктов.
 * Даёт endpoints для CRUD.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @GetMapping("/add")
  public Product add(
      @RequestParam String name,
      @RequestParam Double price) {
    return service.add(name, price);
  }

  @GetMapping(value = "/{id}")
  public ProductDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @GetMapping
  public List<ProductDto> getAll() {
    return service.getAll();
  }
}