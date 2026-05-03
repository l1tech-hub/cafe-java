package com.cafe.service;

import com.cafe.dto.IngredientDto;
import com.cafe.dto.IngredientMissingDto;
import com.cafe.entity.Ingredient;
import com.cafe.entity.Product;
import com.cafe.entity.Recipe;
import com.cafe.exception.InvalidDataException;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.mapper.IngredientMapper;
import com.cafe.repository.IngredientRepository;
import com.cafe.repository.ProductRepository;
import com.cafe.repository.RecipeRepository;
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

    validateIngredient(dto);

    Product product = productRepository.findById(dto.getProductId())
        .orElseThrow(() -> new ResourceNotFoundException("Product", "id", dto.getProductId()));

    Recipe recipe = recipeRepository.findById(dto.getRecipeId())
        .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", dto.getRecipeId()));

    Ingredient ingredient = IngredientMapper.toEntity(dto, product, recipe);

    return IngredientMapper.toDto(repository.save(ingredient));
  }

  public List<IngredientDto> getByRecipe(Long recipeId) {

    if (!recipeRepository.existsById(recipeId)) {
      throw new ResourceNotFoundException("Recipe", "id", recipeId);
    }

    return repository.findByRecipeId(recipeId)
        .stream()
        .map(IngredientMapper::toDto)
        .toList();
  }

  @Transactional
  public Ingredient updateIngredient(Long id, Ingredient updatedIngredient) {

    Ingredient ingredient = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));

    if (updatedIngredient.getQuantity() != null && updatedIngredient.getQuantity() <= 0) {
      throw new InvalidDataException("quantity", updatedIngredient.getQuantity(), "must be > 0");
    }

    if (updatedIngredient.getQuantity() != null) {
      ingredient.setQuantity(updatedIngredient.getQuantity());
    }

    if (updatedIngredient.getProduct() != null) {
      Long productId = updatedIngredient.getProduct().getId();
      Product product = productRepository.findById(productId)
          .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
      ingredient.setProduct(product);
    }

    if (updatedIngredient.getRecipe() != null) {
      Long recipeId = updatedIngredient.getRecipe().getId();
      Recipe recipe = recipeRepository.findById(recipeId)
          .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", recipeId));
      ingredient.setRecipe(recipe);
    }

    return repository.save(ingredient);
  }

  public void delete(Long id) {

    Ingredient ingredient = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));

    repository.delete(ingredient);
  }

  public List<IngredientMissingDto> getMissing(Long recipeId, Double iterations, LocalDate date,
      boolean allowExpiredProducts) {

    if (recipeId != null && !recipeRepository.existsById(recipeId)) {
      throw new ResourceNotFoundException("Recipe", "id", recipeId);
    }

    if (iterations != null && iterations <= 0) {
      throw new InvalidDataException("iterations", iterations, "must be > 0");
    }

    return repository.findMissingIngredients(recipeId, iterations, date, allowExpiredProducts);
  }

  public List<IngredientMissingDto> getMissing2(Long recipeId, Double iterations, LocalDate date,
      boolean allowExpiredProducts) {

    if (recipeId != null && !recipeRepository.existsById(recipeId)) {
      throw new ResourceNotFoundException("Recipe", "id", recipeId);
    }

    if (iterations != null && iterations <= 0) {
      throw new InvalidDataException("iterations", iterations, "must be > 0");
    }

    return repository.findMissingIngredients2(recipeId, iterations, date, allowExpiredProducts)
        .stream()
        .map(p -> new IngredientMissingDto(
            p.getIngredientId(),
            p.getProductName(),
            p.getRequired(),
            p.getAvailable(),
            p.getMissing()
        ))
        .toList();
  }

  private void validateIngredient(IngredientDto dto) {

    if (dto.getQuantity() != null && dto.getQuantity() <= 0) {
      throw new InvalidDataException("quantity", dto.getQuantity(), "must be > 0");
    }
  }
}
