package com.cafe.task;

public class TaskStatus {

  private String id;
  private Status status;
  private String message;

  public enum Status {
    CREATED,
    RUNNING,
    DONE,
    FAILED
  }

  public TaskStatus() {
  }

  public TaskStatus(String id, Status status, String message) {
    this.id = id;
    this.status = status;
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}