package com.cafe.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cafe.dto.IngredientDto;
import com.cafe.dto.IngredientMissingDto;
import com.cafe.dto.IngredientMissingProjection;
import com.cafe.entity.Ingredient;
import com.cafe.entity.Product;
import com.cafe.entity.Recipe;
import com.cafe.exception.InvalidDataException;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.repository.IngredientRepository;
import com.cafe.repository.ProductRepository;
import com.cafe.repository.RecipeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

  @Mock
  private IngredientRepository repository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private RecipeRepository recipeRepository;

  @InjectMocks
  private IngredientService service;

  private IngredientDto dto;
  private Product product;
  private Recipe recipe;
  private Ingredient ingredient;


  @BeforeEach
  void setUp() {

    product = new Product();
    product.setId(1L);

    recipe = new Recipe();
    recipe.setId(1L);

    ingredient = new Ingredient();
    ingredient.setId(1L);
    ingredient.setProduct(product);
    ingredient.setRecipe(recipe);

    dto = new IngredientDto();
    dto.setProductId(1L);
    dto.setRecipeId(1L);
    dto.setQuantity(2.0);
  }

  @Test
  void shouldAddIngredientSuccessfully() {

    Ingredient saved = new Ingredient();
    saved.setId(1L);
    saved.setRecipe(recipe);   // FIX
    saved.setProduct(product); // FIX

    when(productRepository.findById(1L))
        .thenReturn(Optional.of(product));

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(recipe));

    when(repository.save(any()))
        .thenReturn(saved);

    IngredientDto result = service.add(dto);

    assertNotNull(result);
    verify(repository).save(any());
  }

  @Test
  void shouldThrowWhenProductNotFound() {

    when(productRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.add(dto)
    );
  }

  @Test
  void shouldThrowWhenRecipeNotFound() {

    when(productRepository.findById(1L))
        .thenReturn(Optional.of(product));

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.add(dto)
    );
  }

  @Test
  void shouldThrowWhenQuantityInvalidOnAdd() {

    dto.setQuantity(0.0);

    assertThrows(InvalidDataException.class, () ->
        service.add(dto)
    );
  }


  @Test
  void shouldGetByRecipeSuccessfully() {

    Ingredient ing = new Ingredient();
    ing.setId(1L);
    ing.setRecipe(recipe);     // FIX
    ing.setProduct(product);   // FIX

    when(recipeRepository.existsById(1L))
        .thenReturn(true);

    when(repository.findByRecipeId(1L))
        .thenReturn(List.of(ing));

    List<IngredientDto> result = service.getByRecipe(1L);

    assertEquals(1, result.size());
  }


  @Test
  void shouldThrowWhenRecipeNotExists() {

    when(recipeRepository.existsById(1L))
        .thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () ->
        service.getByRecipe(1L)
    );
  }


  @Test
  void shouldUpdateQuantityOnly() {

    Ingredient updated = new Ingredient();
    updated.setQuantity(5.0);

    when(repository.findById(1L))
        .thenReturn(Optional.of(ingredient));

    when(repository.save(any()))
        .thenReturn(ingredient);

    Ingredient result = service.updateIngredient(1L, updated);

    assertNotNull(result);
  }

  @Test
  void shouldThrowWhenQuantityInvalidOnUpdate() {

    Ingredient updated = new Ingredient();
    updated.setQuantity(0.0);

    when(repository.findById(1L))
        .thenReturn(Optional.of(ingredient));

    assertThrows(InvalidDataException.class, () ->
        service.updateIngredient(1L, updated)
    );
  }

  @Test
  void shouldUpdateProduct() {

    Ingredient updated = new Ingredient();
    Product newProduct = new Product();
    newProduct.setId(2L);
    updated.setProduct(newProduct);

    when(repository.findById(1L))
        .thenReturn(Optional.of(ingredient));

    when(productRepository.findById(2L))
        .thenReturn(Optional.of(newProduct));

    when(repository.save(any()))
        .thenReturn(ingredient);

    Ingredient result = service.updateIngredient(1L, updated);

    assertNotNull(result);
  }

  @Test
  void shouldUpdateRecipe() {

    Ingredient updated = new Ingredient();
    Recipe newRecipe = new Recipe();
    newRecipe.setId(2L);
    updated.setRecipe(newRecipe);

    when(repository.findById(1L))
        .thenReturn(Optional.of(ingredient));

    when(recipeRepository.findById(2L))
        .thenReturn(Optional.of(newRecipe));

    when(repository.save(any()))
        .thenReturn(ingredient);

    Ingredient result = service.updateIngredient(1L, updated);

    assertNotNull(result);
  }

  @Test
  void shouldThrowWhenIngredientNotFoundOnUpdate() {

    when(repository.findById(1L))
        .thenReturn(Optional.empty());

    Ingredient updated = new Ingredient();

    assertThrows(ResourceNotFoundException.class, () ->
        service.updateIngredient(1L, updated)
    );
  }

  @Test
  void shouldThrowWhenProductNotFoundOnUpdate() {

    Ingredient updated = new Ingredient();
    Product p = new Product();
    p.setId(2L);
    updated.setProduct(p);

    when(repository.findById(1L))
        .thenReturn(Optional.of(ingredient));

    when(productRepository.findById(2L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.updateIngredient(1L, updated)
    );
  }

  @Test
  void shouldThrowWhenRecipeNotFoundOnUpdate() {

    Ingredient updated = new Ingredient();
    Recipe r = new Recipe();
    r.setId(2L);
    updated.setRecipe(r);

    when(repository.findById(1L))
        .thenReturn(Optional.of(ingredient));

    when(recipeRepository.findById(2L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.updateIngredient(1L, updated)
    );
  }


  @Test
  void shouldDeleteIngredient() {

    when(repository.findById(1L))
        .thenReturn(Optional.of(ingredient));

    service.delete(1L);

    verify(repository).delete(ingredient);
  }

  @Test
  void shouldThrowWhenDeleteNotFound() {

    when(repository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.delete(1L)
    );
  }

  // ---------- GET MISSING ----------

  @Test
  void shouldGetMissingSuccessfully() {

    when(recipeRepository.existsById(anyLong()))
        .thenReturn(true);

    when(recipeRepository.existsById(1L))
        .thenReturn(true);

    when(repository.findMissingIngredients(any(), any(), any()))
        .thenReturn(List.of());

    List<IngredientMissingDto> result =
        service.getMissing(1L, 2.0, LocalDate.now());

    assertNotNull(result);
  }

  @Test
  void shouldThrowWhenIterationsInvalid() {

    LocalDate date = LocalDate.now();

    assertThrows(InvalidDataException.class, () ->
        service.getMissing(null, 0.0, date)
    );
  }

  @Test
  void shouldGetMissing2Successfully() {

    when(recipeRepository.existsById(anyLong()))
        .thenReturn(true);

    when(recipeRepository.existsById(1L))
        .thenReturn(true);

    when(repository.findMissingIngredients2(any(), any(), any()))
        .thenReturn(List.of());

    List<IngredientMissingDto> result =
        service.getMissing2(1L, 2.0, LocalDate.now());

    assertNotNull(result);
  }

  @Test
  void shouldThrowWhenRecipeInvalidInMissing() {

    when(recipeRepository.existsById(anyLong()))
        .thenReturn(true);

    when(recipeRepository.existsById(1L))
        .thenReturn(false);

    LocalDate date = LocalDate.now();

    assertThrows(ResourceNotFoundException.class, () ->
        service.getMissing(1L, 2.0, date)
    );
  }

  @Test
  void shouldMapMissing2Correctly() {


    when(recipeRepository.existsById(1L))
        .thenReturn(true);

    IngredientMissingProjection p = mock(IngredientMissingProjection.class);

    when(p.getIngredientId()).thenReturn(1L);
    when(p.getProductName()).thenReturn("Milk");
    when(p.getRequired()).thenReturn(10.0);
    when(p.getAvailable()).thenReturn(5.0);
    when(p.getMissing()).thenReturn(5.0);

    when(repository.findMissingIngredients2(any(), any(), any()))
        .thenReturn(List.of(p));

    List<IngredientMissingDto> result =
        service.getMissing2(1L, 2.0, LocalDate.now());

    assertEquals(1, result.size());
  }

  @Test
  void shouldReturnMissingIngredients2Successfully() {
    Long recipeId = 1L;
    Double iterations = 2.0;
    LocalDate date = LocalDate.now();

    when(recipeRepository.existsById(recipeId)).thenReturn(true);

    IngredientMissingProjection p = mock(IngredientMissingProjection.class);
    when(p.getIngredientId()).thenReturn(1L);
    when(p.getProductName()).thenReturn("Milk");
    when(p.getRequired()).thenReturn(10.0);
    when(p.getAvailable()).thenReturn(5.0);
    when(p.getMissing()).thenReturn(5.0);

    when(repository.findMissingIngredients2(recipeId, iterations, date))
        .thenReturn(List.of(p));

    var result = service.getMissing2(recipeId, iterations, date);

    assertEquals(1, result.size());
  }

  @Test
  void shouldThrowWhenRecipeNotExists_getMissing2() {
    when(recipeRepository.existsById(1L)).thenReturn(false);

    LocalDate date = LocalDate.now();

    assertThrows(ResourceNotFoundException.class,
        () -> service.getMissing2(1L, 1.0, date));
  }


  @Test
  void shouldThrowWhenIterationsInvalid_getMissing2() {

    when(recipeRepository.existsById(1L)).thenReturn(true);

    LocalDate date = LocalDate.now();

    assertThrows(InvalidDataException.class,
        () -> service.getMissing2(1L, 0.0, date));
  }

  @Test
  void shouldWorkWhenRecipeIdIsNull_getMissing2() {
    when(repository.findMissingIngredients2(null, 1.0, LocalDate.now()))
        .thenReturn(List.of());


    LocalDate date = LocalDate.now();

    var result = service.getMissing2(null, 1.0, date);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldWorkWhenIterationsNull_getMissing() {
    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(repository.findMissingIngredients(1L, null, LocalDate.now()))
        .thenReturn(List.of());

    List<IngredientMissingDto> result =
        service.getMissing(1L, null, LocalDate.now());

    assertNotNull(result);
  }

  @Test
  void shouldWorkWhenIterationsNull_getMissing2() {
    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(repository.findMissingIngredients2(1L, null, LocalDate.now()))
        .thenReturn(List.of());

    List<IngredientMissingDto> result =
        service.getMissing2(1L, null, LocalDate.now());

    assertNotNull(result);
  }

  @Test
  void shouldPassWhenQuantityNull() {
    IngredientDto dtoNull = new IngredientDto();
    dtoNull.setQuantity(null);
    dtoNull.setProductId(1L);
    dtoNull.setRecipeId(1L);

    Product productNull = new Product();
    Recipe recipeNull = new Recipe();
    Ingredient ingredientNull = new Ingredient();
    ingredientNull.setProduct(productNull);
    ingredientNull.setRecipe(recipeNull);

    when(productRepository.findById(1L)).thenReturn(Optional.of(productNull));
    when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipeNull));
    when(repository.save(any())).thenReturn(ingredientNull);

    assertDoesNotThrow(() -> service.add(dtoNull));
  }
}
