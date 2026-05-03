package com.cafe.service;

import com.cafe.task.TaskStatus;
import com.cafe.task.TaskStore;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CookingAsyncService {

  private final DishService dishService;
  private final TaskStore taskStore;

  public CookingAsyncService(DishService dishService, TaskStore taskStore) {
    this.dishService = dishService;
    this.taskStore = taskStore;
  }

  @Async
  public CompletableFuture<Void> cookAsync(String taskId,
      Long dishId,
      boolean allowExpiredProducts) {

    TaskStatus task = taskStore.get(taskId);
    task.setStatus(TaskStatus.Status.RUNNING);

    try {
      Thread.sleep(12000);

      dishService.cook(dishId, allowExpiredProducts);

      task.setStatus(TaskStatus.Status.DONE);
      task.setMessage("Cooking completed");

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      task.setStatus(TaskStatus.Status.FAILED);
      task.setMessage("Task was interrupted");

    } catch (Exception e) {
      task.setStatus(TaskStatus.Status.FAILED);
      task.setMessage(e.getMessage());
    }

    return CompletableFuture.completedFuture(null);
  }
}
