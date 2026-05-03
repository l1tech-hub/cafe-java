package com.cafe.repository;

import com.cafe.dto.IngredientMissingDto;
import com.cafe.dto.IngredientMissingProjection;
import com.cafe.entity.Ingredient;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

  List<Ingredient> findByRecipeId(Long recipeId);


  @Query("""
          SELECT new com.cafe.dto.IngredientMissingDto(
              i.id,
              p.name,
              i.quantity * :iterations,
              COALESCE(SUM(b.quantity), 0.0),
              i.quantity * :iterations - COALESCE(SUM(b.quantity), 0.0)
          )
          FROM Ingredient i
          JOIN i.product p
          LEFT JOIN p.batches b
              ON (b.expiryDate >= :cookingDate OR :allowExpiredProducts = true)
          WHERE i.recipe.id = :recipeId
          GROUP BY i.id, p.name, i.quantity
          HAVING COALESCE(SUM(b.quantity), 0.0) < (i.quantity * :iterations)
      """)
  List<IngredientMissingDto> findMissingIngredients(
      @Param("recipeId") Long recipeId,
      @Param("iterations") Double iterations,
      @Param("cookingDate") LocalDate cookingDate,
      @Param("allowExpiredProducts") boolean allowExpiredProducts
  );

  @Query(value = """
          SELECT
              i.id as IngredientId,
              p.name as productName,
              (i.quantity * :iterations) as required,
              COALESCE(SUM(b.quantity), 0.0) as available,
              ((i.quantity * :iterations) - COALESCE(SUM(b.quantity), 0.0)) as missing
          FROM ingredient i
          INNER JOIN product p ON i.product_id = p.id
          LEFT JOIN batch b
              ON b.product_id = p.id
             AND (b.expiry_date >= :cookingDate OR :allowExpiredProducts = true)
          WHERE i.recipe_id = :recipeId
          GROUP BY i.id, p.name, i.quantity
          HAVING COALESCE(SUM(b.quantity), 0.0) < (i.quantity * :iterations)
      """, nativeQuery = true)
  List<IngredientMissingProjection> findMissingIngredients2(
      @Param("recipeId") Long recipeId,
      @Param("iterations") Double iterations,
      @Param("cookingDate") LocalDate cookingDate,
      @Param("allowExpiredProducts") boolean allowExpiredProducts
  );

  boolean existsByProductId(Long productId);
}