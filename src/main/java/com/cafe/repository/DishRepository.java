package com.cafe.repository;

import com.cafe.entity.Dish;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface DishRepository extends JpaRepository<Dish, Long> {

  List<Dish> findByNameContainingIgnoreCase(String name);

  @EntityGraph(attributePaths = {"recipe", "recipe.ingredients"})
  @NonNull
  List<Dish> findAll();
}