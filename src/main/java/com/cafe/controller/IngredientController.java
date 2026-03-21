package com.cafe.controller;

import com.cafe.dto.IngredientDto;
import com.cafe.dto.IngredientMissingDto;
import com.cafe.service.IngredientService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ingredients")
public class IngredientController {

  private final IngredientService service;

  public IngredientController(IngredientService service) {
    this.service = service;
  }

  @PostMapping
  public IngredientDto add(@RequestBody IngredientDto dto) {
    return service.add(dto);
  }

  @GetMapping("/recipe/{recipeId}")
  public List<IngredientDto> getByRecipe(@PathVariable Long recipeId) {
    return service.getByRecipe(recipeId);
  }

  @GetMapping("/recipe/{recipeId}/missing")
  public List<IngredientMissingDto> getMissing(
      @PathVariable Long recipeId,
      @RequestParam(name = "itr") Double iterations,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    return service.getMissing(recipeId, iterations, date);
  }

  @GetMapping("/recipe/{recipeId}/missing2")
  public List<IngredientMissingDto> getMissing2(
      @PathVariable Long recipeId,
      @RequestParam(name = "itr") Double iterations,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    return service.getMissing2(recipeId, iterations, date);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
