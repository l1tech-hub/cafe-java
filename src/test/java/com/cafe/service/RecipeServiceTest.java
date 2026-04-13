package com.cafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.cafe.dto.CreateRecipeDto;
import com.cafe.dto.IngredientDto;
import com.cafe.dto.RecipeDto;
import com.cafe.dto.RecipeIngredientRequestDto;
import com.cafe.entity.Dish;
import com.cafe.entity.Ingredient;
import com.cafe.entity.Product;
import com.cafe.entity.Recipe;
import com.cafe.exception.InvalidDataException;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.repository.DishRepository;
import com.cafe.repository.IngredientRepository;
import com.cafe.repository.ProductRepository;
import com.cafe.repository.RecipeRepository;
import com.cafe.service.cache.RecipeCache;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
class RecipeServiceTest {

  @Mock
  private RecipeRepository recipeRepository;
  @Mock
  private IngredientRepository ingredientRepository;
  @Mock
  private ProductRepository productRepository;
  @Mock
  private DishRepository dishRepository;
  @Mock
  private RecipeCache recipeCache;

  @InjectMocks
  private RecipeService service;

  private Recipe recipe;
  private Dish dish;
  private Product product;

  @BeforeEach
  void setUp() {
    recipe = new Recipe();
    recipe.setId(1L);
    recipe.setName("R");

    dish = new Dish();
    dish.setId(1L);

    product = new Product();
    product.setId(1L);
  }

  @Test
  void shouldCreateRecipeWithoutDish() {

    RecipeDto dto = new RecipeDto();

    when(recipeRepository.save(any()))
        .thenReturn(recipe);

    RecipeDto result = service.createRecipe(dto);

    assertNotNull(result);
    verify(recipeCache).clearAll();
  }

  @Test
  void shouldCreateRecipeWithDish() {

    RecipeDto dto = new RecipeDto();
    dto.setDishId(1L);

    when(dishRepository.findById(1L))
        .thenReturn(Optional.of(dish));

    when(recipeRepository.save(any()))
        .thenReturn(recipe);

    RecipeDto result = service.createRecipe(dto);

    assertNotNull(result);
  }

  @Test
  void shouldThrowWhenDishNotFoundOnCreate() {

    RecipeDto dto = new RecipeDto();
    dto.setDishId(1L);

    when(dishRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.createRecipe(dto)
    );
  }


  @Test
  void shouldCreateRecipeWithIngredients() {

    CreateRecipeDto request = new CreateRecipeDto();
    request.setName("R");
    request.setIngredients(List.of(new IngredientDto() {{
      setProductId(1L);
      setQuantity(2.0);
    }}));

    when(productRepository.findById(1L))
        .thenReturn(Optional.of(product));

    service.createRecipeWithIngredients(request);

    verify(ingredientRepository).save(any());
    verify(recipeCache).clearAll();
  }

  @Test
  void shouldThrowWhenProductNotFoundInCreateWithIngredients() {

    CreateRecipeDto request = new CreateRecipeDto();
    request.setIngredients(List.of(new IngredientDto() {{
      setProductId(1L);
      setQuantity(2.0);
    }}));

    when(productRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.createRecipeWithIngredients(request)
    );
  }


  @Test
  void shouldReturnCachedList() {

    when(recipeCache.getAll())
        .thenReturn(List.of(new RecipeDto()));

    List<RecipeDto> result = service.getAll();

    assertEquals(1, result.size());
    verify(recipeRepository, never()).findAll();
  }

  @Test
  void shouldLoadAndCacheList() {

    when(recipeCache.getAll()).thenReturn(null);
    when(recipeRepository.findAll()).thenReturn(List.of(recipe));

    List<RecipeDto> result = service.getAll();

    assertEquals(1, result.size());
    verify(recipeCache).putAll(any());
  }


  @Test
  void shouldReturnCachedPage() {

    Page<RecipeDto> page = new PageImpl<>(List.of(new RecipeDto()));

    Pageable pageable = PageRequest.of(0, 10);

    when(recipeCache.getPage(pageable, 1L))
        .thenReturn(page);

    Page<RecipeDto> result = service.getAllPaged(pageable, 1L);

    assertNotNull(result);
    verify(recipeRepository, never()).findAllPageable(any(), any());
  }

  @Test
  void shouldLoadAndCachePage() {

    Pageable pageable = PageRequest.of(0, 10);

    when(recipeCache.getPage(pageable, null)).thenReturn(null);
    when(recipeRepository.findAllPageable(pageable, null))
        .thenReturn(new PageImpl<>(List.of(recipe)));

    Page<RecipeDto> result = service.getAllPaged(pageable, null);

    assertNotNull(result);
    verify(recipeCache).putPage(eq(pageable), any(), eq(null));
  }


  @Test
  void shouldReturnCachedById() {

    when(recipeCache.getById(1L))
        .thenReturn(new RecipeDto());

    RecipeDto result = service.getById(1L);

    assertNotNull(result);
    verify(recipeRepository, never()).findById(any());
  }

  @Test
  void shouldLoadAndCacheById() {

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(recipe));

    RecipeDto result = service.getById(1L);

    assertNotNull(result);
    verify(recipeCache).putById(eq(1L), any());
  }

  @Test
  void shouldThrowWhenNotFoundById() {

    when(recipeCache.getById(1L)).thenReturn(null);
    when(recipeRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.getById(1L)
    );
  }


  @Test
  void shouldUpdateWithDish() {

    RecipeDto dto = new RecipeDto();
    dto.setDishId(1L);

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(recipe));

    when(dishRepository.findById(1L))
        .thenReturn(Optional.of(dish));

    when(recipeRepository.save(any()))
        .thenReturn(recipe);

    RecipeDto result = service.updateRecipe(1L, dto);

    assertNotNull(result);
    verify(recipeCache).evictById(1L);
  }

  @Test
  void shouldUpdateWithoutDish() {

    RecipeDto dto = new RecipeDto();
    dto.setDishId(null);

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(recipe));

    when(recipeRepository.save(any()))
        .thenReturn(recipe);

    RecipeDto result = service.updateRecipe(1L, dto);

    assertNotNull(result);
    assertNull(recipe.getDish());
  }

  @Test
  void shouldThrowWhenDishNotFoundOnUpdate() {

    RecipeDto dto = new RecipeDto();
    dto.setDishId(1L);

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(recipe));

    when(dishRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.updateRecipe(1L, dto)
    );
  }


  @Test
  void shouldDeleteRecipe() {

    recipe.setDish(dish);

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(recipe));

    service.deleteRecipe(1L);

    verify(recipeRepository).delete(recipe);
    assertNull(dish.getRecipe());
  }

  @Test
  void shouldThrowWhenDeleteNotFound() {

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.deleteRecipe(1L)
    );
  }


  @Test
  void shouldAddIngredients() {

    RecipeIngredientRequestDto dto = new RecipeIngredientRequestDto();
    dto.setProductId(1L);
    dto.setQuantity(2.0);

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(recipe));

    when(productRepository.findById(1L))
        .thenReturn(Optional.of(product));

    service.addIngredients(1L, List.of(dto));

    verify(ingredientRepository).save(any());
  }

  @Test
  void shouldThrowWhenRecipeNotFoundInBulk() {

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.empty());

    List<RecipeIngredientRequestDto> list = List.of();

    assertThrows(ResourceNotFoundException.class, () ->
        service.addIngredients(1L, list)
    );
  }

  @Test
  void shouldThrowWhenProductIdNullInBulk() {

    RecipeIngredientRequestDto dto = new RecipeIngredientRequestDto();
    dto.setProductId(null);
    dto.setQuantity(2.0);

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(recipe));

    List<RecipeIngredientRequestDto> list = List.of(dto);

    assertThrows(InvalidDataException.class, () ->
        service.addIngredients(1L, list)
    );
  }

  @Test
  void shouldThrowWhenQuantityInvalidInBulk() {

    RecipeIngredientRequestDto dto = new RecipeIngredientRequestDto();
    dto.setProductId(1L);
    dto.setQuantity(0.0);

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(recipe));

    List<RecipeIngredientRequestDto> list = List.of(dto);

    assertThrows(InvalidDataException.class, () ->
        service.addIngredients(1L, list)
    );
  }

  @Test
  void shouldThrowWhenQuantityInvalid() {

    CreateRecipeDto request = new CreateRecipeDto();
    request.setIngredients(List.of(new IngredientDto() {{
      setProductId(1L);
      setQuantity(0.0);
    }}));

    assertThrows(InvalidDataException.class, () ->
        service.createRecipeWithIngredients(request)
    );
  }

  @Test
  void shouldHandleNullIngredients() {

    CreateRecipeDto request = new CreateRecipeDto();
    request.setIngredients(null);

    service.createRecipeWithIngredients(request);

    verify(ingredientRepository, never()).save(any());
  }


  @Test
  void shouldCreateWithDish() {

    CreateRecipeDto request = new CreateRecipeDto();
    request.setDishId(1L);

    when(dishRepository.findById(1L))
        .thenReturn(Optional.of(new Dish()));

    service.createRecipeWithIngredients(request);

    verify(dishRepository).findById(1L);
  }

  @Test
  void shouldThrowWhenDishNotFound() {

    CreateRecipeDto request = new CreateRecipeDto();
    request.setDishId(1L);

    when(dishRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.createRecipeWithIngredients(request)
    );
  }

  @Test
  void shouldThrowWhenQuantityNull() {

    CreateRecipeDto request = new CreateRecipeDto();
    request.setIngredients(List.of(new IngredientDto() {{
      setProductId(1L);
      setQuantity(null);
    }}));

    assertThrows(InvalidDataException.class, () ->
        service.createRecipeWithIngredients(request)
    );
  }

  @Test
  void shouldHandleEmptyIngredientsList() {

    CreateRecipeDto request = new CreateRecipeDto();
    request.setIngredients(List.of());

    service.createRecipeWithIngredients(request);

    verify(ingredientRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenProductNotFoundInAddIngredients() {

    RecipeIngredientRequestDto dto = new RecipeIngredientRequestDto();
    dto.setProductId(1L);
    dto.setQuantity(2.0);

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(new Recipe()));

    when(productRepository.findById(1L))
        .thenReturn(Optional.empty());

    List<RecipeIngredientRequestDto> list = List.of(dto);

    assertThrows(ResourceNotFoundException.class, () ->
        service.addIngredients(1L, list)
    );
  }

  @Test
  void shouldThrowWhenDishNotFoundInUpdate() {

    RecipeDto dto = new RecipeDto();
    dto.setDishId(1L);

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(new Recipe()));

    when(dishRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.updateRecipe(1L, dto)
    );
  }

  @Test
  void shouldThrowWhenRecipeNotFoundInUpdate() {
    Long id = 1L;

    RecipeDto dto = new RecipeDto();
    dto.setName("test");
    dto.setInstructions("test");

    when(recipeRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> service.updateRecipe(id, dto));
  }

  @Test
  void shouldSkipIngredientsWhenNull_addIngredients() {
    Recipe recipeNull = new Recipe();
    recipeNull.setId(1L);

    when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipeNull));

    Recipe result = service.addIngredients(1L, null);

    assertNotNull(result);
    verifyNoInteractions(ingredientRepository);
  }

  @Test
  void shouldDeleteRecipeWithoutDish() {
    Recipe recipeWithoutDish = new Recipe();
    recipeWithoutDish.setId(1L);
    recipeWithoutDish.setDish(null);

    when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipeWithoutDish));

    service.deleteRecipe(1L);

    verify(recipeRepository).delete(recipeWithoutDish);
  }

  @Test
  void shouldThrowWhenQuantityNull_toIngredient() {

    Recipe recipeNull = new Recipe();

    RecipeIngredientRequestDto dto = new RecipeIngredientRequestDto();
    dto.setProductId(1L);
    dto.setQuantity(null);

    assertThrows(InvalidDataException.class,
        () -> invokeToIngredient(recipeNull, dto));
  }

  private Ingredient invokeToIngredient(Recipe recipe, RecipeIngredientRequestDto dto) {
    try {
      Method method = RecipeService.class
          .getDeclaredMethod("toIngredient", Recipe.class, RecipeIngredientRequestDto.class);

      method.setAccessible(true);

      return (Ingredient) method.invoke(service, recipe, dto);

    } catch (InvocationTargetException e) {
      throw (RuntimeException) e.getTargetException();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
