package com.cafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cafe.dto.ProductDto;
import com.cafe.entity.Product;
import com.cafe.exception.InvalidDataException;
import com.cafe.exception.ResourceInUseException;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.repository.IngredientRepository;
import com.cafe.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository repository;

  @Mock
  private IngredientRepository ingredientRepository;

  @InjectMocks
  private ProductService service;

  private Product product;
  private ProductDto dto;

  @BeforeEach
  void setUp() {

    product = new Product();
    product.setId(1L);
    product.setName("Milk");

    dto = new ProductDto();
    dto.setName("Milk");
  }


  @Test
  void shouldAddProduct() {

    when(repository.save(any()))
        .thenReturn(product);

    Product result = service.add("Milk");

    assertNotNull(result);
    verify(repository).save(any());
  }

  @Test
  void shouldThrowWhenNameBlankOnAdd() {

    assertThrows(InvalidDataException.class, () ->
        service.add("   ")
    );
  }

  @Test
  void shouldAllowNullNameOnAdd() {

    when(repository.save(any()))
        .thenReturn(product);

    Product result = service.add(null);

    assertNotNull(result);
  }


  @Test
  void shouldGetById() {

    when(repository.findById(1L))
        .thenReturn(Optional.of(product));

    ProductDto result = service.getById(1L);

    assertNotNull(result);
  }

  @Test
  void shouldThrowWhenNotFound() {

    when(repository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.getById(1L)
    );
  }


  @Test
  void shouldGetAll() {

    when(repository.findAll())
        .thenReturn(List.of(product));

    List<ProductDto> result = service.getAll();

    assertEquals(1, result.size());
  }


  @Test
  void shouldFindByName() {

    when(repository.findByNameContainingIgnoreCase("milk"))
        .thenReturn(List.of(product));

    List<ProductDto> result = service.findByName("milk");

    assertEquals(1, result.size());
    assertEquals(product.getName(), result.getFirst().getName());
  }

  @Test
  void shouldThrowWhenNameBlankOnSearch() {

    assertThrows(InvalidDataException.class, () ->
        service.findByName(" ")
    );
  }

  @Test
  void shouldAllowNullNameOnSearch() {

    when(repository.findByNameContainingIgnoreCase(null))
        .thenReturn(List.of(product));

    List<ProductDto> result = service.findByName(null);

    assertEquals(1, result.size());
  }


  @Test
  void shouldUpdateName() {

    ProductDto update = new ProductDto();
    update.setName("Cream");

    when(repository.findById(1L))
        .thenReturn(Optional.of(product));

    when(repository.save(any()))
        .thenReturn(product);

    ProductDto result = service.update(1L, update);

    assertNotNull(result);
  }

  @Test
  void shouldThrowWhenUpdateNotFound() {

    when(repository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.update(1L, dto)
    );
  }

  @Test
  void shouldThrowWhenUpdateNameBlank() {

    ProductDto update = new ProductDto();
    update.setName(" ");

    when(repository.findById(1L))
        .thenReturn(Optional.of(product));

    assertThrows(InvalidDataException.class, () ->
        service.update(1L, update)
    );
  }

  @Test
  void shouldUpdateWithNullName() {

    ProductDto update = new ProductDto();
    update.setName(null);

    when(repository.findById(1L))
        .thenReturn(Optional.of(product));

    when(repository.save(any()))
        .thenReturn(product);

    ProductDto result = service.update(1L, update);

    assertNotNull(result);
  }

  @Test
  void shouldDelete() {

    when(repository.findById(1L))
        .thenReturn(Optional.of(product));

    when(ingredientRepository.existsByProductId(1L))
        .thenReturn(false);

    service.delete(1L);

    verify(repository).delete(product);
  }

  @Test
  void shouldThrowWhenProductInUse() {

    when(repository.findById(1L))
        .thenReturn(Optional.of(product));

    when(ingredientRepository.existsByProductId(1L))
        .thenReturn(true);

    assertThrows(ResourceInUseException.class, () ->
        service.delete(1L)
    );
  }

  @Test
  void shouldThrowWhenDeleteNotFound() {

    when(repository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.delete(1L)
    );
  }
}
