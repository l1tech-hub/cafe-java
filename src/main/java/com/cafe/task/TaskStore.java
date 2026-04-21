package com.cafe.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TaskStore {

  private final Map<String, TaskStatus> tasks = new ConcurrentHashMap<>();

  public void save(TaskStatus task) {
    tasks.put(task.getId(), task);
  }

  public TaskStatus get(String id) {
    return tasks.get(id);
  }

  public void updateStatus(String id, TaskStatus.Status status) {
    TaskStatus task = tasks.get(id);
    if (task != null) {
      task.setStatus(status);
    }
  }

  public void updateMessage(String id, String message) {
    TaskStatus task = tasks.get(id);
    if (task != null) {
      task.setMessage(message);
    }
  }

  public void update(String id, TaskStatus.Status status, String message) {
    TaskStatus task = tasks.get(id);
    if (task != null) {
      task.setStatus(status);
      task.setMessage(message);
    }
  }
}
