package com.cafe.controller;

import com.cafe.dto.IngredientDto;
import com.cafe.dto.IngredientMissingDto;
import com.cafe.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Ingredient", description = "Операции с ингредиентами")
public class IngredientController {

  private final IngredientService service;

  public IngredientController(IngredientService service) {
    this.service = service;
  }

  @Operation(summary = "Создать ингредиент")
  @PostMapping
  public IngredientDto add(@RequestBody IngredientDto dto) {
    return service.add(dto);
  }

  @Operation(summary = "Получить ингредиенты по ID рецепта")
  @GetMapping("/recipe/{recipeId}")
  public List<IngredientDto> getByRecipe(@PathVariable Long recipeId) {
    return service.getByRecipe(recipeId);
  }

  @Operation(summary = "Получить недостающие ингредиенты")
  @GetMapping("/recipe/{recipeId}/missing")
  public List<IngredientMissingDto> getMissing(
      @Parameter(description = "ID рецепта", example = "1", required = true)
      @PathVariable Long recipeId,

      @Parameter(description = "Количество итераций приготовления",
          example = "2.0", required = true)
      @RequestParam(name = "itr") Double iterations,

      @Parameter(description = "Дата приготовления (формат yyyy-MM-dd)",
          example = "2026-04-01", required = true)
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    return service.getMissing(recipeId, iterations, date);
  }

  @Operation(summary = "Получить недостающие ингредиенты (альтернативный метод)")
  @GetMapping("/recipe/{recipeId}/missing2")
  public List<IngredientMissingDto> getMissing2(
      @Parameter(description = "ID рецепта", example = "1", required = true)
      @PathVariable Long recipeId,

      @Parameter(description = "Количество итераций приготовления",
          example = "2.0", required = true)
      @RequestParam(name = "itr") Double iterations,

      @Parameter(description = "Дата приготовления (формат yyyy-MM-dd)",
          example = "2026-04-01", required = true)
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    return service.getMissing2(recipeId, iterations, date);
  }

  @Operation(summary = "Удалить ингредиент")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}