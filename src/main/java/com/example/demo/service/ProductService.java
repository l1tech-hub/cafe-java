package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.dto.ProductDto;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product add(String name, Double price) {
        Product product = new Product(name, price);
        return repository.save(product);
    }

    public ProductDto getById(Long id) {
        Product product = repository.findById(id).orElse(null);
        return product != null ? ProductMapper.toDto(product) : null;
    }

    public List<ProductDto> getAll() {
        return repository.findAll()
                .stream()
                .map(ProductMapper::toDto)
                .toList();
    }
}

