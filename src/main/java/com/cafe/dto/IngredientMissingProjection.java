package com.cafe.dto;

public interface IngredientMissingProjection {

  Long getIngredientId();

  String getProductName();

  Double getRequired();

  Double getAvailable();

  Double getMissing();

}
