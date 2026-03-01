package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Интерфейс класса продукта.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

}
