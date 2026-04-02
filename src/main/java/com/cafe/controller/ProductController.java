package com.cafe.controller;

import com.cafe.dto.ProductDto;
import com.cafe.entity.Product;
import com.cafe.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@Tag(name = "Product", description = "Операции с продуктами")
public class ProductController {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @Operation(summary = "Создать продукт")
  @PostMapping
  public Product add(@RequestBody ProductDto dto) {
    return service.add(dto.getName());
  }

  @Operation(summary = "Получить продукт по ID")
  @GetMapping("/{id}")
  public ProductDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @Operation(summary = "Получить все продукты")
  @GetMapping
  public List<ProductDto> getAll() {
    return service.getAll();
  }

  @Operation(summary = "Поиск продуктов по названию")
  @GetMapping("/search")
  public List<Product> searchByName(@RequestParam String name) {
    return service.findByName(name);
  }

  @Operation(summary = "Обновить продукт")
  @PutMapping("/{id}")
  public ProductDto update(@PathVariable Long id, @RequestBody ProductDto dto) {
    return service.update(id, dto);
  }

  @Operation(summary = "Удалить продукт")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
