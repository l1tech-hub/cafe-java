package com.cafe.exception;

public class ResourceInUseException extends RuntimeException {

  private final String resourceName;
  private final String operation;
  private final String reason;

  public ResourceInUseException(String resourceName, String operation, String reason) {
    super(String.format("Cannot %s %s: %s", operation, resourceName, reason));
    this.resourceName = resourceName;
    this.operation = operation;
    this.reason = reason;
  }

  public String getResourceName() {
    return resourceName;
  }

  public String getOperation() {
    return operation;
  }

  public String getReason() {
    return reason;
  }
}
