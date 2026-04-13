package com.cafe.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cafe.dto.DishDto;
import com.cafe.entity.Dish;
import com.cafe.entity.Recipe;
import com.cafe.exception.InvalidDataException;
import com.cafe.exception.ResourceInUseException;
import com.cafe.exception.ResourceNotFoundException;
import com.cafe.repository.DishRepository;
import com.cafe.repository.RecipeRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DishServiceTest {

  @Mock
  private DishRepository dishRepository;

  @Mock
  private RecipeRepository recipeRepository;

  @InjectMocks
  private DishService service;

  private Dish dish;
  private Recipe recipe;
  private DishDto validDto;

  @BeforeEach
  void setUp() {
    recipe = new Recipe();
    recipe.setId(1L);

    dish = new Dish();
    dish.setId(1L);
    dish.setName("Dish");
    dish.setPrice(10.0);
    dish.setWeight(100.0);

    validDto = new DishDto();
    validDto.setName("Dish");
    validDto.setPrice(10.0);
    validDto.setWeight(100.0);
    validDto.setRecipeId(1L);
  }


  @Test
  void shouldCreateWithRecipe() {

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(recipe));

    when(dishRepository.save(any()))
        .thenReturn(dish);

    DishDto result = service.create(validDto);

    assertNotNull(result);
    verify(dishRepository).save(any());
  }

  @Test
  void shouldCreateWithoutRecipe() {

    validDto.setRecipeId(null);

    when(dishRepository.save(any()))
        .thenReturn(dish);

    DishDto result = service.create(validDto);

    assertNotNull(result);
    verify(recipeRepository, never()).findById(any());
  }

  @Test
  void shouldThrowWhenRecipeNotFoundOnCreate() {

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.create(validDto)
    );
  }

  @Test
  void shouldThrowWhenPriceInvalidOnCreate() {
    validDto.setPrice(-1.0);

    assertThrows(InvalidDataException.class, () ->
        service.create(validDto)
    );
  }

  @Test
  void shouldThrowWhenWeightInvalidOnCreate() {
    validDto.setWeight(0.0);

    assertThrows(InvalidDataException.class, () ->
        service.create(validDto)
    );
  }

  // ---------- GET ----------

  @Test
  void shouldGetById() {

    when(dishRepository.findById(1L))
        .thenReturn(Optional.of(dish));

    DishDto result = service.getById(1L);

    assertNotNull(result);
  }

  @Test
  void shouldThrowWhenDishNotFound() {

    when(dishRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.getById(1L)
    );
  }

  @Test
  void shouldGetAll() {

    when(dishRepository.findAll())
        .thenReturn(List.of(dish));

    List<DishDto> result = service.getAll();

    assertEquals(1, result.size());
  }

  // ---------- UPDATE ----------

  @Test
  void shouldUpdateWithSameRecipe() {

    dish.setRecipe(recipe);

    when(dishRepository.findById(1L))
        .thenReturn(Optional.of(dish));

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(recipe));

    when(dishRepository.save(any()))
        .thenReturn(dish);

    DishDto result = service.update(1L, validDto);

    assertNotNull(result);
  }

  @Test
  void shouldUpdateWithNewRecipe() {

    Recipe oldRecipe = new Recipe();
    oldRecipe.setId(2L);
    dish.setRecipe(oldRecipe);

    when(dishRepository.findById(1L))
        .thenReturn(Optional.of(dish));

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.of(recipe));

    when(dishRepository.save(any()))
        .thenReturn(dish);

    DishDto result = service.update(1L, validDto);

    assertNotNull(result);
    assertNull(oldRecipe.getDish());
  }

  @Test
  void shouldRemoveRecipeOnUpdate() {

    dish.setRecipe(recipe);
    validDto.setRecipeId(null);

    when(dishRepository.findById(1L))
        .thenReturn(Optional.of(dish));

    when(dishRepository.save(any()))
        .thenReturn(dish);

    DishDto result = service.update(1L, validDto);

    assertNotNull(result);
    assertNull(dish.getRecipe());
  }

  @Test
  void shouldThrowWhenDishNotFoundOnUpdate() {

    when(dishRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.update(1L, validDto)
    );
  }

  @Test
  void shouldThrowWhenRecipeNotFoundOnUpdate() {

    when(dishRepository.findById(1L))
        .thenReturn(Optional.of(dish));

    when(recipeRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.update(1L, validDto)
    );
  }


  @Test
  void shouldDelete() {

    when(dishRepository.findById(1L))
        .thenReturn(Optional.of(dish));

    service.delete(1L);

    verify(dishRepository).deleteById(1L);
  }

  @Test
  void shouldThrowWhenDishInUse() {

    dish.setRecipe(recipe);

    when(dishRepository.findById(1L))
        .thenReturn(Optional.of(dish));

    assertThrows(ResourceInUseException.class, () ->
        service.delete(1L)
    );
  }

  @Test
  void shouldThrowWhenDeleteNotFound() {

    when(dishRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        service.delete(1L)
    );
  }


  @Test
  void shouldSearchByName() {

    when(dishRepository.findByNameContainingIgnoreCase("dish"))
        .thenReturn(List.of(dish));

    List<DishDto> result = service.searchByName("dish");

    assertEquals(1, result.size());
  }


  @Test
  void shouldNotDetachWhenSameRecipe() {
    Long recipeId = 1L;

    Recipe recipe = new Recipe();
    recipe.setId(recipeId);

    Dish dish = new Dish();
    dish.setRecipe(recipe);

    DishDto dto = new DishDto();
    dto.setName("test");
    dto.setPrice(10.0);
    dto.setWeight(1.0);
    dto.setRecipeId(recipeId);

    when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(dishRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    service.update(1L, dto);

    assertEquals(recipe, dish.getRecipe());
  }

  @Test
  void shouldDoNothingWhenRecipeNullAndOldRecipeNull() {
    Dish dish = new Dish(); // без рецепта

    DishDto dto = new DishDto();
    dto.setName("test");
    dto.setPrice(10.0);
    dto.setWeight(1.0);
    dto.setRecipeId(null);

    when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
    when(dishRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    service.update(1L, dto);

    assertNull(dish.getRecipe());
  }

  @Test
  void shouldPassWhenWeightPositive_update() {
    Dish dish = new Dish();

    DishDto dto = new DishDto();
    dto.setName("test");
    dto.setPrice(10.0);
    dto.setWeight(1.0); // граница

    when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
    when(dishRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    assertDoesNotThrow(() -> service.update(1L, dto));
  }

  @Test
  void shouldCoverNullRecipeIdAndNullOldRecipe() {
    Dish dish = new Dish();

    DishDto dto = new DishDto();
    dto.setName("x");
    dto.setPrice(10.0);
    dto.setWeight(1.0);
    dto.setRecipeId(null);

    when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
    when(dishRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    service.update(1L, dto);

    assertNull(dish.getRecipe());
  }

  @Test
  void shouldCoverNullOldRecipeBranch() {
    Dish dish = new Dish();

    Recipe newRecipe = new Recipe();
    newRecipe.setId(2L);

    DishDto dto = new DishDto();
    dto.setName("x");
    dto.setPrice(10.0);
    dto.setWeight(1.0);
    dto.setRecipeId(2L);

    when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
    when(recipeRepository.findById(2L)).thenReturn(Optional.of(newRecipe));
    when(dishRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    service.update(1L, dto);

    assertEquals(newRecipe, dish.getRecipe());
  }
}

