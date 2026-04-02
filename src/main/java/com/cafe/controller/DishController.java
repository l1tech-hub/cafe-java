package com.cafe.controller;

import com.cafe.dto.DishDto;
import com.cafe.service.DishService;
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
public class DishController {

  private final DishService service;

  public DishController(DishService service) {
    this.service = service;
  }

  @PostMapping
  public DishDto create(@RequestBody DishDto dto) {
    return service.create(dto);
  }

  @GetMapping("/{id}")
  public DishDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @GetMapping
  public List<DishDto> getAll() {
    return service.getAll();
  }

  @PutMapping("/{id}")
  public DishDto update(@PathVariable Long id, @RequestBody DishDto dto) {
    return service.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @GetMapping("/search")
  public List<DishDto> search(@RequestParam String name) {
    return service.searchByName(name);
  }
}