package com.cafe.exception;

public class ServiceExecutionException extends RuntimeException {
  public ServiceExecutionException(String message, Throwable cause) {
    super(message, cause);
  }
}
