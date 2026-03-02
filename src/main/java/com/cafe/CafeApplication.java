package com.cafe;

import com.cafe.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Точка входа в программу.
 */
@SpringBootApplication
public class CafeApplication {

  public static void main(String[] args) {
    SpringApplication.run(CafeApplication.class, args);
  }

  @Bean
  CommandLineRunner initDatabase(ProductService service) {
    return args -> {
      if (service.getAll().isEmpty()) {
        service.add("Яблоко", 2.5);
        service.add("Молоко", 1.8);
        service.add("Гречка", 3.2);
      }
    };
  }
}
