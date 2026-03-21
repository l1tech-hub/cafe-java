package com.cafe.controller;

import com.cafe.dto.CreateRecipeDto;
import com.cafe.dto.RecipeDto;
import com.cafe.dto.RecipeIngredientRequestDto;
import com.cafe.entity.Recipe;
import com.cafe.service.RecipeService;
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
@RequestMapping("/recipes")
public class RecipeController {

  private final RecipeService service;

  public RecipeController(RecipeService service) {
    this.service = service;
  }

  @PostMapping
  public RecipeDto create(@RequestBody RecipeDto recipe) {
    return service.createRecipe(recipe);
  }

  @PostMapping("/withingredients")
  public Recipe createWithIngredients(@RequestBody CreateRecipeDto request) {
    return service.createRecipeWithIngredients(request);
  }

  @PostMapping("/{id}/ingredients")
  public Recipe addIngredients(
      @PathVariable Long id,
      @RequestBody List<RecipeIngredientRequestDto> ingredients
  ) {
    return service.addIngredients(id, ingredients);
  }

  @GetMapping
  public List<RecipeDto> getAll() {
    return service.getAll();
  }

  @GetMapping("/paged")
  public Page<RecipeDto> getAllPaged(
      @PageableDefault(size = 5, sort = "id") Pageable pageable
  ) {
    return service.getAllPaged(pageable);
  }

  @GetMapping("/{id}")
  public RecipeDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @PutMapping("/{id}")
  public RecipeDto update(@PathVariable Long id, @RequestBody RecipeDto recipe) {
    return service.updateRecipe(id, recipe);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.deleteRecipe(id);
  }
}
