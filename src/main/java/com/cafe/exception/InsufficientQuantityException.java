package com.cafe.exception;

public class InsufficientQuantityException extends RuntimeException {

  private final String productName;
  private final Double missingQuantity;

  public InsufficientQuantityException(String productName, Double missingQuantity) {
    super(String.format("Missing %f of '%s'", missingQuantity, productName));
    this.productName = productName;
    this.missingQuantity = missingQuantity;
  }

  public String getProductName() {
    return productName;
  }

  public Double getMissingQuantity() {
    return missingQuantity;
  }

  public String setProductName() {
    return productName;
  }
}
