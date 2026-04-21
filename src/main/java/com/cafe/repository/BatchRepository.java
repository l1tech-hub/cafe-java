package com.cafe.repository;

import com.cafe.entity.Batch;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BatchRepository extends JpaRepository<Batch, Long> {

  List<Batch> findByProductId(Long productId);

  Page<Batch> findByProductId(Long productId, Pageable pageable);

  Page<Batch> findAll(Pageable pageable);

  @Query("""
          SELECT b FROM Batch b
          WHERE b.product.id = :productId
          ORDER BY b.expiryDate ASC
      """)
  List<Batch> findByProductIdOrderByExpiryDateAsc(Long productId);
}
