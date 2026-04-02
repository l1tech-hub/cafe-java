package com.cafe.controller;

import com.cafe.dto.BatchDto;
import com.cafe.service.BatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batches")
@Tag(name = "Batch", description = "Операции с партиями продуктов")
public class BatchController {

  private final BatchService service;

  public BatchController(BatchService service) {
    this.service = service;
  }

  @Operation(summary = "Создать партию продукта")
  @PostMapping
  public BatchDto create(@RequestBody BatchDto dto) {
    return service.create(dto);
  }

  @Operation(summary = "Получить партию по ID")
  @GetMapping("/{id}")
  public BatchDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @Operation(summary = "Получить все партии")
  @GetMapping
  public List<BatchDto> getAll() {
    return service.getAll();
  }

  @Operation(summary = "Получить партии по ID продукта")
  @GetMapping("/product/{productId}")
  public List<BatchDto> getByProduct(@PathVariable Long productId) {
    return service.getByProduct(productId);
  }

  @Operation(summary = "Получить все партии с пагинацией")
  @GetMapping("/paged")
  public Page<BatchDto> getAllPaged(
      @PageableDefault(size = 5, sort = "id") Pageable pageable
  ) {
    return service.getAllPaged(pageable);
  }

  @Operation(summary = "Получить партии продукта с пагинацией")
  @GetMapping("/product/{productId}/paged")
  public Page<BatchDto> getByProductPaged(
      @PathVariable Long productId,
      @PageableDefault(size = 5, sort = "id") Pageable pageable
  ) {
    return service.getByProductPaged(productId, pageable);
  }

  @Operation(summary = "Обновить партию")
  @PutMapping("/{id}")
  public BatchDto update(@PathVariable Long id, @RequestBody BatchDto dto) {
    return service.update(id, dto);
  }
}