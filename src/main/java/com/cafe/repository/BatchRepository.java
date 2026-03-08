package com.cafe.repository;

import com.cafe.entity.Batch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch, Long> {

  List<Batch> findByProductId(Long productId);
}