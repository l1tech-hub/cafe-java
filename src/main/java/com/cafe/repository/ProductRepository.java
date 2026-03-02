package com.cafe.repository;

import com.cafe.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Интерфейс класса продукта.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findByName(String name);
}
