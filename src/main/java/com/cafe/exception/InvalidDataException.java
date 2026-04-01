package com.cafe.exception;

public class InvalidDataException extends RuntimeException {

  private final String field;
  private final Object value;

  public InvalidDataException(String field, Object value, String message) {
    super(String.format("Invalid value for '%s': %s (%s)", field, value, message));
    this.field = field;
    this.value = value;
  }

  public String getField() {
    return field;
  }

  public Object getValue() {
    return value;
  }
}
