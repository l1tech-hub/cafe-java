package com.cafe.repository;

import com.cafe.entity.Recipe;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

  List<Recipe> findByNameContainingIgnoreCase(String name);

  @EntityGraph(attributePaths = {"dish", "ingredients"})
  @NonNull
  List<Recipe> findAll();

  @EntityGraph(attributePaths = {"dish", "ingredients"})
  @Query("""
          SELECT r FROM Recipe r
          WHERE (:dishId IS NULL OR r.dish.id = :dishId)
      """)
  @NonNull
  Page<Recipe> findAllPageable(Pageable pageable, @Param("dishId") Long dishId);

  @EntityGraph(attributePaths = {"dish", "ingredients"})
  @NonNull
  Optional<Recipe> findById(@NonNull Long id);

}