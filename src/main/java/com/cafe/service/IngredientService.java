package com.cafe.service;

import com.cafe.dto.IngredientDto;
import com.cafe.entity.Ingredient;
import com.cafe.entity.Product;
import com.cafe.mapper.IngredientMapper;
import com.cafe.repository.IngredientRepository;
import com.cafe.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class IngredientService {

  private final IngredientRepository repository;
  private final ProductRepository productRepository;
  private final RecipeRepository recipeRepository;

  public IngredientService(IngredientRepository repository,
      ProductRepository productRepository,
      RecipeRepository recipeRepository) {
    this.repository = repository;
    this.productRepository = productRepository;
    this.recipeRepository = recipeRepository;
  }

  public IngredientDto add(IngredientDto dto) {

    Product product = productRepository.findById(dto.getProductId())
        .orElseThrow(() -> new RuntimeException("Product not found"));

    Recipe recipe = recipeRepository.findById(dto.getRecipeId())
        .orElseThrow(() -> new RuntimeException("Recipe not found"));

    Ingredient ingredient = IngredientMapper.toEntity(dto, product, recipe);

    return IngredientMapper.toDto(repository.save(ingredient));
  }

  public List<IngredientDto> getByRecipe(Long recipeId) {

    return repository.findByRecipeId(recipeId)
        .stream()
        .map(IngredientMapper::toDto)
        .toList();
  }

  public void delete(Long id) {
    repository.deleteById(id);
  }
}