package com.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Оценка стоимости приготовления по рецепту")
public class RecipeCostEstimateDto {

  @Schema(description = "Строки по ингредиентам")
  private List<RecipeCostLineDto> lines;

  @Schema(description = "Итого, руб.")
  private Double totalCost;

  public RecipeCostEstimateDto() {
  }

  public RecipeCostEstimateDto(List<RecipeCostLineDto> lines, Double totalCost) {
    this.lines = lines;
    this.totalCost = totalCost;
  }

  public List<RecipeCostLineDto> getLines() {
    return lines;
  }

  public void setLines(List<RecipeCostLineDto> lines) {
    this.lines = lines;
  }

  public Double getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(Double totalCost) {
    this.totalCost = totalCost;
  }
}
