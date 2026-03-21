package com.cafe.controller;

import com.cafe.dto.ProductDto;
import com.cafe.entity.Product;
import com.cafe.service.ProductService;
import java.awt.print.Pageable;
import java.util.List;
import org.springframework.data.web.PageableDefault;
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
public class ProductController {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @PostMapping
  public Product add(@RequestBody ProductDto dto) {
    return service.add(dto.getName());
  }

  @GetMapping("/{id}")
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

  @PutMapping("/{id}")
  public ProductDto update(@PathVariable Long id, @RequestBody ProductDto dto) {
    return service.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}