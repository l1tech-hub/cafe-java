package com.cafe.controller;

import com.cafe.dto.BatchDto;
import com.cafe.service.BatchService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batches")
public class BatchController {

  private final BatchService service;

  public BatchController(BatchService service) {
    this.service = service;
  }

  @PostMapping
  public BatchDto create(@RequestBody BatchDto dto) {
    return service.create(dto);
  }

  @GetMapping("/{id}")
  public BatchDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @GetMapping
  public List<BatchDto> getAll() {
    return service.getAll();
  }

  @GetMapping("/product/{productId}")
  public List<BatchDto> getByProduct(@PathVariable Long productId) {
    return service.getByProduct(productId);
  }

  @PutMapping("/{id}")
  public BatchDto update(@PathVariable Long id, @RequestBody BatchDto dto) {
    return service.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}