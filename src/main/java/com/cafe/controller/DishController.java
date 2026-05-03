package com.cafe.controller;

import com.cafe.dto.BatchOrder;
import com.cafe.dto.DishCookStatDto;
import com.cafe.dto.DishDto;
import com.cafe.dto.ProductSpentDto;
import com.cafe.service.CookingAsyncService;
import com.cafe.service.CookingMetricsService;
import com.cafe.service.DishService;
import com.cafe.task.TaskStatus;
import com.cafe.task.TaskStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dishes")
@Tag(name = "Dish", description = "Операции с блюдами")
public class DishController {

  private final DishService service;
  private final CookingAsyncService asyncService;
  private final TaskStore taskStore;
  private final CookingMetricsService metricsService;

  public DishController(DishService service, CookingAsyncService asyncService, TaskStore taskStore,
      CookingMetricsService metricsService) {
    this.service = service;
    this.asyncService = asyncService;
    this.taskStore = taskStore;
    this.metricsService = metricsService;
  }

  @Operation(summary = "Создать блюдо")
  @PostMapping
  public DishDto create(@RequestBody DishDto dto) {
    return service.create(dto);
  }

  @Operation(summary = "Получить все блюда")
  @GetMapping
  public List<DishDto> getAll() {
    return service.getAll();
  }

  @Operation(summary = "Поиск блюд по названию")
  @GetMapping("/search")
  public List<DishDto> search(@RequestParam String name) {
    return service.searchByName(name);
  }

  @Operation(summary = "Статистика приготовленных блюд (из БД)")
  @GetMapping("/cooking-statistics")
  public List<DishCookStatDto> getCookingStatistics() {
    return metricsService.listCookStatistics();
  }

  @Operation(summary = "Потраченные продукты (кг) по рецептам и числу приготовлений")
  @GetMapping("/spent-products")
  public List<ProductSpentDto> getSpentProductsKilograms() {
    return metricsService.listSpentProductsKilograms();
  }

  @GetMapping("/race-demo")
  public String raceConditionDemo(@RequestParam(defaultValue = "1000") int tasks)
      throws InterruptedException {

    Long dishId = 1L;

    try (ExecutorService executor = Executors.newFixedThreadPool(50)) {

      for (int i = 0; i < tasks; i++) {
        executor.submit(() -> {
          metricsService.unsafeIncrement(dishId);
          metricsService.increment(dishId);
        });
      }

      executor.shutdown();
      if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        executor.shutdownNow();
      }
    }

    int unsafe = metricsService.getUnsafe(dishId);
    int safe = metricsService.getCount(dishId);

    return "Expected=" + tasks + ", unsafe=" + unsafe + ", safe=" + safe;
  }

  @Operation(summary = "Получить статус задачи приготовления")
  @GetMapping("/tasks/{taskId}")
  public TaskStatus getTaskStatus(@PathVariable String taskId) {
    return taskStore.get(taskId);
  }

  @Operation(summary = "Получить блюдо по ID")
  @GetMapping("/{id:\\d+}")
  public DishDto getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @Operation(summary = "Обновить блюдо")
  @PutMapping("/{id:\\d+}")
  public DishDto update(@PathVariable Long id, @RequestBody DishDto dto) {
    return service.update(id, dto);
  }

  @Operation(summary = "Удалить блюдо")
  @DeleteMapping("/{id:\\d+}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @Operation(summary = "Запустить приготовление блюда (асинхронно)")
  @PostMapping("/{id:\\d+}/cook")
  public String cook(@PathVariable Long id,
      @RequestParam(defaultValue = "false") boolean allowExpiredProducts,
      @RequestParam(defaultValue = "EXPIRY_ASC") BatchOrder batchOrder) {

    String taskId = UUID.randomUUID().toString();

    TaskStatus task = new TaskStatus(taskId, TaskStatus.Status.CREATED, null);
    taskStore.save(task);

    asyncService.cookAsync(taskId, id, allowExpiredProducts, batchOrder);

    return taskId;
  }
}
