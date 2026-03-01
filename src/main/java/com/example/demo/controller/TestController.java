package com.example.demo.controller;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final ProductRepository repo;

    public TestController(ProductRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/test")
    public String test() {
        Product p = new Product();
        p.setName("Test");
        p.setPrice(10.0);

        repo.save(p);

        return "Saved!";
    }
}