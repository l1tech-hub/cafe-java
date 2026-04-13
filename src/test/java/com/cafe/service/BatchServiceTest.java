package com.cafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cafe.dto.BatchDto;
import com.cafe.entity.Batch;
import com.cafe.entity.Product;
import com.cafe.exception.InvalidDataException;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.repository.BatchRepository;
import com.cafe.repository.ProductRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

  @Mock
  private BatchRepository batchRepository;

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private BatchService service;

  private BatchDto validDto;
  private Product product;
  private Batch batch;

  @BeforeEach
  void setUp() {
    product = new Product();
    product.setId(1L);

    batch = new Batch();
    batch.setId(1L);
    batch.setProduct(product);

    validDto = new BatchDto();
    validDto.setProductId(1L);
    validDto.setPrice(10.0);
    validDto.setQuantity(5.0);
    validDto.setManufactureDate(LocalDate.now().minusDays(1));
    validDto.setExpiryDate(LocalDate.now().plusDays(1));
  }

  @Test
  void shouldCreateBatchSuccessfully() {

    when(productRepository.findById(1L))
        .thenReturn(Optional.of(product));

    when(batchRepository.save(any(Batch.class)))
        .thenReturn(batch);

    BatchDto result = service.create(validDto);

    assertNotNull(result);

    verify(batchRepository).save(any(Batch.class));
  }

  @Test
  void shouldThrowWhenProductNotFoundOnCreate() {

    when(productRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.create(validDto)
    );

    verify(batchRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenPriceInvalid() {
    validDto.setPrice(-1.0);

    assertThrows(InvalidDataException.class, () ->
        service.create(validDto)
    );
  }

  @Test
  void shouldThrowWhenQuantityInvalid() {
    validDto.setQuantity(0.0);

    assertThrows(InvalidDataException.class, () ->
        service.create(validDto)
    );
  }

  @Test
  void shouldThrowWhenDatesInvalid() {
    validDto.setManufactureDate(LocalDate.now());
    validDto.setExpiryDate(LocalDate.now().minusDays(1));

    assertThrows(InvalidDataException.class, () ->
        service.create(validDto)
    );
  }

  @Test
  void shouldGetByIdSuccessfully() {

    when(batchRepository.findById(1L))
        .thenReturn(Optional.of(batch));

    BatchDto result = service.getById(1L);

    assertNotNull(result);
  }

  @Test
  void shouldThrowWhenBatchNotFound() {

    when(batchRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.getById(1L)
    );
  }

  @Test
  void shouldGetAll() {

    when(batchRepository.findAll())
        .thenReturn(List.of(batch));

    List<BatchDto> result = service.getAll();

    assertEquals(1, result.size());
  }

  @Test
  void shouldGetByProductSuccessfully() {

    when(productRepository.existsById(1L))
        .thenReturn(true);

    when(batchRepository.findByProductId(1L))
        .thenReturn(List.of(batch));

    List<BatchDto> result = service.getByProduct(1L);

    assertEquals(1, result.size());
  }

  @Test
  void shouldThrowWhenProductNotFoundInGetByProduct() {

    when(productRepository.existsById(1L))
        .thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () ->
        service.getByProduct(1L)
    );
  }

  @Test
  void shouldGetAllPaged() {

    Page<Batch> page = new PageImpl<>(List.of(batch));

    when(batchRepository.findAll(any(Pageable.class)))
        .thenReturn(page);

    Page<BatchDto> result = service.getAllPaged(PageRequest.of(0, 10));

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void shouldThrowWhenProductNotFoundInPaged() {

    when(productRepository.existsById(1L))
        .thenReturn(false);

    Pageable pageable = PageRequest.of(0, 10);

    assertThrows(ResourceNotFoundException.class, () ->
        service.getByProductPaged(1L, pageable)
    );
  }

  @Test
  void shouldGetByProductPagedSuccessfully() {

    Page<Batch> page = new PageImpl<>(List.of(batch));

    when(productRepository.existsById(1L))
        .thenReturn(true);

    when(batchRepository.findByProductId(eq(1L), any(Pageable.class)))
        .thenReturn(page);

    Page<BatchDto> result =
        service.getByProductPaged(1L, PageRequest.of(0, 10));

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void shouldUpdateSuccessfully() {

    when(batchRepository.findById(1L))
        .thenReturn(Optional.of(batch));

    when(productRepository.findById(1L))
        .thenReturn(Optional.of(product));

    when(batchRepository.save(any(Batch.class)))
        .thenReturn(batch);

    BatchDto result = service.update(1L, validDto);

    assertNotNull(result);
    verify(batchRepository).save(batch);
  }

  @Test
  void shouldUpdateWithoutChangingProduct() {

    validDto.setProductId(null);

    when(batchRepository.findById(1L))
        .thenReturn(Optional.of(batch));

    when(batchRepository.save(any(Batch.class)))
        .thenReturn(batch);

    BatchDto result = service.update(1L, validDto);

    assertNotNull(result);
    verify(productRepository, never()).findById(any());
  }

  @Test
  void shouldThrowWhenBatchNotFoundOnUpdate() {

    when(batchRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.update(1L, validDto)
    );
  }

  @Test
  void shouldThrowWhenProductNotFoundOnUpdate() {

    when(batchRepository.findById(1L))
        .thenReturn(Optional.of(batch));

    when(productRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.update(1L, validDto)
    );
  }


  @Test
  void shouldDeleteSuccessfully() {

    when(batchRepository.findById(1L))
        .thenReturn(Optional.of(batch));

    service.delete(1L);

    verify(batchRepository).deleteById(1L);
  }

  @Test
  void shouldThrowWhenDeleteNotFound() {

    when(batchRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.delete(1L)
    );

    verify(batchRepository, never()).deleteById(any());
  }
}

