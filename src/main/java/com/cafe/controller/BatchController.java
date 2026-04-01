package com.cafe.controller;

import com.cafe.dto.BatchDto;
import com.cafe.service.BatchService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
  public BatchDto create(@RequestBody @Valid BatchDto dto) {
    return service.create(dto);
  }

  @GetMapping("/{id}")
  public BatchDto getById(@PathVariable @Valid Long id) {
    return service.getById(id);
  }

  @GetMapping
  public List<BatchDto> getAll() {
    return service.getAll();
  }

  @GetMapping("/product/{productId}")
  public List<BatchDto> getByProduct(@PathVariable @Valid Long productId) {
    return service.getByProduct(productId);
  }

  @GetMapping("/paged")
  public Page<BatchDto> getAllPaged(
      @PageableDefault(size = 5, sort = "id") @Valid Pageable pageable
  ) {
    return service.getAllPaged(pageable);
  }

  @GetMapping("/product/{productId}/paged")
  public Page<BatchDto> getByProductPaged(
      @PathVariable Long productId,
      @PageableDefault(size = 5, sort = "id") Pageable pageable
  ) {
    return service.getByProductPaged(productId, pageable);
  }

  @PutMapping("/{id}")
  public BatchDto update(@PathVariable @Valid Long id, @RequestBody @Valid BatchDto dto) {
    return service.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable @Valid Long id) {
    service.delete(id);
  }
}