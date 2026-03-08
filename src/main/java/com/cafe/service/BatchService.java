package com.cafe.service;

import com.cafe.dto.BatchDto;
import com.cafe.entity.Batch;
import com.cafe.entity.Product;
import com.cafe.mapper.BatchMapper;
import com.cafe.repository.BatchRepository;
import com.cafe.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class BatchService {

  private final BatchRepository batchRepository;
  private final ProductRepository productRepository;

  public BatchService(BatchRepository batchRepository,
      ProductRepository productRepository) {
    this.batchRepository = batchRepository;
    this.productRepository = productRepository;
  }

  public BatchDto create(BatchDto dto) {

    Product product = productRepository.findById(dto.getProductId())
        .orElseThrow(() -> new RuntimeException("Product not found"));

    Batch batch = BatchMapper.toEntity(dto, product);

    return BatchMapper.toDto(batchRepository.save(batch));
  }

  public List<BatchDto> getByProduct(Long productId) {

    return batchRepository.findByProductId(productId)
        .stream()
        .map(BatchMapper::toDto)
        .toList();
  }

  public BatchDto update(Long id, BatchDto dto) {

    Batch batch = batchRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Batch not found"));

    batch.setPrice(dto.getPrice());
    batch.setManufactureDate(dto.getManufactureDate());
    batch.setExpiryDate(dto.getExpiryDate());

    Batch updated = batchRepository.save(batch);

    return BatchMapper.toDto(updated);
  }

  public void delete(Long id) {

    if (!batchRepository.existsById(id)) {
      throw new EntityNotFoundException("Batch not found");
    }

    batchRepository.deleteById(id);
  }
}