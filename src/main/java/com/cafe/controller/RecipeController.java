package com.cafe.controller;

import com.cafe.dto.CreateRecipeDto;
import com.cafe.dto.RecipeDto;
import com.cafe.dto.RecipeIngredientRequestDto;
import com.cafe.entity.Recipe;
import com.cafe.service.RecipeService;
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
@RequestMapping("/recipes")
public class RecipeController {

  private final RecipeService service;

  public RecipeController(RecipeService service) {
    this.service = service;
  }

  @PostMapping
  public Recipe create(@RequestBody Recipe recipe) {
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

  @GetMapping("/{id}")
  public Recipe getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @PutMapping("/{id}")
  public Recipe update(
      @PathVariable Long id,
      @RequestBody Recipe recipe
  ) {
    return service.updateRecipe(id, recipe);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.deleteRecipe(id);
  }
}
