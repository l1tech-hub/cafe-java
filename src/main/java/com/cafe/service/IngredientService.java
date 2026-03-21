package com.cafe.service;

import com.cafe.dto.IngredientDto;
import com.cafe.dto.IngredientMissingDto;
import com.cafe.entity.Ingredient;
import com.cafe.entity.Product;
import com.cafe.entity.Recipe;
import com.cafe.mapper.IngredientMapper;
import com.cafe.repository.IngredientRepository;
import com.cafe.repository.ProductRepository;
import com.cafe.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional
  public Ingredient updateIngredient(Long id, Ingredient updatedIngredient) {

    Ingredient ingredient = repository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("ingredient not found"));

    ingredient.setQuantity(updatedIngredient.getQuantity());
    ingredient.setRecipe(updatedIngredient.getRecipe());
    ingredient.setProduct(updatedIngredient.getProduct());

    return repository.save(ingredient);
  }

  public void delete(Long id) {
    repository.deleteById(id);
  }

  public List<IngredientMissingDto> getMissing(Long recipeId, Double iterations, LocalDate date) {
    return repository.findMissingIngredients(recipeId, iterations, date);
  }

  public List<IngredientMissingDto> getMissing2(Long recipeId, Double iterations, LocalDate date) {
    return repository.findMissingIngredients2(recipeId, iterations, date).stream()
        .map(p -> new IngredientMissingDto(
            p.getIngredientId(),
            p.getProductName(),
            p.getRequired(),
            p.getAvailable(),
            p.getMissing()
        ))
        .toList();
  }
}