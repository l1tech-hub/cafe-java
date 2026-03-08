package com.cafe;

import com.cafe.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CafeApplication {

  public static void main(String[] args) {
    SpringApplication.run(CafeApplication.class, args);
  }
}
