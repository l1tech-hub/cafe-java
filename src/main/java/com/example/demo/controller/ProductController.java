package com.example.demo.controller;

import com.example.demo.dto.ProductDto;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/add")
    public Product add(
            @RequestParam String name,
            @RequestParam Double price) {
        return service.add(name, price);
    }

    @GetMapping(value = "/{id}")
    public ProductDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<ProductDto> getAll() {
        return service.getAll();
    }
}