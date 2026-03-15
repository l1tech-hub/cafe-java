package com.cafe.service;

import com.cafe.dto.BatchDto;
import com.cafe.entity.Batch;
import com.cafe.entity.Product;
import com.cafe.mapper.BatchMapper;
import com.cafe.repository.BatchRepository;
import com.cafe.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchService {

  private final BatchRepository batchRepository;
  private final ProductRepository productRepository;

  public BatchService(BatchRepository batchRepository,
      ProductRepository productRepository) {
    this.batchRepository = batchRepository;
    this.productRepository = productRepository;
  }

  @Transactional
  public BatchDto create(BatchDto dto) {

    Product product = productRepository.findById(dto.getProductId())
        .orElseThrow(() -> new RuntimeException("Product not found"));

    Batch batch = BatchMapper.toEntity(dto, product);

    return BatchMapper.toDto(batchRepository.save(batch));
  }

  public BatchDto getById(Long id) {

    Batch batch = batchRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Batch not found"));

    return BatchMapper.toDto(batch);
  }

  public List<BatchDto> getAll() {

    return batchRepository.findAll()
        .stream()
        .map(BatchMapper::toDto)
        .toList();
  }

  public List<BatchDto> getByProduct(Long productId) {

    return batchRepository.findByProductId(productId)
        .stream()
        .map(BatchMapper::toDto)
        .toList();
  }

  @Transactional
  public BatchDto update(Long id, BatchDto dto) {

    Batch batch = batchRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Batch not found"));

    batch.setPrice(dto.getPrice());
    batch.setManufactureDate(dto.getManufactureDate());
    batch.setExpiryDate(dto.getExpiryDate());

    if (dto.getProductId() != null) {
      Product product = productRepository.findById(dto.getProductId())
          .orElseThrow(() -> new RuntimeException("Product not found"));
      batch.setProduct(product);
    }

    return BatchMapper.toDto(batchRepository.save(batch));
  }

  @Transactional
  public void delete(Long id) {
    batchRepository.deleteById(id);
  }
}