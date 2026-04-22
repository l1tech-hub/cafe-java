package com.cafe.exception;

public class ExpiredProductsNotAllowedException extends RuntimeException {

  private final String productName;

  public ExpiredProductsNotAllowedException(String productName) {
    super(String.format(
        "Products '%s' batch is expired. Cooking from expired batches is not allowed.",
        productName));
    this.productName = productName;
  }

  public String getProductName() {
    return productName;
  }
}
