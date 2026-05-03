package com.cafe.repository;

import com.cafe.entity.DishCookStatistics;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DishCookStatisticsRepository extends JpaRepository<DishCookStatistics, Long> {

  Optional<DishCookStatistics> findByDishId(Long dishId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value = """
          INSERT INTO dish_cook_statistics (dish_id, cook_count)
          VALUES (:dishId, 1)
          ON CONFLICT (dish_id) DO UPDATE SET cook_count = dish_cook_statistics.cook_count + 1
          """,
      nativeQuery = true
  )
  void upsertIncrementCookCount(@Param("dishId") Long dishId);
}
