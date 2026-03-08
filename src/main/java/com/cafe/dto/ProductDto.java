package com.cafe.dto;

public class ProductDto {

  private Long id;
  private String name;
  boolean state;

  public ProductDto() {
  }

  public ProductDto(Long id, String name, boolean state) {
    this.id = id;
    this.name = name;
    this.state = state;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean getState() {
    return state;
  }

  public void setState(boolean state) {
    this.state = state;
  }
}