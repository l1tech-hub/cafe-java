package com.cafe.controller;

import com.cafe.dto.DishDto;
import com.cafe.service.DishService;
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
@RequestMapping("/dishes")
@Tag(name = "Dish", description = "Операции с блюдами")
public class DishController {

  private final DishService service;

  public DishController(DishService service) {
    this.service = service;
  }

  @Operation(summary = "Создать блюдо")
  @PostMapping
  public DishDto create(@RequestBody DishDto dto) {
    return service.create(dto);
  }

  @Operation(summary = "Получить блюдо по ID")
  @GetMapping("/{id}")
  public DishDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @Operation(summary = "Получить все блюда")
  @GetMapping
  public List<DishDto> getAll() {
    return service.getAll();
  }

  @Operation(summary = "Обновить блюдо")
  @PutMapping("/{id}")
  public DishDto update(@PathVariable Long id, @RequestBody DishDto dto) {
    return service.update(id, dto);
  }

  @Operation(summary = "Удалить блюдо")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @Operation(summary = "Поиск блюд по названию")
  @GetMapping("/search")
  public List<DishDto> search(@RequestParam String name) {
    return service.searchByName(name);
  }
}