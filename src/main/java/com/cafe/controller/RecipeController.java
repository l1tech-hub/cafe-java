package com.cafe.controller;

import com.cafe.dto.CreateRecipeDto;
import com.cafe.dto.RecipeDto;
import com.cafe.dto.RecipeIngredientRequestDto;
import com.cafe.entity.Recipe;
import com.cafe.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipes")
@Tag(name = "Recipe", description = "Операции с рецептами")
public class RecipeController {

  private final RecipeService service;

  public RecipeController(RecipeService service) {
    this.service = service;
  }

  @Operation(summary = "Создать рецепт")
  @PostMapping
  public RecipeDto create(@RequestBody RecipeDto recipe) {
    return service.createRecipe(recipe);
  }

  @Operation(summary = "Создать рецепт с ингредиентами")
  @PostMapping("/withingredients")
  public Recipe createWithIngredients(@RequestBody CreateRecipeDto request) {
    return service.createRecipeWithIngredients(request);
  }

  @Operation(summary = "Добавить ингредиенты к рецепту")
  @PostMapping("/{id}/ingredients")
  public Recipe addIngredients(
      @PathVariable Long id,
      @RequestBody List<RecipeIngredientRequestDto> ingredients
  ) {
    return service.addIngredients(id, ingredients);
  }

  @Operation(summary = "Получить все рецепты")
  @GetMapping
  public List<RecipeDto> getAll() {
    return service.getAll();
  }

  @Operation(summary = "Получить все рецепты с пагинацией")
  @GetMapping("/paged")
  public Page<RecipeDto> getAllPaged(
      @RequestParam(required = false) Long dishId,
      @PageableDefault(size = 5, sort = "id") Pageable pageable
  ) {
    return service.getAllPaged(pageable, dishId);
  }

  @Operation(summary = "Получить рецепт по ID")
  @GetMapping("/{id}")
  public RecipeDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @Operation(summary = "Обновить рецепт")
  @PutMapping("/{id}")
  public RecipeDto update(@PathVariable Long id, @RequestBody RecipeDto recipe) {
    return service.updateRecipe(id, recipe);
  }

  @Operation(summary = "Удалить рецепт")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.deleteRecipe(id);
  }
}
