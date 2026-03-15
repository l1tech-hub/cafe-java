package com.cafe.repository;

import com.cafe.entity.Recipe;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

  List<Recipe> findByNameContainingIgnoreCase(String name);

  @EntityGraph(attributePaths = {"dish", "ingredients"})
  @NonNull
  List<Recipe> findAll();

  @EntityGraph(attributePaths = {"dish", "ingredients"})
  @NonNull
  Optional<Recipe> findById(@NonNull Long id);

}