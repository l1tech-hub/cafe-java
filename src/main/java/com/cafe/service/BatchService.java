package com.cafe.service;

import com.cafe.dto.BatchDto;
import com.cafe.entity.Batch;
import com.cafe.entity.Product;
import com.cafe.exception.InvalidDataException;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.mapper.BatchMapper;
import com.cafe.repository.BatchRepository;
import com.cafe.repository.ProductRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchService {

  private static final String BATCH_MSG = "Batch";

  private final BatchRepository batchRepository;
  private final ProductRepository productRepository;

  public BatchService(BatchRepository batchRepository,
      ProductRepository productRepository) {
    this.batchRepository = batchRepository;
    this.productRepository = productRepository;
  }

  @Transactional
  public BatchDto create(BatchDto dto) {

    validateBatch(dto);

    Long productId = dto.getProductId();
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

    Batch batch = BatchMapper.toEntity(dto, product);

    return BatchMapper.toDto(batchRepository.save(batch));
  }

  public BatchDto getById(Long id) {

    Batch batch = batchRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(BATCH_MSG, "id", id));

    return BatchMapper.toDto(batch);
  }

  public List<BatchDto> getAll() {

    return batchRepository.findAll()
        .stream()
        .map(BatchMapper::toDto)
        .toList();
  }

  public List<BatchDto> getByProduct(Long productId) {
    if (!productRepository.existsById(productId)) {
      throw new ResourceNotFoundException("Product", "id", productId);
    }

    return batchRepository.findByProductId(productId)
        .stream()
        .map(BatchMapper::toDto)
        .toList();
  }

  public Page<BatchDto> getAllPaged(Pageable pageable) {
    return batchRepository.findAll(pageable)
        .map(BatchMapper::toDto);
  }

  public Page<BatchDto> getByProductPaged(Long productId, Pageable pageable) {
    if (!productRepository.existsById(productId)) {
      throw new ResourceNotFoundException("Product", "id", productId);
    }
    return batchRepository.findByProductId(productId, pageable)
        .map(BatchMapper::toDto);
  }

  @Transactional
  public BatchDto update(Long id, BatchDto dto) {

    validateBatch(dto);

    Batch batch = batchRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(BATCH_MSG, "id", id));

    batch.setPrice(dto.getPrice());
    batch.setQuantity(dto.getQuantity());
    batch.setManufactureDate(dto.getManufactureDate());
    batch.setExpiryDate(dto.getExpiryDate());

    Long productId = dto.getProductId();

    if (dto.getProductId() != null) {
      Product product = productRepository.findById(dto.getProductId())
          .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
      batch.setProduct(product);
    }

    return BatchMapper.toDto(batchRepository.save(batch));
  }

  @Transactional
  public void delete(Long id) {

    batchRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(BATCH_MSG, "id", id));

    batchRepository.deleteById(id);
  }

  private void validateBatch(BatchDto dto) {

    if (dto.getPrice() < 0) {
      throw new InvalidDataException("price", dto.getPrice(), "must be >= 0");
    }

    if (dto.getQuantity() <= 0) {
      throw new InvalidDataException("quantity", dto.getQuantity(), "must be > 0");
    }

    if (dto.getManufactureDate().isAfter(dto.getExpiryDate())) {
      throw new InvalidDataException(
          "expiryDate",
          dto.getExpiryDate(),
          "must be after manufactureDate"
      );
    }
  }
}