package com.cafe.repository;

import com.cafe.entity.Ingredient;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

  List<Ingredient> findByRecipeId(Long recipeId);

  boolean existsByProductId(Long productId);
}