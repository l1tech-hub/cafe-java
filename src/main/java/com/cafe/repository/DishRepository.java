package com.cafe.repository;

import com.cafe.entity.Dish;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface DishRepository extends JpaRepository<Dish, Long> {

  List<Dish> findByNameContainingIgnoreCase(String name);

  @EntityGraph(attributePaths = {"recipe", "recipe.ingredients"})
  @NonNull
  List<Dish> findAll();

  @EntityGraph(attributePaths = {"recipe", "recipe.ingredients", "recipe.ingredients.product"})
  @NonNull
  Optional<Dish> findWithRecipeGraphById(@NonNull Long id);
}